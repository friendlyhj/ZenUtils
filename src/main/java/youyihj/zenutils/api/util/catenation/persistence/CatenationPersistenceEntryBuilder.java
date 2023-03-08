package youyihj.zenutils.api.util.catenation.persistence;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.HashMap;
import java.util.Map;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.CatenationPersistenceEntryBuilder")
public class CatenationPersistenceEntryBuilder {
    private final String key;
    private final Map<String, ICatenationObjectHolder.Type<?>> objectHolderTypes = new HashMap<>();

    private ICatenationFactory catenationFactory;

    public CatenationPersistenceEntryBuilder(String key) {
        this.key = key;
    }

    public CatenationPersistenceEntryBuilder addObjectHolder(String objectKey, ICatenationObjectHolder.Type<?> type) {
        objectHolderTypes.put(objectKey, type);
        return this;
    }

    @ZenMethod
    public CatenationPersistenceEntryBuilder setCatenationFactory(ICatenationFactory catenationFactory) {
        this.catenationFactory = catenationFactory;
        return this;
    }

    @ZenMethod
    public void register() {
        if (catenationFactory == null) {
            throw new IllegalArgumentException("CatenationFactory is not settle.");
        }
        CraftTweakerAPI.apply(new CatenationPersistenceRegisterAction(key, objectHolderTypes, catenationFactory));
    }

    @ZenMethod
    public CatenationPersistenceEntryBuilder addPlayerHolder(@Optional("player") String objectKey) {
        return addObjectHolder(objectKey, BuiltinObjectHolderTypes.PLAYER);
    }
}
