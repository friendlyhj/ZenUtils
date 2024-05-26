package youyihj.zenutils.impl.util.catenation;

import youyihj.zenutils.api.util.catenation.*;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * @author youyihj
 */
public class CatenationTaskQueueBuilder implements ICatenationTaskQueueBuilder {
    private final Queue<ICatenationTask> tasks = new ArrayDeque<>();

    @Override
    public ICatenationTaskQueueBuilder addTask(ICatenationTask task) {
        tasks.add(task);
        return this;
    }

    @Override
    public Queue<ICatenationTask> build() {
        return tasks;
    }

    @Override
    public ICatenationTaskQueueBuilder run(IWorldFunction function) {
        return addTask(new InstantTask(function));
    }

    @Override
    public ICatenationTaskQueueBuilder sleep(long ticks) {
        return addTask(new SleepTask(ticks));
    }

    @Override
    public ICatenationTaskQueueBuilder sleepUntil(IWorldCondition condition) {
        return addTask(new SleepUntilTask(condition));
    }

    @Override
    public ICatenationTaskQueueBuilder customTimer(long duration, ITimerHandler handler) {
        return addTask(new TimerTask(duration, handler));
    }

    @Override
    public ICatenationTaskQueueBuilder repeat(int times, ICatenationTaskQueueBuilderConsumer taskQueueBuilderConsumer) {
        return addTask(new RepeatTask(taskQueueBuilderConsumer, times));
    }
}
