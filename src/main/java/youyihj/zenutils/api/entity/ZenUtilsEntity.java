package youyihj.zenutils.api.entity;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.data.IData;
import crafttweaker.api.entity.IEntity;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import crafttweaker.api.world.IVector3d;
import crafttweaker.mc1120.data.NBTConverter;
import crafttweaker.mc1120.data.NBTUpdater;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import youyihj.zenutils.api.util.CrTUUID;

/**
 * @author youyihj
 */
@ZenRegister
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

    @ZenMethod
    public static void updateNBT(IEntity entity, IData data) {
        Entity mcEntity = CraftTweakerMC.getEntity(entity);
        NBTTagCompound nbt = new NBTTagCompound();
        mcEntity.writeToNBT(nbt);
        NBTUpdater.updateMap(nbt, data);
        mcEntity.readFromNBT(nbt);
    }

    @ZenGetter("forgeData")
    public static IData getForgeData(IEntity entity) {
        return NBTConverter.from(CraftTweakerMC.getEntity(entity).getEntityData(), true);
    }
}
