package youyihj.zenutils.impl.util;

import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;
import crafttweaker.api.data.DataMap;
import crafttweaker.api.data.IData;
import crafttweaker.util.EventList;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.common.Loader;
import youyihj.zenutils.Reference;
import youyihj.zenutils.impl.member.ClassDataFetcher;
import youyihj.zenutils.impl.member.bytecode.BytecodeClassDataFetcher;
import youyihj.zenutils.impl.member.reflect.ReflectionClassDataFetcher;
import youyihj.zenutils.impl.runtime.InvalidCraftTweakerVersionException;
import youyihj.zenutils.impl.runtime.ScriptStatus;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Type;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author youyihj
 */
public final class InternalUtils {
    private static final List<Runnable> ALL_EVENT_LISTS_CLEAR_ACTIONS = new ArrayList<>();
    private static final BytecodeClassDataFetcher CLASS_DATA_FETCHER = new BytecodeClassDataFetcher(
            new BytecodeClassDataFetcher(new ReflectionClassDataFetcher(Launch.classLoader), new LaunchClassLoaderBytesProvider()),
            Collections.singletonList(Paths.get("mods"))
    );
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
        return Loader.isModLoaded(Reference.MOD_COT);
    }

    public static void registerEventList(EventList<?> eventList) {
        ALL_EVENT_LISTS_CLEAR_ACTIONS.add(eventList::clear);
    }

    public static void cleanAllEventLists() {
        ALL_EVENT_LISTS_CLEAR_ACTIONS.forEach(Runnable::run);
    }

    public static void setScriptStatus(ScriptStatus scriptStatus) {
        InternalUtils.scriptStatus = scriptStatus;
    }

    public static ScriptStatus getScriptStatus() {
        return scriptStatus;
    }

    public static Object cloneArray(Object array) {
        Class<?> arrayClass = array.getClass();
        Preconditions.checkArgument(arrayClass.isArray(), "argument should be an array");
        try {
            return MethodHandles.publicLookup()
                                .findVirtual(arrayClass, "clone", MethodType.methodType(Object.class))
                                .bindTo(array)
                                .invokeExact();
        } catch (Throwable e) {
            throw new RuntimeException("Failed to clone the array", e);
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    public static <T extends U, U> Type getSingleItfGenericVariable(Class<T> type, Class<U> itf) {
        return TypeToken.of(type).getSupertype(itf).resolveType(itf.getTypeParameters()[0]).getType();
    }

    public static <T> T make(T origin, Consumer<T> consumer) {
        consumer.accept(origin);
        return origin;
    }

    @SuppressWarnings("unchecked")
    public static <T> T cast(Object o) {
        return (T)o;
    }

    public static ClassDataFetcher getClassDataFetcher() {
        return CLASS_DATA_FETCHER;
    }
}
