package youyihj.zenutils.impl.util.catenation;

import com.google.common.base.Preconditions;
import crafttweaker.api.data.DataIntArray;
import crafttweaker.api.data.DataMap;
import crafttweaker.api.data.IData;
import crafttweaker.api.world.IWorld;
import youyihj.zenutils.api.util.catenation.CatenationContext;
import youyihj.zenutils.api.util.catenation.ICatenationTask;
import youyihj.zenutils.api.util.catenation.ICatenationTaskQueueBuilder;
import youyihj.zenutils.api.util.catenation.ICatenationTaskQueueBuilderConsumer;

import java.util.HashMap;
import java.util.Queue;

/**
 * @author youyihj
 */
public class RepeatTask implements ICatenationTask {
    private final ICatenationTaskQueueBuilderConsumer taskQueueBuilderConsumer;
    private final int times;
    private int hasRepeated;
    private int currentTaskIndex;
    private Queue<ICatenationTask> tasksInThisCycle;

    public RepeatTask(ICatenationTaskQueueBuilderConsumer taskQueueBuilderConsumer, int times) {
        Preconditions.checkArgument(times > 0, "times must be greater than 0");
        this.taskQueueBuilderConsumer = taskQueueBuilderConsumer;
        this.times = times;
        initTaskCycle();
    }

    @Override
    public void run(IWorld world, CatenationContext context) {
        if (!tasksInThisCycle.isEmpty()) {
            ICatenationTask task = tasksInThisCycle.peek();
            task.run(world, context);
            if (task.isComplete()) {
                currentTaskIndex++;
                tasksInThisCycle.poll();
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
        DataIntArray state = new DataIntArray(new int[]{currentTaskIndex, hasRepeated}, true);
        DataMap data = new DataMap(new HashMap<>(), false);
        if (tasksInThisCycle.peek() != null) {
            data.memberSet("task", tasksInThisCycle.peek().serializeToData());
        }
        data.memberSet("state", state);
        return data;
    }

    @Override
    public void deserializeFromData(IData data) {
        int[] state = data.memberGet("state").asIntArray();
        currentTaskIndex = state[0];
        hasRepeated = state[1];
        for (int i = 0; i < currentTaskIndex; i++) {
            tasksInThisCycle.poll();
        }
        IData taskData = data.memberGet("task");
        if (taskData != null && tasksInThisCycle.peek() != null) {
            tasksInThisCycle.peek().deserializeFromData(taskData);
        }
    }

    private void initTaskCycle() {
        ICatenationTaskQueueBuilder taskQueueBuilder = new CatenationTaskQueueBuilder();
        taskQueueBuilderConsumer.apply(taskQueueBuilder);
        tasksInThisCycle = taskQueueBuilder.build();
    }
}
