package youyihj.zenutils.api.entity;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.entity.IEntity;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import crafttweaker.api.world.IVector3d;
import net.minecraft.entity.player.EntityPlayer;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenMethod;
import youyihj.zenutils.api.util.CrTUUID;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.ZenUtilsEntity")
@ZenExpansion("crafttweaker.entity.IEntity")
@SuppressWarnings("unused")
public class ZenUtilsEntity {

    @ZenMethod
    public static void setMotionVector(IEntity entity, IVector3d vector) {
        entity.setMotionX(vector.getX());
        entity.setMotionY(vector.getY());
        entity.setMotionZ(vector.getZ());
    }

    @ZenMethod
    public static CrTUUID getUUIDObject(IEntity entity) {
        if (entity instanceof IPlayer) {
            return new CrTUUID(EntityPlayer.getUUID(CraftTweakerMC.getPlayer(((IPlayer) entity)).getGameProfile()));
        } else {
            return new CrTUUID(CraftTweakerMC.getEntity(entity).getUniqueID());
        }
    }
}
