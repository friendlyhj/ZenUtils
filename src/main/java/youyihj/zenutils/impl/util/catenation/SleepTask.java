package youyihj.zenutils.impl.util.catenation;

import crafttweaker.api.world.IWorld;
import youyihj.zenutils.api.util.catenation.ICatenationTask;

/**
 * @author youyihj
 */
public class SleepTask implements ICatenationTask {
    private long timer;
    private final long sleepTime;

    public SleepTask(long sleepTime) {
        this.sleepTime = sleepTime;
    }

    @Override
    public void run(IWorld world) {
        timer++;
    }

    @Override
    public boolean isComplete() {
        return timer >= sleepTime;
    }
}
