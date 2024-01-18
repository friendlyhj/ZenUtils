package youyihj.zenutils.impl.entity;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.entity.Entity;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import youyihj.zenutils.api.entity.IEntityTick;
import youyihj.zenutils.api.util.catenation.Catenation;
import youyihj.zenutils.api.util.catenation.ICatenationBuilder;
import youyihj.zenutils.api.util.catenation.persistence.ICatenationFactory;
import youyihj.zenutils.api.world.ZenUtilsWorld;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Function;

/**
 * @author youyihj
 */
@Mod.EventBusSubscriber
public class EntityTickDispatcher {
    private static final Multimap<Entity, Catenation> catenations = Multimaps.newListMultimap(new WeakHashMap<>(), ArrayList::new);
    private static final Multimap<Class<? extends Entity>, Mutable<Function<Entity, ICatenationFactory>>> catenationFactories = HashMultimap.create();
    public static void register(Class<? extends Entity> entityClass, IEntityTick operation, int interval) {
        // starting a catenation requires 1 tick, at least skip 1 tick -> 2 ticks interval
        int actualInterval = Math.max(0, interval - 2);

        Mutable<Function<Entity, ICatenationFactory>> ref = new MutableObject<>();
        ref.setValue(entity -> world -> {
            ICatenationBuilder builder = ZenUtilsWorld.catenation(world);
            if (actualInterval != 0) {
                builder.sleep(actualInterval);
            }
            builder.then(((world1, context) -> operation.tick(CraftTweakerMC.getIEntity(entity))));
            builder.stopWhen((world1, context) -> !entity.isEntityAlive() || !entity.isAddedToWorld());
            builder.onStop(((world1, context) -> {
                switch (context.getStatus()) {
                    case STOP_MANUAL:
                        break;
                    case FINISH:
                        ref.getValue().apply(entity).get(world);
                    default:
                        catenations.remove(entity, context.getCatenation());
                }
            }));
            Catenation catenation = builder.start();
            catenations.put(entity, catenation);
            return catenation;
        });
        catenationFactories.put(entityClass, ref);
    }

    public static void clearCatenationFactories() {
        catenationFactories.clear();
    }

    public static void restartCatenations() {
        catenations.values().forEach(Catenation::stop);
        Set<Entity> entities = new HashSet<>(catenations.keySet());
        catenations.clear();
        entities.forEach(EntityTickDispatcher::startCatenation);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
        Entity entity = event.getEntity();
        if (entity.world.isRemote) return;
        startCatenation(entity);
    }

    private static void startCatenation(Entity entity) {
        for (Mutable<Function<Entity, ICatenationFactory>> factoryFactory : catenationFactories.get(entity.getClass())) {
            factoryFactory.getValue().apply(entity).get(CraftTweakerMC.getIWorld(entity.world));
        }
    }
}
