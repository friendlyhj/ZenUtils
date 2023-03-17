package youyihj.zenutils.api.util.catenation;

import crafttweaker.api.data.IData;
import crafttweaker.api.world.IWorld;

/**
 * @author youyihj
 */
public interface ICatenationTask {
    void run(IWorld world, CatenationContext context);

    boolean isComplete();

    IData serializeToData();

    void deserializeFromData(IData data);
}
