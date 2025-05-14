package youyihj.zenutils.impl.zenscript.entrypoint;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.recipes.ICraftingRecipe;
import crafttweaker.mc1120.CraftTweaker;
import crafttweaker.mc1120.furnace.MCFurnaceManager;
import crafttweaker.mc1120.recipes.MCRecipeManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ProgressManager;
import net.minecraftforge.fml.common.event.*;
import youyihj.zenutils.ZenUtils;
import youyihj.zenutils.impl.core.Configuration;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author youyihj
 */
public class CustomScriptEntrypoint {

    private static final Table<Class<? extends FMLEvent>, EntrypointKey, String> entrypointToRun = HashBasedTable.create();
    private static boolean parsed = false;

    public static int craftingRecipeAdditionStandardIndex;
    public static int craftingRecipeRemovalStandardIndex;
    public static int furnaceRecipeAdditionStandardIndex;
    public static int furnaceRecipeRemovalStandardIndex;

    public static void runScript(Object modCoreObj, FMLEvent event, boolean before) {
        if (!parsed) {
            parseEntryPoint();
            parsed = true;
        }
        Mod modAnnotation = modCoreObj.getClass().getAnnotation(Mod.class);
        if (modAnnotation == null) {
            return;
        }
        String modid = modAnnotation.modid();
        String loader = entrypointToRun.remove(event.getClass(), new EntrypointKey(modid, before));
        if (loader != null) {
            ZenUtils.forgeLogger.info("loading script with loader {} {} {} on {}", loader, before ? "before" : "after", modid, event.description());
            CraftTweakerAPI.tweaker.loadScript(false, loader);
            if (event.getClass() == FMLPostInitializationEvent.class && !entrypointToRun.containsRow(FMLPostInitializationEvent.class)) {
                CraftTweakerAPI.logInfo("All custom script entrypoint on postInit are executed, re-run recipe modifications.");
                rerunRecipeModifications();
            }
        }
    }

    private static void rerunRecipeModifications() {
        CraftTweaker.INSTANCE.applyActions(Collections.singletonList(MCRecipeManager.actionRemoveRecipesNoIngredients), "Applying remove recipes without ingredients action", "Failed to apply remove recipes without ingredient action");
        CraftTweaker.INSTANCE.applyActions(subFrom(MCRecipeManager.recipesToRemove, craftingRecipeRemovalStandardIndex), "Applying remove recipe actions", "Failed to apply remove recipe actions");
        MCRecipeManager.refreshRecipes();
        if (MCRecipeManager.ActionReplaceAllOccurences.INSTANCE.hasSubAction()) {
            MCRecipeManager.ActionReplaceAllOccurences.INSTANCE.describeSubActions();
            List<ICraftingRecipe> recipes = CraftTweakerAPI.recipes.getAll();
            ProgressManager.ProgressBar progressBar = ProgressManager.push("Applying replace all occurences action", recipes.size());
            for (ICraftingRecipe recipe : recipes) {
                try {
                    progressBar.step(recipe.getFullResourceName());
                    MCRecipeManager.ActionReplaceAllOccurences.INSTANCE.setCurrentModifiedRecipe(recipe);
                    MCRecipeManager.ActionReplaceAllOccurences.INSTANCE.apply();
                } catch (Exception e) {
                    CraftTweaker.LOG.catching(e);
                    CraftTweakerAPI.logError("Failed to apply replace all occurences action at recipe " + recipe.getFullResourceName(), e);
                }
            }
            ProgressManager.pop(progressBar);
            MCRecipeManager.ActionReplaceAllOccurences.INSTANCE.removeOldRecipes();
        }

        CraftTweaker.INSTANCE.applyActions(subFrom(MCRecipeManager.recipesToAdd, craftingRecipeAdditionStandardIndex), "Applying add recipe actions", "Failed to apply add recipe actions");
        CraftTweaker.INSTANCE.applyActions(subFrom(MCFurnaceManager.recipesToRemove, furnaceRecipeRemovalStandardIndex), "Applying remove furnace recipe actions", "Failed to apply remove furnace recipe actions");
        CraftTweaker.INSTANCE.applyActions(subFrom(MCFurnaceManager.recipesToAdd, furnaceRecipeAdditionStandardIndex), "Applying add furnace recipe actions", "Failed to apply add furnace recipe actions");
        CraftTweaker.INSTANCE.applyActions(CraftTweaker.LATE_ACTIONS, "Applying late actions", "Failed to apply late actions");
        MCRecipeManager.refreshRecipes();
        CraftTweaker.LATE_ACTIONS.clear();

        //Cleanup
        MCRecipeManager.ActionReplaceAllOccurences.INSTANCE.clear();
        MCRecipeManager.cleanUpRecipeList();
    }

    private static <E> List<E> subFrom(List<E> list, int startIndex) {
        return list.subList(startIndex, list.size());
    }

    private static void parseEntryPoint() {
        for (String s : Configuration.customScriptEntrypoint) {
            String[] split = s.trim().split(";", 4);
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
            entrypointToRun.put(event, new EntrypointKey(modid, before), loaderName);
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
