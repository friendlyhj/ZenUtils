package youyihj.zenutils.world;

import com.google.common.collect.Lists;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.entity.IEntity;
import crafttweaker.api.entity.IEntityItem;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import crafttweaker.api.world.IWorld;
import crafttweaker.mc1120.entity.MCEntityItem;
import crafttweaker.mc1120.player.MCPlayer;
import net.minecraft.entity.item.EntityItem;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenMethod;
import youyihj.zenutils.util.object.CrTUUID;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.ZenUtilsWorld")
@ZenExpansion("crafttweaker.world.IWorld")
@SuppressWarnings("unused")
public class ZenUtilsWorld {
    @Nullable
    @ZenMethod
    public static IPlayer getPlayerByName(IWorld iWorld, String name) {
       return CraftTweakerMC.getIPlayer(CraftTweakerMC.getWorld(iWorld).getPlayerEntityByName(name));
    }

    @Nullable
    @ZenMethod
    public static IPlayer getPlayerByUUID(IWorld iWorld, CrTUUID uuid) {
        return CraftTweakerMC.getIPlayer(CraftTweakerMC.getWorld(iWorld).getPlayerEntityByUUID((UUID) uuid.getInternal()));
    }

    @ZenMethod
    public static List<IPlayer> getAllPlayers(IWorld iWorld) {
        List<IPlayer> temp = Lists.newArrayList();
        temp.addAll(CraftTweakerMC.getWorld(iWorld).playerEntities.stream().map(MCPlayer::new).collect(Collectors.toList()));
        return temp;
    }

    @ZenMethod
    public static IPlayer getClosestPlayerToEntity(IWorld iWorld, IEntity iEntity, double distance, boolean spectator) {
        return getClosestPlayer(iWorld, iEntity.getPosX(), iEntity.getPosY(), iEntity.getPosZ(), distance, spectator);
    }

    @ZenMethod
    public static IPlayer getClosestPlayer(IWorld iWorld, double posX, double posY, double posZ, double distance, boolean spectator) {
        return CraftTweakerMC.getIPlayer(CraftTweakerMC.getWorld(iWorld).getClosestPlayer(posX, posY, posZ, distance, spectator));
    }

    @ZenMethod
    public static List<IEntity> getEntities(IWorld iWorld) {
        return CraftTweakerMC.getWorld(iWorld).loadedEntityList.stream().map(CraftTweakerMC::getIEntity).collect(Collectors.toList());
    }

    @ZenMethod
    public static List<IEntityItem> getEntityItems(IWorld iWorld) {
        return CraftTweakerMC.getWorld(iWorld).getEntities(EntityItem.class, (entity -> true)).stream().map(MCEntityItem::new).collect(Collectors.toList());
    }

    @ZenMethod
    public static List<IPlayer> getPlayers(IWorld iWorld) {
        return CraftTweakerMC.getWorld(iWorld).playerEntities.stream().map(MCPlayer::new).collect(Collectors.toList());
    }
}
