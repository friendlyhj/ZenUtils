package youyihj.zenutils.impl.network;

import it.unimi.dsi.fastutil.objects.Object2BooleanArrayMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import stanhebben.zenscript.ZenModule;

import java.util.Arrays;
import java.util.UUID;

/**
 * @author youyihj
 */
public class ScriptValidator {
    private static final Object2BooleanMap<UUID> validateResult = new Object2BooleanArrayMap<>();

    public static void validate(UUID playerUUID, String name, byte[] bytes) {
        validateResult.put(playerUUID, ZenModule.classes.containsKey(name) && Arrays.equals(ZenModule.classes.get(name), bytes));
    }

    public static boolean getValidateResult(UUID playerUUID) {
        return validateResult.getBoolean(playerUUID);
    }
}
