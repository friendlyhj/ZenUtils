package youyihj.zenutils.api.entity;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.entity.IEntityDefinition;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.registry.EntityEntry;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenMethod;
import youyihj.zenutils.impl.entity.EntityTickDispatcher;

/**
 * @author youyihj
 */
@ZenRegister
@ZenExpansion("crafttweaker.entity.IEntityDefinition")
public class ZenUtilsEntityDefinition {
    @ZenMethod
    public static void onTick(IEntityDefinition definition, IEntityTick operation, @Optional int interval) {
        Class<? extends Entity> entityClass = ((EntityEntry) definition.getInternal()).getEntityClass();
        EntityTickDispatcher.register(entityClass, operation, interval);
    }
}
