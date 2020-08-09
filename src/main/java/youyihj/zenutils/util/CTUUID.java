package youyihj.zenutils.util;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.*;

import java.util.UUID;

@ZenRegister
@ZenClass("mods.zenutils.UUID")
@SuppressWarnings("unused")
public class CTUUID {

    private final UUID uuid;

    public CTUUID(UUID uuid) {
        this.uuid = uuid;
    }

    @ZenMethod
    public long getMostSignificantBits() {
        return this.uuid.getMostSignificantBits();
    }

    @ZenMethod
    public long getLeastSignificantBits() {
        return this.uuid.getLeastSignificantBits();
    }

    @ZenMethod
    @ZenCaster
    public String asString() {
        return this.uuid.toString();
    }

    @ZenMethod
    public static CTUUID randomUUID() {
        return new CTUUID(UUID.randomUUID());
    }

    @ZenMethod
    public static CTUUID fromString(String name) {
        return new CTUUID(UUID.fromString(name));
    }

    @ZenOperator(OperatorType.EQUALS)
    public boolean equals(CTUUID other) {
        return this.uuid.equals(other.uuid);
    }

    @ZenOperator(OperatorType.COMPARE)
    public int compareTo(CTUUID other) {
        return this.uuid.compareTo(other.uuid);
    }

    public Object getInternal() {
        return this.uuid;
    }
}
