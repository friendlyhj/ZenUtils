package youyihj.zenutils.api.util.catenation.persistence;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.data.IData;
import crafttweaker.api.entity.IEntity;
import crafttweaker.api.player.IPlayer;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IWorld;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import youyihj.zenutils.api.util.catenation.Catenation;
import youyihj.zenutils.impl.util.catenation.persistence.CatenationPersistedObjects;
import youyihj.zenutils.impl.util.catenation.persistence.CatenationPersistenceImpl;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.PersistedCatenationStarter")
public class PersistedCatenationStarter {
    private final IWorld world;
    private final String key;

    private final CatenationPersistedObjects objects = new CatenationPersistedObjects();

    public PersistedCatenationStarter(IWorld world, String key) {
        this.world = world;
        this.key = key;
    }

    public <T> PersistedCatenationStarter with(String key, ICatenationObjectHolder.Type<T> type, T object) {
        this.objects.with(key, type, object);
        return this;
    }

    @ZenMethod
    public Catenation start() {
        return CatenationPersistenceImpl.startCatenation(world, key, objects);
    }

    @ZenMethod
    public PersistedCatenationStarter withPlayer(IPlayer player, @Optional("player") String key) {
        return with(key, BuiltinObjectHolderTypes.PLAYER, player);
    }

    @ZenMethod
    public PersistedCatenationStarter withPosition(IBlockPos pos, @Optional("pos") String key) {
        return with(key, BuiltinObjectHolderTypes.POSITION, pos);
    }

    @ZenMethod
    public PersistedCatenationStarter withData(IData data, @Optional("data") String key) {
        return with(key, BuiltinObjectHolderTypes.DATA, data);
    }

    @ZenMethod
    public PersistedCatenationStarter withEntity(IEntity entity, @Optional("entity") String key) {
        return with(key, BuiltinObjectHolderTypes.ENTITY, entity);
    }
}
