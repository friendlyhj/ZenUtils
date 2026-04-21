package youyihj.zenutils.impl.zenscript.mixin;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.runtime.ScriptFile;
import org.apache.commons.lang3.tuple.Pair;
import youyihj.zenutils.impl.core.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author youyihj
 */
public class ZenMainSchedule {
    private static boolean isMixinLoaded = !Configuration.enableMixin;
    private static final List<Pair<Runnable, ScriptFile>> toRun = new ArrayList<>();

    public static void run(ScriptFile file, Runnable runnable) {
        if (isMixinLoaded) {
            runnable.run();
        } else {
            CraftTweakerAPI.logDefault("class is not loaded to LaunchClassLoader, schedule script: " + file);
            toRun.add(Pair.of(runnable, file));
        }
    }

    public static void onMixinLoaded() {
        isMixinLoaded = true;
        for (Pair<Runnable, ScriptFile> runnableScriptFilePair : toRun) {
            CraftTweakerAPI.logDefault("Running scheduled script: " + runnableScriptFilePair.getRight());
            runnableScriptFilePair.getLeft().run();
        }
        toRun.clear();
    }
}
