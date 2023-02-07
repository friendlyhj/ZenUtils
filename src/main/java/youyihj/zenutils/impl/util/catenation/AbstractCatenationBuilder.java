package youyihj.zenutils.impl.util.catenation;

import youyihj.zenutils.api.util.catenation.*;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.Queue;

/**
 * @author youyihj
 */
public abstract class AbstractCatenationBuilder implements ICatenationBuilder {
    protected final Queue<ICatenationTask> tasks = new ArrayDeque<>();
    @Nullable
    protected IWorldCondition stopWhen;
    @Nullable
    protected IWorldFunction onStop;

    @Override
    public ICatenationBuilder addTask(ICatenationTask task) {
        tasks.add(task);
        return this;
    }

    @Override
    public ICatenationBuilder run(IWorldFunction function) {
        return addTask(new InstantTask(function));
    }

    @Override
    public ICatenationBuilder sleep(long ticks) {
        return addTask(new SleepTask(ticks));
    }

    @Override
    public ICatenationBuilder sleepUntil(IWorldCondition condition) {
        return addTask(new SleepUntilTask(condition));
    }

    @Override
    public ICatenationBuilder stopWhen(IWorldCondition condition) {
        stopWhen = condition;
        return this;
    }

    @Override
    public ICatenationBuilder onStop(IWorldFunction function) {
        onStop = function;
        return this;
    }

    protected Catenation build() {
        return new Catenation(tasks, stopWhen, onStop);
    }

    protected abstract void register(Catenation catenation);

    @Override
    public Catenation start() {
        Catenation catenation = build();
        register(catenation);
        return catenation;
    }
}
