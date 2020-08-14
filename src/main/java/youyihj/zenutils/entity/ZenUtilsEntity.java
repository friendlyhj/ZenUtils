package youyihj.zenutils.entity;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.entity.IEntity;
import crafttweaker.api.world.IVector3d;
import net.minecraft.entity.Entity;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenMethod;
import youyihj.zenutils.util.CrTUUID;

@ZenRegister
@ZenClass("mods.zenutils.ZenUtilsEntity")
@ZenExpansion("crafttweaker.entity.IEntity")
@SuppressWarnings("unused")
public class ZenUtilsEntity {

    @ZenMethod
    public static void setMotionX(IEntity entity, double value) {
        ((Entity) entity.getInternal()).motionX = value;
    }

    @ZenMethod
    public static void setMotionY(IEntity entity, double value) {
        ((Entity) entity.getInternal()).motionY = value;
    }

    @ZenMethod
    public static void setMotionZ(IEntity entity, double value) {
        ((Entity) entity.getInternal()).motionZ = value;
    }

    @ZenMethod
    public static void setMotionVector(IEntity entity, IVector3d vector) {
        ZenUtilsEntity.setMotionX(entity, vector.getX());
        ZenUtilsEntity.setMotionY(entity, vector.getY());
        ZenUtilsEntity.setMotionZ(entity, vector.getZ());
    }

    @ZenMethod
    public static CrTUUID getUUID(IEntity entity) {
        return new CrTUUID(((Entity) entity.getInternal()).getUniqueID());
    }
}
