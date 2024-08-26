package youyihj.zenutils.impl.mixin.vanilla;

import org.apache.commons.lang3.ArrayUtils;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import youyihj.zenutils.Reference;
import youyihj.zenutils.api.util.ReflectionInvoked;
import youyihj.zenutils.impl.core.Configuration;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author youyihj
 */
@ReflectionInvoked
public class VanillaMixinPlugin implements IMixinConfigPlugin {

    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return "";
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (Reference.IS_CLEANROOM) {
            return !(ArrayUtils.contains(new String[]{
                    "youyihj.zenutils.impl.mixin.vanilla.MixinAnvilUpdateEvent",
                    "youyihj.zenutils.impl.mixin.vanilla.ContainerRepairAccessor",
                    "youyihj.zenutils.impl.mixin.vanilla.MixinForgeHooks",
                    "youyihj.zenutils.impl.mixin.vanilla.MixinForgeLoadController"
            }, mixinClassName));
        } else {
            return !"youyihj.zenutils.impl.mixin.vanilla.MixinCleanroomLoadController".equals(mixinClassName);
        }
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
        if (!Configuration.enableMixin) {
            myTargets.remove("net.minecraftforge.fml.common.LoadController");
        }
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
