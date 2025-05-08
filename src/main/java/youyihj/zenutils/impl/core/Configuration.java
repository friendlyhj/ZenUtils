package youyihj.zenutils.impl.core;

import com.cleanroommc.configanytime.ConfigAnytime;
import net.minecraftforge.common.config.Config;
import youyihj.zenutils.Reference;

/**
 * @author youyihj
 */
@Config(modid = Reference.MODID, name = Reference.NAME)
public class Configuration {
    @Config.Comment("Enable mixinzs. Default: false")
    public static boolean enableMixin = false;

    @Config.Comment("Enable RandomTickEvent. It patches hotspot random tick code, which may produce performance loss. Do not enable it if you don't need it. Default: false")
    public static boolean enableRandomTickEvent = false;

    @Config.Comment("Enable fast script loading. Note: If enabled, ONLY PREPROCESSORS AT SCRIPT HEADER ARE EFFECTIVE. (except mixin scripts) Default: false")
    public static boolean fastScriptLoading = false;

    static {
        ConfigAnytime.register(Configuration.class);
    }
}
