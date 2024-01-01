package youyihj.zenutils.impl.mixin.crafttweaker;

import crafttweaker.api.entity.IEntityDefinition;
import crafttweaker.mc1120.entity.MCEntity;
import crafttweaker.mc1120.entity.MCEntityDefinition;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import youyihj.zenutils.impl.util.SimpleCache;

import java.util.Map;

/**
 * @author youyihj
 */
@Mixin(value = MCEntity.class, remap = false)
public abstract class MixinMCEntity {

    @Unique
    private static final SimpleCache<Class<?>, IEntityDefinition> DEF_CACHE = new SimpleCache<>(it -> {
        for (Map.Entry<ResourceLocation, EntityEntry> entry : ForgeRegistries.ENTITIES.getEntries()) {
            if (entry.getValue().getEntityClass() == it) {
                return new MCEntityDefinition(entry.getValue());
            }
        }
        return null;
    });

    @Shadow
    private Entity entity;

    /**
     * @author youyihj
     * @reason improve performance using a cache
     */
    @Overwrite
    public IEntityDefinition getDefinition() {
        return DEF_CACHE.get(entity.getClass());
    }
}
