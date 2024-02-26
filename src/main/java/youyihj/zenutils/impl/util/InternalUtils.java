package youyihj.zenutils.impl.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.data.DataMap;
import crafttweaker.api.data.IData;
import crafttweaker.api.event.MTEventManager;
import crafttweaker.util.EventList;
import crafttweaker.zenscript.GlobalRegistry;
import net.minecraftforge.fml.common.Loader;
import stanhebben.zenscript.TypeExpansion;
import stanhebben.zenscript.type.expand.ZenExpandMember;
import stanhebben.zenscript.type.natives.JavaMethod;
import youyihj.zenutils.ZenUtils;
import youyihj.zenutils.impl.runtime.InvalidCraftTweakerVersionException;
import youyihj.zenutils.impl.runtime.ScriptStatus;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author youyihj
 */
public final class InternalUtils {
    @SuppressWarnings("rawtypes")
    private static final List<EventList> ALL_EVENT_LISTS = new ArrayList<>();

    private static ScriptStatus scriptStatus = ScriptStatus.INIT;

    private InternalUtils() {
    }

    public static void checkDataMap(IData data) {
        Preconditions.checkArgument(data instanceof DataMap, "data argument must be DataMap");
    }

    public static void checkCraftTweakerVersion(String requiredVersion, IVersionChecker versionChecker) {
        boolean result = versionChecker.getResult();
        if (!result) {
            throw new InvalidCraftTweakerVersionException(requiredVersion);
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
        return Loader.isModLoaded(ZenUtils.MOD_COT);
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

    public static void setScriptStatus(ScriptStatus scriptStatus) {
        InternalUtils.scriptStatus = scriptStatus;
    }

    public static ScriptStatus getScriptStatus() {
        return scriptStatus;
    }
}
