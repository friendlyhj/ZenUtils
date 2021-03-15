package youyihj.zenutils.util.delay;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.player.IPlayer;
import stanhebben.zenscript.annotations.ZenClass;

@ZenRegister
@ZenClass("mods.zenutils.IPlayerDelayWork")
@FunctionalInterface
public interface IPlayerDelayWork {
    void work(IPlayer thisPlayer);

    class Worker {
        private final IPlayerDelayWork work;
        private final IPlayer player;

        public Worker(IPlayerDelayWork work, IPlayer player) {
            this.work = work;
            this.player = player;
        }

        public void work() {
            work.work(this.player);
        }

        public boolean isTargetedAt(IPlayer other) {
            return player.equals(other);
        }
    }
}
