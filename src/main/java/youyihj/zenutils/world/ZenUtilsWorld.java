package youyihj.zenutils.world;

import com.google.common.collect.Lists;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.entity.IEntity;
import crafttweaker.api.player.IPlayer;
import crafttweaker.api.world.IWorld;
import crafttweaker.mc1120.player.MCPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenMethod;
import youyihj.zenutils.util.CrTUUID;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ZenRegister
@ZenClass("mods.zenutils.ZenUtilsWorld")
@ZenExpansion("crafttweaker.world.IWorld")
@SuppressWarnings("unused")
public class ZenUtilsWorld {
    @Nullable
    @ZenMethod
    public static IPlayer getPlayerByName(IWorld iWorld, String name) {
       EntityPlayer player = (((World) iWorld.getInternal()).getPlayerEntityByName(name));
       return (player == null) ? null : new MCPlayer(player);
    }

    @Nullable
    @ZenMethod
    public static IPlayer getPlayerByUUID(IWorld iWorld, CrTUUID uuid) {
        EntityPlayer player = ((World) iWorld.getInternal()).getPlayerEntityByUUID((UUID) uuid.getInternal());
        return (player == null) ? null : new MCPlayer(player);
    }

    @ZenMethod
    public static List<IPlayer> getAllPlayers(IWorld iWorld) {
        List<IPlayer> temp = Lists.newArrayList();
        temp.addAll(((World) iWorld.getInternal()).playerEntities.stream().map(MCPlayer::new).collect(Collectors.toList()));
        return temp;
    }

    @ZenMethod
    public static IPlayer getClosestPlayerToEntity(IWorld iWorld, IEntity iEntity, double distance, boolean spectator) {
        return getClosestPlayer(iWorld, iEntity.getPosX(), iEntity.getPosY(), iEntity.getPosZ(), distance, spectator);
    }

    @ZenMethod
    public static IPlayer getClosestPlayer(IWorld iWorld, double posX, double posY, double posZ, double distance, boolean spectator) {
        EntityPlayer player = ((World) iWorld.getInternal()).getClosestPlayer(posX, posY, posZ, distance, spectator);
        return (player == null) ? null : new MCPlayer(player);
    }
}
