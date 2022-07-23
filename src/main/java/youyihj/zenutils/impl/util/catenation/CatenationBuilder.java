package youyihj.zenutils.impl.util.catenation;

import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.world.IWorld;
import youyihj.zenutils.api.util.catenation.*;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @author youyihj
 */
public class CatenationBuilder implements ICatenationBuilder {
    private final IWorld world;
    private final Queue<ICatenationTask> tasks = new LinkedList<>();
    private IWorldCondition stopWhen;

    public CatenationBuilder(IWorld world) {
        this.world = world;
    }

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
        CatenationManager.catenations.put(CraftTweakerMC.getWorld(world), catenation);
        return catenation;
    }
}
