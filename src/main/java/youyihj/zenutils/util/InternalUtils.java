package youyihj.zenutils.util;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.data.DataMap;
import crafttweaker.api.data.IData;
import crafttweaker.api.world.IBlockPos;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * @author youyihj
 */
public final class InternalUtils {
    private InternalUtils() {}

    public static void checkDataMap(IData data) {
        if (!(data instanceof DataMap)) {
            CraftTweakerAPI.logError("data argument must be DataMap", new IllegalArgumentException());
        }
    }

    public static void checkCrTVersion() {
        try {
            Class<?> clazz = Class.forName("crafttweaker.mc1120.util.expand.ExpandAxisAlignedBB"); // for 603
            Method method = clazz.getDeclaredMethod("create", IBlockPos.class);
            if (!Modifier.isStatic(method.getModifiers())) { // for 604
                throw new NoSuchMethodException();
            }
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            throw new RuntimeException("crafttweaker version must be 4.1.20.604 or above!");
        }
    }
}
