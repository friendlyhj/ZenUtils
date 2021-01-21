package youyihj.zenutils.util;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.data.DataMap;
import crafttweaker.api.data.IData;
import crafttweaker.util.SuppressErrorFlag;
import net.minecraft.util.StringUtils;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author youyihj
 */
public final class InternalUtils {
    private InternalUtils() {}

    private static boolean suppressErrorSingleScriptMode = false;
    private static boolean isFirstSetMode = true;
    private static final Map<String, SuppressErrorFlag> suppressErrorScriptMap = new HashMap<>();

    public static void checkDataMap(IData data) {
        if (!(data instanceof DataMap)) {
            CraftTweakerAPI.logError("data argument must be DataMap", new IllegalArgumentException());
        }
    }

    public static void checkCrTVersion() {
        try {
            Class.forName("crafttweaker.util.SuppressErrorFlag");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("crafttweaker version must be 4.1.20.608 or above!");
        }
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
}
