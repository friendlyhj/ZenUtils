package youyihj.zenutils.api.util.delay;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import youyihj.zenutils.impl.util.delay.DelayManager;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.DelayManager")
public class CTDelayManager {
    @ZenMethod
    public static void addDelayWork(String uid, DelayRunnable runnable, @Optional(valueLong = 1L) long delay) {
        DelayManager.addDelayWork(runnable, delay, uid);
    }

    @ZenMethod
    public static void delDelayWork(String uid){
        DelayManager.delDelayWork(uid);
    }
}
