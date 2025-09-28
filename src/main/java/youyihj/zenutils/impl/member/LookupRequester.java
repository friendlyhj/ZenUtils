package youyihj.zenutils.impl.member;

import org.objectweb.asm.Opcodes;

/**
 * @author youyihj
 */
public enum LookupRequester {
    PUBLIC(Opcodes.ACC_PUBLIC),
    SUBCLASS(Opcodes.ACC_PUBLIC | Opcodes.ACC_PROTECTED),
    SELF(-1);

    private final int modifierMask;

    LookupRequester(int modifierMask) {
        this.modifierMask = modifierMask;
    }

    public boolean allows(int modifier) {
        return (this == SELF || (modifierMask & modifier) != 0) && (modifier & Opcodes.ACC_SYNTHETIC) == 0;
    }
}
