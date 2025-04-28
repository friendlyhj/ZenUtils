package youyihj.zenutils.impl.core;

import com.cleanroommc.configanytime.ConfigAnytime;
import net.minecraftforge.common.config.Config;
import youyihj.zenutils.Reference;

/**
 * @author youyihj
 */
@Config(modid = Reference.MODID, name = Reference.NAME)
public class Configuration {
    public static boolean enableMixin = false;
    public static boolean enableRandomTickEvent = false;

    static {
        ConfigAnytime.register(Configuration.class);
    }
}
