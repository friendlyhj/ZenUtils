package youyihj.zenutils.api.util.catenation.persistence;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.world.IWorld;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import youyihj.zenutils.impl.util.catenation.persistence.CatenationPersistenceImpl;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.CatenationPersistence")
public class CatenationPersistenceAPI {
    @ZenMethod
    public static CatenationPersistenceEntryBuilder registerPersistedCatenation(String key, ICatenationFactory catenationSupplier) {
        return new CatenationPersistenceEntryBuilder(key, catenationSupplier);
    }

    @ZenMethod
    public static PersistedCatenationStarter startPersistedCatenation(String key, IWorld world) {
        return new PersistedCatenationStarter(world, key);
    }

    public static <T> void receiveObject(ICatenationObjectHolder.Type<T> type, T object) {
        CatenationPersistenceImpl.receiveObject(type, object);
    }
}
