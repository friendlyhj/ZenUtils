package youyihj.zenutils.impl.util.catenation;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;
import crafttweaker.api.data.DataIntArray;
import crafttweaker.api.data.IData;
import crafttweaker.api.world.IWorld;
import youyihj.zenutils.api.util.catenation.CatenationContext;
import youyihj.zenutils.api.util.catenation.ICatenationTask;
import youyihj.zenutils.api.util.catenation.ICatenationTaskQueueBuilder;
import youyihj.zenutils.api.util.catenation.ICatenationTaskQueueBuilderConsumer;

/**
 * @author youyihj
 */
public class RepeatTask implements ICatenationTask {
    private final ICatenationTaskQueueBuilderConsumer taskQueueBuilderConsumer;
    private final int times;
    private int hasRepeated;
    private int currentTaskIndex;
    private PeekingIterator<ICatenationTask> tasksInThisCycle;

    public RepeatTask(ICatenationTaskQueueBuilderConsumer taskQueueBuilderConsumer, int times) {
        Preconditions.checkArgument(times > 0, "times must be greater than 0");
        this.taskQueueBuilderConsumer = taskQueueBuilderConsumer;
        this.times = times;
        initTaskCycle();
    }

    @Override
    public void run(IWorld world, CatenationContext context) {
        if (tasksInThisCycle.hasNext()) {
            ICatenationTask task = tasksInThisCycle.peek();
            task.run(world, context);
            if (task.isComplete()) {
                currentTaskIndex++;
                tasksInThisCycle.next();
            }
        } else if (!this.isComplete()) {
            hasRepeated++;
            currentTaskIndex = 0;
            initTaskCycle();
            run(world, context);
        }
    }

    @Override
    public boolean isComplete() {
        return hasRepeated >= times;
    }

    @Override
    public IData serializeToData() {
        return new DataIntArray(new int[]{currentTaskIndex, hasRepeated}, true);
    }

    @Override
    public void deserializeFromData(IData data) {
        int[] intArray = data.asIntArray();
        currentTaskIndex = intArray[0];
        hasRepeated = intArray[1];
        Iterators.advance(tasksInThisCycle, currentTaskIndex);
    }

    private void initTaskCycle() {
        ICatenationTaskQueueBuilder taskQueueBuilder = new CatenationTaskQueueBuilder();
        taskQueueBuilderConsumer.apply(taskQueueBuilder);
        tasksInThisCycle = Iterators.peekingIterator(taskQueueBuilder.build().iterator());
    }
}
