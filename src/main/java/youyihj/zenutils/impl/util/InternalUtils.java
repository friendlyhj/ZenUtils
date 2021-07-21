package youyihj.zenutils.impl.util;

import com.google.common.collect.ImmutableList;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.data.DataMap;
import crafttweaker.api.data.IData;
import crafttweaker.api.event.MTEventManager;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.mc1120.CraftTweaker;
import crafttweaker.util.EventList;
import crafttweaker.util.SuppressErrorFlag;
import crafttweaker.zenscript.GlobalRegistry;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.StringUtils;
import net.minecraftforge.fml.common.Loader;
import stanhebben.zenscript.TypeExpansion;
import stanhebben.zenscript.type.expand.ZenExpandMember;
import stanhebben.zenscript.type.natives.JavaMethod;
import youyihj.zenutils.ZenUtils;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @author youyihj
 */
public final class InternalUtils {
    private InternalUtils() {}

    public static boolean hardFailMode = false;

    private static boolean suppressErrorSingleScriptMode = false;
    private static boolean isFirstSetMode = true;
    private static final Map<String, SuppressErrorFlag> suppressErrorScriptMap = new HashMap<>();

    @SuppressWarnings("rawtypes")
    private static final List<EventList> ALL_EVENT_LISTS = new ArrayList<>();

    public static void checkDataMap(IData data) {
        if (!(data instanceof DataMap)) {
            CraftTweakerAPI.logError("data argument must be DataMap", new IllegalArgumentException());
        }
    }

    public static void checkCraftTweakerVersion() {
        try {
            Field field = CraftTweaker.class.getField("alreadyChangedThePlayer");
            if (!Modifier.isPublic(field.getModifiers())) {
                throw new RuntimeException("crafttweaker version must be 4.1.20.651 or above!");
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

    }

    public static boolean hasMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        try {
            clazz.getMethod(methodName, parameterTypes);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    public static boolean isContentTweakerInstalled() {
        return Loader.isModLoaded("contenttweaker");
    }

    public static boolean onSuppressErrorSingleScriptMode() {
        return suppressErrorSingleScriptMode;
    }

    public static void doSuppressErrorSingleScriptMode() {
        suppressErrorSingleScriptMode = true;
        if (isFirstSetMode) {
            CraftTweakerAPI.logInfo("ZenUtils' suppress error in single script mode is enable.");
            CraftTweakerAPI.logInfo("#ikwid and #nowarn preprocessors of vanilla CraftTweaker are useless now.");
        }
        isFirstSetMode = false;
    }

    private static String getLastZenScriptStack() {
        for (StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()) {
            String fileName = stackTraceElement.getFileName();
            if (!StringUtils.isNullOrEmpty(fileName) && fileName.endsWith(".zs")) {
                return fileName;
            }
        }
        return "";
    }

    @Nonnull
    public static SuppressErrorFlag getCurrentSuppressErrorFlag() {
        return Optional.ofNullable(suppressErrorScriptMap.get(getLastZenScriptStack())).orElse(SuppressErrorFlag.DEFAULT);
    }

    public static void putSuppressErrorFlag(String zsName, SuppressErrorFlag errorFlag) {
        suppressErrorScriptMap.put(zsName, errorFlag);
    }

    public static Optional<EntityPlayerMP> getPlayer(UUID uuid) {
        return Optional.ofNullable(CraftTweakerAPI.server)
                .map(CraftTweakerMC::getMCServer)
                .map(MinecraftServer::getPlayerList)
                .map(playerList -> playerList.getPlayerByUUID(uuid));
    }

    @SuppressWarnings("unchecked")
    public static void scanAllEventLists() throws NoSuchFieldException {
        try {
            ALL_EVENT_LISTS.addAll(ReflectUtils.getAllFieldsWithClass(MTEventManager.class, EventList.class, CraftTweakerAPI.events));
        } catch (IllegalAccessException e) {
            ZenUtils.forgeLogger.error("Failed to get vanilla CraftTweaker Event List!", e);
        }
        Field membersField = ReflectUtils.removePrivate(TypeExpansion.class, "members");
        Field methodsField = ReflectUtils.removePrivate(ZenExpandMember.class, "methods");
        TypeExpansion eventMangerExpansion = GlobalRegistry.getExpansions().get("crafttweaker.events.IEventManager");
        if (eventMangerExpansion == null) return;
        try {
            Map<String, ZenExpandMember> expandMembers = (Map<String, ZenExpandMember>) membersField.get(eventMangerExpansion);
            List<Class<?>> lookupClasses = new ArrayList<>();
            for (ZenExpandMember expandMember : expandMembers.values()) {
                List<?> list = (List<?>) methodsField.get(expandMember);
                if (list.isEmpty())
                    continue;
                Object javaMethod = list.get(0);
                Class<JavaMethod> javaMethodClass = JavaMethod.class;
                if (javaMethodClass.isInstance(javaMethod)) {
                    Class<?> owner = javaMethodClass.cast(javaMethod).getOwner();
                    if (!lookupClasses.contains(owner)) {
                        lookupClasses.add(owner);
                        ALL_EVENT_LISTS.addAll(ReflectUtils.getAllFieldsWithClass(owner, EventList.class, null));
                    }
                }
            }
        } catch (IllegalAccessException | IllegalArgumentException | NullPointerException e) {
            ZenUtils.forgeLogger.error("Failed to get event manager expansions.", e);
        }
    }

    @SuppressWarnings("rawtypes")
    public static List<EventList> getAllEventLists() {
        return ImmutableList.copyOf(ALL_EVENT_LISTS);
    }
}
