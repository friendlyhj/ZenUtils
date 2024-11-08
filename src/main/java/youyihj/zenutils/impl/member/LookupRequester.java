package youyihj.zenutils.impl.member;

import java.lang.reflect.Modifier;

/**
 * @author youyihj
 */
public enum LookupRequester {
    PUBLIC(Modifier.PUBLIC),
    SUBCLASS(Modifier.PUBLIC | Modifier.PROTECTED),
    SELF(0) {
        @Override
        public boolean allows(int modifier) {
            return true;
        }
    };

    private final int modifierMask;

    LookupRequester(int modifierMask) {
        this.modifierMask = modifierMask;
    }

    public boolean allows(int modifier) {
        return (modifierMask & modifier) != 0;
    }
}
