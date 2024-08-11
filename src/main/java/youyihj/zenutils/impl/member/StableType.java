package youyihj.zenutils.impl.member;

import java.lang.reflect.Type;

/**
 * @author youyihj
 */
public class StableType implements Type {
    private final String name;

    public StableType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
