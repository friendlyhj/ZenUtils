package youyihj.zenutils.api.util.catenation.persistence;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import youyihj.zenutils.impl.util.catenation.persistence.PlayerObjectHolder;

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

    private final ICatenationFactory catenationSupplier;

    public CatenationPersistenceEntryBuilder(String key, ICatenationFactory catenationSupplier) {
        this.key = key;
        this.catenationSupplier = catenationSupplier;
    }

    public void addObjectHolder(String objectKey, ICatenationObjectHolder.Type<?> type) {
        objectHolderTypes.put(objectKey, type);
    }

    @ZenMethod
    public void register() {
        CraftTweakerAPI.apply(new CatenationPersistenceRegisterAction(key, objectHolderTypes, catenationSupplier));
    }

    @ZenMethod
    public void addPlayerHolder(@Optional(value = "player") String objectKey) {
        addObjectHolder(objectKey, PlayerObjectHolder.TYPE);
    }
}
