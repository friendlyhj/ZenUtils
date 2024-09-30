package youyihj.zenutils.impl.mixin.itf;

import java.util.List;

/**
 * @author youyihj
 */
public interface IMixinTargetEnvironment {
    List<String> getTargets();

    void setTargets(List<String> targets);
}
