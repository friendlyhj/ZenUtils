package youyihj.zenutils.impl.config;

import net.minecraft.launchwrapper.IClassTransformer;

import java.util.HashMap;
import java.util.Map;

public class ClassProvider implements IClassTransformer {
    public static final Map<String, byte[]> classes = new HashMap<>();

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        return classes.getOrDefault(transformedName, basicClass);
    }
}
