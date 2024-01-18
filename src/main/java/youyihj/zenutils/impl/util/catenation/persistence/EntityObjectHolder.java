package youyihj.zenutils.impl.util.catenation.persistence;

import crafttweaker.api.data.DataList;
import crafttweaker.api.data.DataLong;
import crafttweaker.api.data.IData;
import crafttweaker.api.entity.IEntity;
import crafttweaker.api.minecraft.CraftTweakerMC;
import youyihj.zenutils.api.util.catenation.Catenation;
import youyihj.zenutils.api.util.catenation.persistence.BuiltinObjectHolderTypes;
import youyihj.zenutils.api.util.catenation.persistence.ICatenationObjectHolder;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @author youyihj
 */
public class EntityObjectHolder implements ICatenationObjectHolder<IEntity> {
    private IEntity entity;
    private UUID serial;

    @Override
    public Type<IEntity> getType() {
        return BuiltinObjectHolderTypes.ENTITY;
    }

    @Override
    public IData serializeToData() {
        UUID uuid = CraftTweakerMC.getEntity(entity).getUniqueID();
        return new DataList(Arrays.asList(new DataLong(uuid.getMostSignificantBits()), new DataLong(uuid.getLeastSignificantBits())), true);
    }

    @Override
    public void deserializeFromData(IData data) {
        List<IData> list = data.asList();
        serial = new UUID(list.get(0).asLong(), list.get(1).asLong());
    }

    @Override
    public void receiveObject(IEntity object) {
        if (Objects.equals(serial, CraftTweakerMC.getEntity(object).getUniqueID())) {
            this.entity = object;
        }
    }

    @Override
    public IEntity getValue() {
        return entity;
    }

    @Override
    public void setValue(IEntity value) {
        this.entity = value;
    }

    @Override
    public void invalidate() {
        this.entity = null;
    }

    @Override
    public ValidationResult validate(Catenation catenation) {
        if (entity != null) {
            if (!entity.isAlive()) {
                return ValidationResult.INVALID_STOP;
            } else if (!CraftTweakerMC.getEntity(entity).isAddedToWorld()) {
                return ValidationResult.INVALID_PAUSE;
            }
        }
        return ValidationResult.VALID;
    }
}
