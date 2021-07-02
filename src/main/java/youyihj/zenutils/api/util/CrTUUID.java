package youyihj.zenutils.api.util;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.*;

import java.util.Objects;
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

    @ZenOperator(OperatorType.COMPARE)
    public int compareTo(CrTUUID other) {
        return this.uuid.compareTo(other.uuid);
    }

    public UUID getInternal() {
        return this.uuid;
    }

    @Override
    @ZenOperator(OperatorType.EQUALS)
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CrTUUID crTUUID = (CrTUUID) o;
        return Objects.equals(uuid, crTUUID.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
