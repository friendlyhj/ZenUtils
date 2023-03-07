package youyihj.zenutils.api.util.catenation.persistence;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.world.IWorld;
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
}
