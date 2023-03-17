package youyihj.zenutils.api.util.catenation.persistence;

import crafttweaker.IAction;
import youyihj.zenutils.api.reload.Reloadable;
import youyihj.zenutils.impl.util.catenation.persistence.CatenationPersistenceImpl;

import java.util.Map;

/**
 * @author youyihj
 */
@Reloadable
public class CatenationPersistenceRegisterAction implements IAction {
    private final String key;
    private final Map<String, ICatenationObjectHolder.Type<?>> objectHolderTypes;

    private final ICatenationFactory catenationSupplier;

    public CatenationPersistenceRegisterAction(String key, Map<String, ICatenationObjectHolder.Type<?>> objectHolderTypes, ICatenationFactory catenationSupplier) {
        this.key = key;
        this.objectHolderTypes = objectHolderTypes;
        this.catenationSupplier = catenationSupplier;
    }

    @Override
    public void apply() {
        CatenationPersistenceImpl.registerPersistCatenation(key, catenationSupplier, objectHolderTypes);
    }

    @Override
    public String describe() {
        return null;
    }
}
