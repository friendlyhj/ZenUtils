package youyihj.zenutils.impl.util.catenation.persistence;

import crafttweaker.api.data.IData;
import youyihj.zenutils.api.util.catenation.persistence.BuiltinObjectHolderTypes;
import youyihj.zenutils.api.util.catenation.persistence.ICatenationObjectHolder;

/**
 * @author youyihj
 */
public class DataObjectHolder implements ICatenationObjectHolder<IData> {

    private IData data;

    @Override
    public Type<IData> getType() {
        return BuiltinObjectHolderTypes.DATA;
    }

    @Override
    public IData serializeToData() {
        return data;
    }

    @Override
    public void deserializeFromData(IData data) {
        this.data = data;
    }

    @Override
    public void receiveObject(IData object) {
        if (this.data != null) {
            this.data = object;
        }
    }

    @Override
    public IData getValue() {
        return data;
    }

    @Override
    public void setValue(IData value) {
        this.data = value;
    }
}
