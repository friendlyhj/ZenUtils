package youyihj.zenutils.api.util.delay;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.mc1120.CraftTweaker;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import youyihj.zenutils.api.world.ZenUtilsWorld;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.DelayManager")
@Deprecated
public class CTDelayManager {

    @ZenMethod
    public static void addDelayWork(DelayRunnable runnable, @Optional(valueLong = 1L) long delay) {
        addDelayWork(runnable, IsExecute.of(true), delay);
    }

    @ZenMethod
    public static void addDelayWork(DelayRunnable runnable, IsExecute isExecute, @Optional(valueLong = 1L) long delay) {
        CraftTweakerAPI.logWarning("DelayManager is deprecated, use Catenation instead.");
        ZenUtilsWorld.catenation(CraftTweakerMC.getIWorld(CraftTweaker.server.getEntityWorld()))
                .sleep(delay)
                .then(world -> runnable.run())
                .stopWhen(world -> !isExecute.isExec())
                .start();
    }
}
