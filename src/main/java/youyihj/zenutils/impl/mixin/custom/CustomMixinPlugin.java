package youyihj.zenutils.impl.mixin.custom;

import crafttweaker.CraftTweakerAPI;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.transformer.meta.MixinMerged;
import org.spongepowered.asm.util.Annotations;
import youyihj.zenutils.impl.zenscript.mixin.MixinZSHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author youyihj
 */
public class CustomMixinPlugin implements IMixinConfigPlugin {
    private static final List<String> mixins = new ArrayList<>();

    public static void addMixinClass(String mixin) {
        mixins.add(mixin);
    }

    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return "";
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return false;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return mixins;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        CraftTweakerAPI.logInfo("Applying mixin class: " + mixinClassName.substring(mixinClassName.lastIndexOf('.') + 1) + ", target: " + targetClassName);
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        List<MethodNode> standardMethods = targetClass.methods.stream()
                .filter(m -> !isAnnotated(m, MixinMerged.class))
                .collect(Collectors.toList());
        List<MethodNode> handlerMethods = targetClass.methods.stream()
                .filter(m -> isAnnotated(m, MixinZSHandler.class))
                .filter(m -> isAnnotated(m, MixinMerged.class))
                .collect(Collectors.toList());
        for (MethodNode handlerMethod : handlerMethods) {
            if (!mixinClassName.equals(Annotations.getValue(Annotations.getVisible(handlerMethod, MixinMerged.class), "mixin"))) {
                continue;
            }
            boolean found = false;
            for (MethodNode standardMethod : standardMethods) {
                ListIterator<AbstractInsnNode> instructions = standardMethod.instructions.iterator();
                while (instructions.hasNext()) {
                    AbstractInsnNode insn = instructions.next();
                    if (insn instanceof MethodInsnNode) {
                        MethodInsnNode methodInsn = (MethodInsnNode) insn;
                        if (methodInsn.name.equals(handlerMethod.name) && methodInsn.desc.equals(handlerMethod.desc)) {
                            found = true;
                            break;
                        }
                    }
                }
            }
            if (!found) {
                String simpleClassName = mixinClassName.substring(mixinClassName.lastIndexOf('.') + 1);
                String simpleHandlerName = handlerMethod.name.substring(handlerMethod.name.lastIndexOf('$') + 1);
                CraftTweakerAPI.logError("Mixin handler " + simpleHandlerName + handlerMethod.desc + " in " + simpleClassName + " does not hit any injection point.");
            }
        }
    }

    private boolean isAnnotated(MethodNode methodNode, Class<?> annotationClass) {
        String descriptor = Type.getDescriptor(annotationClass);
        if (methodNode.visibleAnnotations != null) {
            return methodNode.visibleAnnotations.stream().anyMatch(a -> a.desc.equals(descriptor));
        }
        return false;
    }
}
