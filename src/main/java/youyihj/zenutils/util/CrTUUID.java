package youyihj.zenutils.util;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.*;

import java.util.UUID;

@ZenRegister
@ZenClass("mods.zenutils.UUID")
@SuppressWarnings("unused")
public class CrTUUID {

    private final UUID uuid;

    public CrTUUID(UUID uuid) {
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
    public static CrTUUID randomUUID() {
        return new CrTUUID(UUID.randomUUID());
    }

    @ZenMethod
    public static CrTUUID fromString(String name) {
        return new CrTUUID(UUID.fromString(name));
    }

    @ZenOperator(OperatorType.EQUALS)
    public boolean equals(CrTUUID other) {
        return this.uuid.equals(other.uuid);
    }

    @ZenOperator(OperatorType.COMPARE)
    public int compareTo(CrTUUID other) {
        return this.uuid.compareTo(other.uuid);
    }

    public Object getInternal() {
        return this.uuid;
    }
}
