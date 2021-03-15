package youyihj.zenutils.util.delay;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.event.PlayerTickEvent;
import crafttweaker.api.event.WorldTickEvent;
import crafttweaker.util.IEventHandler;
import org.apache.commons.lang3.tuple.Pair;

import java.util.function.Predicate;

public class DelayHandler {
    private static final PlayerTickHandler PLAYER_TICK_HANDLER = new PlayerTickHandler();
    private static final WorldTickHandler WORLD_TICK_HANDLER = new WorldTickHandler();

    public static void init() {
        CraftTweakerAPI.events.onPlayerTick(PLAYER_TICK_HANDLER);
        CraftTweakerAPI.events.onWorldTick(WORLD_TICK_HANDLER);
    }


    private static class PlayerTickHandler implements IEventHandler<PlayerTickEvent> {
        @Override
        public void handle(PlayerTickEvent event) {
            Predicate<Pair<IPlayerDelayWork.Worker, Long>> pairPredicate = (pair) ->
                pair.getRight() >= event.getPlayer().getWorld().getWorldTime() && pair.getLeft().isTargetedAt(event.getPlayer());
            DelayManager.PLAYER_DELAY_WORKS.stream().filter(pairPredicate).map(Pair::getLeft).forEach(IPlayerDelayWork.Worker::work);
            DelayManager.PLAYER_DELAY_WORKS.removeIf(pairPredicate);
        }
    }

    private static class WorldTickHandler implements IEventHandler<WorldTickEvent> {
        @Override
        public void handle(WorldTickEvent event) {
            Predicate<Pair<IPositionDelayWork.Worker, Long>> pairPredicate = (pair) ->
                    pair.getRight() >= event.getWorld().getWorldTime() && pair.getLeft().isTargetedAt(event.getWorld());
            DelayManager.POSITION_DELAY_WORKS.stream().filter(pairPredicate).map(Pair::getLeft).forEach(IPositionDelayWork.Worker::work);
            DelayManager.POSITION_DELAY_WORKS.removeIf(pairPredicate);
        }
    }
}
