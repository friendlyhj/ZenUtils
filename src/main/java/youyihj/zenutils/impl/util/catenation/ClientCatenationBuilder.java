package youyihj.zenutils.impl.util.catenation;

import youyihj.zenutils.api.util.catenation.*;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * @author youyihj
 */
public class ClientCatenationBuilder implements ICatenationBuilder {
    private final Queue<ICatenationTask> tasks = new ArrayDeque<>();
    private IWorldCondition stopWhen;

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
    public Catenation start() {
        Catenation catenation = new Catenation(tasks, stopWhen);
        CatenationManager.addClientCatenation(catenation);
        return catenation;
    }
}
