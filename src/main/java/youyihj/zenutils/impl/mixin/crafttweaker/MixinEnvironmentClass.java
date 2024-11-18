package youyihj.zenutils.impl.mixin.crafttweaker;

import org.spongepowered.asm.mixin.Mixin;
import stanhebben.zenscript.compiler.EnvironmentClass;
import youyihj.zenutils.impl.mixin.itf.IEnvironmentClassExtension;
import youyihj.zenutils.impl.zenscript.nat.ZenTypeJavaNative;

import java.util.Collections;
import java.util.List;

/**
 * @author youyihj
 */
@Mixin(EnvironmentClass.class)
public abstract class MixinEnvironmentClass implements IEnvironmentClassExtension {
    private List<String> targets;
    private List<ZenTypeJavaNative> subClasses;

    @Override
    public List<String> getMixinTargets() {
        return targets == null ? Collections.emptyList() : targets;
    }

    @Override
    public void setMixinTargets(List<String> targets) {
        this.targets = targets;
    }

    @Override
    public List<ZenTypeJavaNative> getSuperClasses() {
        return subClasses;
    }

    @Override
    public void setSubClasses(List<ZenTypeJavaNative> superClasses) {
        this.subClasses = superClasses;
    }
}
