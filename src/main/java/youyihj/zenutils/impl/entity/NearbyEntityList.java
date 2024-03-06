package youyihj.zenutils.impl.entity;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import crafttweaker.api.entity.IEntity;
import crafttweaker.api.entity.IEntityDefinition;
import crafttweaker.api.entity.IEntityItem;
import crafttweaker.api.entity.IEntityLivingBase;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import crafttweaker.api.world.IVector3d;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;
import youyihj.zenutils.api.entity.IEntityPredicate;
import youyihj.zenutils.api.entity.INearbyEntityList;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author youyihj
 */
public class NearbyEntityList implements INearbyEntityList {
    private final World world;
    private final IVector3d pos;
    private final double radius;
    private Predicate<IEntity> predicate = Predicates.alwaysTrue();
    private final Entity exclude;

    public NearbyEntityList(World world, IVector3d pos, double radius, IEntity exclude) {
        this.world = world;
        this.pos = pos;
        this.radius = radius;
        this.exclude = CraftTweakerMC.getEntity(exclude);
    }

    @Override
    public INearbyEntityList filterType(IEntityDefinition definition) {
        this.predicate = Predicates.and(this.predicate, it -> Objects.equals(it.getDefinition(), definition));
        return this;
    }

    @Override
    public INearbyEntityList filter(IEntityPredicate predicate) {
        this.predicate = Predicates.and(this.predicate, predicate::test);
        return this;
    }

    @Override
    public List<IEntity> entities() {
        return getEntities(Entity.class, CraftTweakerMC::getIEntity, false);
    }

    @Override
    public List<IEntityLivingBase> livings() {
        return getEntities(EntityLivingBase.class, CraftTweakerMC::getIEntityLivingBase, false);
    }

    @Override
    public List<IPlayer> players() {
        return getEntities(EntityPlayer.class, CraftTweakerMC::getIPlayer, true);
    }

    @Override
    public List<IEntityItem> items() {
        return getEntities(EntityItem.class, CraftTweakerMC::getIEntityItem, false);
    }

    @Override
    public IEntity closestEntity() {
        return firstOrNull(entities());
    }

    @Override
    public IEntityLivingBase closestLiving() {
        return firstOrNull(livings());
    }

    @Override
    public IPlayer closestPlayer() {
        return firstOrNull(players());
    }

    @Override
    public IEntityItem closestItem() {
        return firstOrNull(items());
    }

    private Predicate<Entity> mcPredicate() {
        return Predicates.compose(predicate, CraftTweakerMC::getIEntity);
    }

    private <M extends Entity, C extends IEntity> List<C> getEntities(Class<M> mcEntityClass, Function<M, C> crtMapper, boolean player) {
        List<M> entities = player ? world.getPlayers(mcEntityClass, mcPredicate()) : world.getEntities(mcEntityClass, mcPredicate());
        return entities.stream()
                .map(e -> Pair.of(e, e.getDistanceSq(pos.getX(), pos.getY(), pos.getZ())))
                .filter(it -> it.getRight() <= radius * radius)
                .filter(it -> exclude == null || it.getLeft() != exclude)
                .sorted(Comparator.comparingDouble(Pair::getRight))
                .map(Pair::getLeft)
                .map(crtMapper)
                .collect(Collectors.toList());
    }

    private static <T> T firstOrNull(List<T> list) {
        return list.isEmpty() ? null : list.get(0);
    }
}
