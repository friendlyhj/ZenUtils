package youyihj.zenutils.api.event;

import crafttweaker.annotations.ZenRegister;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import youyihj.zenutils.api.util.ReflectionInvoked;

/**
 * @author youyihj
 */
@ZenClass("mods.zenutils.EventPriority")
@ZenRegister
public enum CTEventPriority {
    HIGHEST(EventPriority.HIGHEST),
    HIGH(EventPriority.HIGH),
    NORMAL(EventPriority.NORMAL),
    LOW(EventPriority.LOW),
    LOWEST(EventPriority.LOWEST);
    private final EventPriority priority;

    CTEventPriority(EventPriority priority) {
        this.priority = priority;
    }

    @ZenMethod
    public static CTEventPriority highest() {
        return HIGHEST;
    }

    @ZenMethod
    public static CTEventPriority high() {
        return HIGH;
    }

    @ZenMethod
    public static CTEventPriority normal() {
        return NORMAL;
    }

    @ZenMethod
    public static CTEventPriority low() {
        return LOW;
    }

    @ZenMethod
    public static CTEventPriority lowest() {
        return LOWEST;
    }

    @ReflectionInvoked
    public static CTEventPriority getDefault(String unused) {
        return NORMAL;
    }

    public EventPriority getPriority() {
        return priority;
    }
}
