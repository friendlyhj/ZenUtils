package youyihj.zenutils.impl.util.catenation;

import crafttweaker.api.data.IData;
import youyihj.zenutils.api.util.catenation.*;

import javax.annotation.Nullable;

/**
 * @author youyihj
 */
public abstract class AbstractCatenationBuilder implements ICatenationBuilder {
    protected final ICatenationTaskQueueBuilder taskQueueBuilder = new CatenationTaskQueueBuilder();
    @Nullable
    protected IWorldCondition stopWhen;
    @Nullable
    protected IWorldFunction onStop;
    protected IData data;

    @Override
    public ICatenationBuilder addTask(ICatenationTask task) {
        taskQueueBuilder.addTask(task);
        return this;
    }

    @Override
    public ICatenationBuilder run(IWorldFunction function) {
        taskQueueBuilder.run(function);
        return this;
    }

    @Override
    public ICatenationBuilder sleep(long ticks) {
        taskQueueBuilder.sleep(ticks);
        return this;
    }

    @Override
    public ICatenationBuilder sleepUntil(IWorldCondition condition) {
        taskQueueBuilder.sleepUntil(condition);
        return this;
    }

    @Override
    public ICatenationBuilder customTimer(long duration, ITimerHandler handler) {
        taskQueueBuilder.customTimer(duration, handler);
        return this;
    }

    @Override
    public ICatenationBuilder repeat(int times, ICatenationTaskQueueBuilderConsumer builderConsumer) {
        taskQueueBuilder.repeat(times, builderConsumer);
        return this;
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

    @Override
    public ICatenationBuilder data(IData data) {
        this.data = data;
        return this;
    }

    protected Catenation build() {
        Catenation catenation = new Catenation(taskQueueBuilder.build(), stopWhen, onStop);
        if (data != null) {
            catenation.getContext().setData(data);
        }
        return catenation;
    }

    protected abstract void register(Catenation catenation);

    @Override
    public Catenation start() {
        Catenation catenation = build();
        register(catenation);
        return catenation;
    }
}
