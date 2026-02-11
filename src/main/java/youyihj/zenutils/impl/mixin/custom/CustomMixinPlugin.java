package youyihj.zenutils.impl.mixin.custom;

import crafttweaker.CraftTweakerAPI;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    }

    private boolean isAnnotated(MethodNode methodNode, Class<?> annotationClass) {
        String descriptor = Type.getDescriptor(annotationClass);
        if (methodNode.visibleAnnotations != null) {
            return methodNode.visibleAnnotations.stream().anyMatch(a -> a.desc.equals(descriptor));
        }
        return false;
    }
}
