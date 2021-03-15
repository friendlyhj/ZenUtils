package youyihj.zenutils.util.delay;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IWorld;
import stanhebben.zenscript.annotations.ZenClass;

@ZenRegister
@ZenClass("mods.zenutils.IPositionDelayWork")
@FunctionalInterface
public interface IPositionDelayWork {
    void work(IWorld thisWorld, IBlockPos thisPos);

    class Worker {
        private final IPositionDelayWork work;
        private final IWorld world;
        private final IBlockPos pos;

        public Worker(IPositionDelayWork work, IWorld world, IBlockPos pos) {
            this.work = work;
            this.world = world;
            this.pos = pos;
        }

        public void work() {
            work.work(world, pos);
        }

        public boolean isTargetedAt(IWorld other) {
            return other.getInternal() == world.getInternal();
        }
    }
}
