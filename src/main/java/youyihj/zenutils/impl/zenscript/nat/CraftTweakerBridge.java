package youyihj.zenutils.impl.zenscript.nat;

import crafttweaker.api.minecraft.CraftTweakerMC;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author youyihj
 */
public enum CraftTweakerBridge {
    INSTANCE;

    private final Map<Class<?>, Method> toNativeCasters = new HashMap<>();
    private final Map<Class<?>, Method> toWrapperCasters = new HashMap<>();

    CraftTweakerBridge() {
        for (Method method : CraftTweakerMC.class.getMethods()) {
            if (method.getName().startsWith("get") && method.getParameterCount() == 1) {
                Class<?> toConvert = method.getParameterTypes()[0];
                String toConvertClassName = toConvert.getCanonicalName();
                if (toConvertClassName.startsWith("crafttweaker.")) {
                    toNativeCasters.put(toConvert, method);
                } else if (toConvertClassName.startsWith("net.minecraft")) {
                    toWrapperCasters.put(toConvert, method);
                }
            }
        }
    }

    public Optional<Method> getNativeCaster(final Class<?> clazz) {
        return Optional.ofNullable(toNativeCasters.get(clazz));
    }

    public Optional<Method> getWrapperCaster(final Class<?> clazz) {
        return Optional.ofNullable(toWrapperCasters.get(clazz));
    }

}
