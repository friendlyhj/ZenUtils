package youyihj.zenutils.impl.member;

import java.lang.reflect.Type;

/**
 * @author youyihj
 */
public class StableType implements Type {
    private final TypeData typeData;

    public StableType(TypeData typeData) {
        this.typeData = typeData;
    }

    public TypeData getTypeData() {
        return typeData;
    }

    @Override
    public String toString() {
        return typeData.toString();
    }
}
