package youyihj.zenutils.util.delay;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.mc1120.CraftTweaker;
import org.apache.commons.lang3.tuple.Pair;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.List;

@ZenRegister
@ZenClass("mods.zenutils.DelayManager")
public class DelayManager {
    static final List<Pair<DelayRunnable, Long>> DELAY_RUNNABLE_LIST = new ArrayList<>();

    @ZenMethod
    public static void addDelayWork(DelayRunnable runnable, @Optional(valueLong = 1L) long delay) {
        DELAY_RUNNABLE_LIST.add(Pair.of(runnable, CraftTweaker.server.getEntityWorld().getWorldTime() + delay));
    }
}
