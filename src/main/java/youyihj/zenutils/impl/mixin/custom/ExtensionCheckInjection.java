package youyihj.zenutils.impl.mixin.custom;

import crafttweaker.CraftTweakerAPI;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.transformer.ext.IExtension;
import org.spongepowered.asm.mixin.transformer.ext.ITargetClassContext;
import org.spongepowered.asm.mixin.transformer.meta.MixinMerged;
import org.spongepowered.asm.util.Annotations;
import youyihj.zenutils.impl.zenscript.mixin.MixinZSHandler;

import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author youyihj
 */
public class ExtensionCheckInjection implements IExtension {
    @Override
    public boolean checkActive(MixinEnvironment environment) {
        return true;
    }

    @Override
    public void preApply(ITargetClassContext context) {

    }

    @Override
    public void postApply(ITargetClassContext context) {
        ClassNode targetClass = context.getClassNode();
        List<MethodNode> standardMethods = targetClass.methods.stream()
                .filter(m -> Annotations.getVisible(m, MixinZSHandler.class) == null)
                .collect(Collectors.toList());
        Map<MethodNode, Boolean> handlerCheckResults = targetClass.methods.stream()
                .filter(m -> Annotations.getVisible(m, MixinZSHandler.class) != null)
                .filter(m -> Annotations.getVisible(m, MixinMerged.class) != null)
                .collect(Collectors.toMap(Function.identity(), m -> calledInMethods(m, standardMethods)));

        for (Map.Entry<MethodNode, Boolean> entry : handlerCheckResults.entrySet()) {
            MethodNode method = entry.getKey();
            Boolean result = entry.getValue();
            if (!result) {
                for (MethodNode otherMethod : handlerCheckResults.keySet()) {
                    if (otherMethod.name.startsWith(method.name + "$mixinextras$bridge$")) {
                        result = handlerCheckResults.get(otherMethod);
                        break;
                    }
                }
            }
            if (!result) {
                reportUnusedHandler(method);
            }
        }
    }

    @Override
    public void export(MixinEnvironment env, String name, boolean force, ClassNode classNode) {

    }

    private boolean calledInMethods(MethodNode method, List<MethodNode> searchScopes) {
        for (MethodNode scope : searchScopes) {
            ListIterator<AbstractInsnNode> instructions = scope.instructions.iterator();
            while (instructions.hasNext()) {
                AbstractInsnNode insn = instructions.next();
                if (insn instanceof MethodInsnNode) {
                    MethodInsnNode methodInsn = (MethodInsnNode) insn;
                    if (methodInsn.name.equals(method.name) && methodInsn.desc.equals(method.desc)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void reportUnusedHandler(MethodNode handlerMethod) {
        String mixinClassName = Annotations.getValue(Annotations.getVisible(handlerMethod, MixinMerged.class), "mixin");
        String simpleClassName = mixinClassName.substring(mixinClassName.lastIndexOf('.') + 1);
        String simpleHandlerName = handlerMethod.name.substring(handlerMethod.name.lastIndexOf('$') + 1);
        CraftTweakerAPI.logError("Mixin handler " + simpleHandlerName + handlerMethod.desc + " in " + simpleClassName + " does not hit any injection point.");
    }
}
