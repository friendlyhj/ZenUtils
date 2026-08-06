package youyihj.zenutils.impl.zenscript.entrypoint;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Table;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.recipes.ICraftingRecipe;
import crafttweaker.mc1120.CraftTweaker;
import crafttweaker.mc1120.furnace.MCFurnaceManager;
import crafttweaker.mc1120.recipes.MCRecipeManager;
import net.minecraftforge.fml.common.FMLModContainer;
import net.minecraftforge.fml.common.ProgressManager;
import net.minecraftforge.fml.common.event.*;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import youyihj.zenutils.ZenUtils;
import youyihj.zenutils.api.util.ReflectionInvoked;
import youyihj.zenutils.impl.config.ClassProvider;
import youyihj.zenutils.impl.core.Configuration;
import youyihj.zenutils.impl.util.InternalUtils;
import youyihj.zenutils.impl.util.ReflectUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.objectweb.asm.Opcodes.*;

/**
 * @author youyihj
 */
public class CustomScriptEntrypoint {

    private static final Table<Class<? extends FMLEvent>, EntrypointKey, String> entrypointToRun = HashBasedTable.create();
    private static boolean parsed = false;
    private static final List<Class<? extends FMLEvent>> EVENTS = Arrays.asList(
            FMLPreInitializationEvent.class,
            FMLInitializationEvent.class,
            FMLPostInitializationEvent.class,
            FMLLoadCompleteEvent.class
    );

    private static final Field EVENT_METHODS_FIELD;

    public static int craftingRecipeAdditionStandardIndex;
    public static int craftingRecipeRemovalStandardIndex;
    public static int furnaceRecipeAdditionStandardIndex;
    public static int furnaceRecipeRemovalStandardIndex;

    static {
        try {
            EVENT_METHODS_FIELD = ReflectUtils.removePrivate(FMLModContainer.class, "eventMethods");
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @ReflectionInvoked(asm = true)
    public static void runScript(String modid, FMLEvent event, boolean before) {
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

    public static void injectToMod(FMLModContainer modContainer) {
        if (!parsed) {
            parseEntryPoint();
            parsed = true;
        }

        ListMultimap<Class<? extends FMLEvent>, Method> eventMethods;
        try {
            eventMethods = InternalUtils.cast(EVENT_METHODS_FIELD.get(modContainer));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        for (Class<? extends FMLEvent> eventClass : EVENTS) {
            if (entrypointToRun.contains(eventClass, new EntrypointKey(modContainer.getModId(), true))) {
                eventMethods.get(eventClass).add(0, defineEntrypointMethod(modContainer.getModId(), eventClass, true));
            }
            if (entrypointToRun.contains(eventClass, new EntrypointKey(modContainer.getModId(), false))) {
                eventMethods.get(eventClass).add(defineEntrypointMethod(modContainer.getModId(), eventClass, false));
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

    private static Method defineEntrypointMethod(String modid, Class<? extends FMLEvent> eventClass, boolean before) {
        String className = "youyihj/zenutils/impl/zenscript/entrypoint/" + eventClass.getSimpleName() + modid + "Exec";
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        cw.visit(V1_8, ACC_PUBLIC | ACC_SUPER | ACC_FINAL, className, null, "java/lang/Object", null);

        cw.visitSource(".dynamic", null);

        MethodVisitor mv;
        mv = cw.visitMethod(ACC_PRIVATE, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        mv.visitInsn(RETURN);
        mv.visitEnd();

        mv = cw.visitMethod(ACC_PUBLIC | ACC_STATIC, "execute", "(Lnet/minecraftforge/fml/common/event/FMLEvent;)V", null, null);
        mv.visitCode();
        mv.visitLdcInsn(modid);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitInsn(before ? ICONST_1 : ICONST_0);
        mv.visitMethodInsn(INVOKESTATIC, "youyihj/zenutils/impl/zenscript/entrypoint/CustomScriptEntrypoint", "runScript", "(Ljava/lang/String;Lnet/minecraftforge/fml/common/event/FMLEvent;Z)V", false);
        mv.visitInsn(RETURN);
        mv.visitEnd();

        cw.visitEnd();

        ClassProvider.classes.put(className.replace('/', '.'), cw.toByteArray());

        try {
            Class<?> execClass = Class.forName(className.replace('/', '.'));
            return execClass.getMethod("execute", FMLEvent.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
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
