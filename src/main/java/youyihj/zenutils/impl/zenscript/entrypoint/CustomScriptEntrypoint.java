package youyihj.zenutils.impl.zenscript.entrypoint;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.mc1120.CraftTweaker;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.event.*;
import youyihj.zenutils.impl.core.Configuration;

import java.util.Objects;

/**
 * @author youyihj
 */
public class CustomScriptEntrypoint {

    private static final Table<Class<? extends FMLEvent>, EntrypointKey, String> entrypointToRun = HashBasedTable.create();
    private static boolean parsed = false;

    public static void runScript(Object modCoreObj, FMLEvent event, boolean before) {
        if (!parsed) {
            parseEntryPoint();
            parsed = true;
        }
        ModContainer modContainer = Loader.instance().getReversedModObjectList().get(modCoreObj);
        if (modContainer == null) {
            return;
        }
        String modid = modContainer.getModId();
        String loader = entrypointToRun.remove(event.getClass(), new EntrypointKey(modid, before));
        if (loader != null) {
            CraftTweakerAPI.tweaker.loadScript(false, loader);
            if (event.getClass() == FMLPostInitializationEvent.class && !entrypointToRun.containsRow(FMLPostInitializationEvent.class)) {
                CraftTweakerAPI.logInfo("All custom script entrypoint on postInit are executed, re-run recipe modifications.");
                CraftTweaker.INSTANCE.onPostInit(((FMLPostInitializationEvent) event));
            }
        }
    }

    private static void parseEntryPoint() {
        for (String s : Configuration.customScriptEntrypoint) {
            String[] split = s.split(";", 4);
            String loaderName = split[0];
            String modid = split[1];
            String stageMarker = split[2];
            String pos = split[3];
            Class<? extends FMLEvent> event;
            switch (stageMarker) {
                case "C":
                    event = FMLConstructionEvent.class;
                    break;
                case "H":
                    event = FMLPreInitializationEvent.class;
                    break;
                case "I":
                    event = FMLInitializationEvent.class;
                    break;
                case "J":
                    event = FMLPostInitializationEvent.class;
                    break;
                case "A":
                    event = FMLLoadCompleteEvent.class;
                    break;
                default:
                    continue;
            }
            boolean before;
            switch (pos) {
                case "before":
                    before = true;
                    break;
                case "after":
                    before = false;
                    break;
                default:
                    continue;
            }
            entrypointToRun.put(event, new EntrypointKey(loaderName, before), modid);
        }
    }

    private static class EntrypointKey {
        private final String modid;
        private final boolean before;

        public EntrypointKey(String modid, boolean before) {
            this.modid = modid;
            this.before = before;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            EntrypointKey that = (EntrypointKey) o;
            return before == that.before && Objects.equals(modid, that.modid);
        }

        @Override
        public int hashCode() {
            return Objects.hash(modid, before);
        }
    }
}
