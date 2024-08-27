package youyihj.zenutils.impl.member;

import java.lang.reflect.Type;

/**
 * @author youyihj
 */
public class LiteralType implements Type {
    private final String name;

    public LiteralType(TypeData typeData) {
        this.name = typeData.toString();
    }

    public LiteralType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
