package youyihj.zenutils.impl.mixin.itf;

import youyihj.zenutils.impl.zenscript.nat.ZenTypeJavaNative;

import java.util.List;

/**
 * @author youyihj
 */
public interface IEnvironmentClassExtension {
    List<String> getMixinTargets();

    void setMixinTargets(List<String> targets);

    List<ZenTypeJavaNative> getSuperClasses();

    void setSubClasses(List<ZenTypeJavaNative> superClasses);
}
