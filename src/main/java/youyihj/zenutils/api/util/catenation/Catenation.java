package youyihj.zenutils.api.util.catenation;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.world.IWorld;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import youyihj.zenutils.api.util.catenation.persistence.ICatenationObjectHolder;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.Queue;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.Catenation")
public class Catenation {
    private final Queue<ICatenationTask> tasks;
    @Nullable
    private final IWorldCondition stopWhen;
    private final CatenationContext context;

    private IWorld world;
    @Nullable
    private String persistenceKey;

    public Catenation(Queue<ICatenationTask> tasks, @Nullable IWorldCondition stopWhen, @Nullable IWorldFunction onStop) {
        this.tasks = tasks;
        this.stopWhen = stopWhen;
        this.context = new CatenationContext(this, onStop);
    }

    @ZenMethod
    public boolean tick(IWorld world) {
        setWorld(world);
        if (isStopped()) {
            return true;
        }
        context.checkObjectHolders();
        if (context.getStatus() == CatenationStatus.WORKING) {
            if (stopWhen != null) {
                try {
                    if (stopWhen.apply(world, context)) {
                        context.setStatus(CatenationStatus.STOP_INTERNAL, world);
                    }
                } catch (Throwable exception) {
                    CraftTweakerAPI.logError("Exception occurred in stopWhen function, stopping the catenation...", exception);
                    context.setStatus(CatenationStatus.ERROR, world);
                }
            }
            if (isStopped()) {
                return true;
            }
            ICatenationTask task = tasks.peek();
            if (task == null) {
                context.setStatus(CatenationStatus.FINISH, world);
                return true;
            }
            try {
                task.run(world, context);
            } catch (Throwable exception) {
                CraftTweakerAPI.logError("Exception occurred in a catenation task, stopping the catenation...", exception);
                context.setStatus(CatenationStatus.ERROR, world);
                return true;
            }
            if (task.isComplete()) {
                tasks.poll();
            }

        }
        return false;
    }

    @ZenMethod
    public void stop() {
        context.setStatus(CatenationStatus.STOP_MANUAL, world);
    }

    @ZenMethod
    public void pause() {
        context.setStatus(CatenationStatus.PAUSE, world);
    }

    @ZenMethod
    public void play() {
        context.setStatus(CatenationStatus.WORKING, world);
    }

    @ZenGetter("stopped")
    @ZenMethod
    public boolean isStopped() {
        return context.getStatus().isStop();
    }

    @ZenGetter("context")
    @ZenMethod
    public CatenationContext getContext() {
        return context;
    }

    // not exposed
    public IWorld getWorld() {
        return world;
    }

    public void setWorld(IWorld world) {
        this.world = world;
    }

    public Queue<ICatenationTask> getTasks() {
        return tasks;
    }

    public void setPersistenceKey(@Nullable String persistenceKey) {
        this.persistenceKey = persistenceKey;
    }

    @Nullable
    public String getPersistenceKey() {
        return persistenceKey;
    }

    public boolean isAllObjectsValid() {
        return validateObjectHolders().isValid();
    }

    public ICatenationObjectHolder.ValidationResult validateObjectHolders() {
        return this.getContext().getObjectHolders().values().stream()
                .map(holder -> {
                    ICatenationObjectHolder.ValidationResult result = holder.validate(this);
                    if (!result.isValid()) {
                        holder.invalidate();
                    }
                    return result;
                })
                .max(Comparator.naturalOrder())
                .orElse(ICatenationObjectHolder.ValidationResult.VALID);
    }
}
