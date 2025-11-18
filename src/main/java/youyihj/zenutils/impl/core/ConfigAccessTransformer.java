package youyihj.zenutils.impl.core;

import com.google.common.io.CharSource;
import net.minecraftforge.fml.common.asm.transformers.AccessTransformer;

import java.io.IOException;

/**
 * @author youyihj
 */
public class ConfigAccessTransformer extends AccessTransformer {
    public ConfigAccessTransformer() throws IOException {
        super("META-INF/dummy_zenutils_at.cfg");
        processATFile(CharSource.wrap(String.join("\n", Configuration.accessTransformers)));
    }
}
