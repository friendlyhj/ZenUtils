package youyihj.zenutils.util.delay;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.player.IPlayer;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IWorld;
import org.apache.commons.lang3.tuple.Pair;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.List;

@ZenRegister
@ZenClass("mods.zenutils.DelayManager")
public class DelayManager {
    static final List<Pair<IPlayerDelayWork.Worker, Long>> PLAYER_DELAY_WORKS = new ArrayList<>();
    static final List<Pair<IPositionDelayWork.Worker, Long>> POSITION_DELAY_WORKS = new ArrayList<>();

    @ZenMethod
    public static void addPlayerDelayWork(IPlayer player, IPlayerDelayWork work, @Optional(valueLong = 1L) long delay) {
        IWorld world = player.getWorld();
        if (world.isRemote())
            return;
        PLAYER_DELAY_WORKS.add(Pair.of(new IPlayerDelayWork.Worker(work, player), world.getWorldTime() + delay));
    }

    @ZenMethod
    public static void addPositionDelayWork(IWorld world, IBlockPos pos, IPositionDelayWork work, @Optional(valueLong = 1L) long delay) {
        if (world.isRemote())
            return;
        POSITION_DELAY_WORKS.add(Pair.of(new IPositionDelayWork.Worker(work, world, pos), world.getWorldTime() + delay));
    }
}
