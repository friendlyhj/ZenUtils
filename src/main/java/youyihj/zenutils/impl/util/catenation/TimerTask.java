package youyihj.zenutils.impl.util.catenation;

import crafttweaker.api.data.DataLong;
import crafttweaker.api.data.IData;
import crafttweaker.api.world.IWorld;
import youyihj.zenutils.api.util.catenation.CatenationContext;
import youyihj.zenutils.api.util.catenation.ICatenationTask;
import youyihj.zenutils.api.util.catenation.ITimerHandler;
import youyihj.zenutils.api.util.catenation.Timer;

/**
  * @author youyihj
  */
public class TimerTask implements ICatenationTask {
    private final Timer timer;
    private final ITimerHandler handler;

    public TimerTask(long duration, ITimerHandler handler) {
        this.timer = new Timer(duration);
        this.handler = handler;
    }

    @Override
    public void run(IWorld world, CatenationContext context) {
        handler.apply(timer, world, context);
    }

    @Override
    public boolean isComplete() {
        return timer.isFinish();
    }

    @Override
    public IData serializeToData() {
        return new DataLong(timer.getCurrent());
    }

    @Override
    public void deserializeFromData(IData data) {
        timer.setCurrent(data.asLong());
    }
}
