package youyihj.zenutils.impl.util.catenation;

import crafttweaker.api.world.IWorld;
import youyihj.zenutils.api.util.catenation.CatenationContext;
import youyihj.zenutils.api.util.catenation.ICatenationTask;
import youyihj.zenutils.api.util.catenation.IWorldFunction;

/**
 * @author youyihj
 */
public class InstantTask implements ICatenationTask {
    private final IWorldFunction worldFunction;
    private boolean ran;

    public InstantTask(IWorldFunction worldFunction) {
        this.worldFunction = worldFunction;
    }

    @Override
    public void run(IWorld world, CatenationContext context) {
        worldFunction.apply(world, context);
        ran = true;
    }

    @Override
    public boolean isComplete() {
        return ran;
    }
}
