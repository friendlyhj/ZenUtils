package youyihj.zenutils.impl.mixin.zenbootstrap;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import youyihj.zenutils.Reference;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author youyihj
 */
public class ZenMixinBootstrapPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return "";
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return Reference.IS_CLEANROOM
                ? "youyihj.zenutils.impl.mixin.zenbootstrap.MixinCleanroomLoadController".equals(mixinClassName)
                : "youyihj.zenutils.impl.mixin.zenbootstrap.MixinForgeLoadController".equals(mixinClassName);
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return Collections.emptyList();
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
