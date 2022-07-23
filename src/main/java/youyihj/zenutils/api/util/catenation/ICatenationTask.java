package youyihj.zenutils.api.util.catenation;

import crafttweaker.api.world.IWorld;

/**
 * @author youyihj
 */
public interface ICatenationTask {
    void run(IWorld world);

    boolean isComplete();
}
