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

    @Config.Comment("Disable mixin script reload warning. Default: false")
    public static boolean disableMixinScriptReloadWarning = false;

    @Config.Comment({
        "Allow insertion of zs execution during mod loading stage",
        "Format: scriptLoaderName;modid;stageMarker;before|after",
        "Valid stage marker: C (Construction) H (Preinit) I (Init) J (Postinit) A (Available)",
        "Example: custom;mekanism;J;before -> runs `#loader custom` scripts before mekanism runs its code on post-initialization stage",
        "Note: ",
        "1. On construction stage and preinit stage before crafttweaker, only native methods are available",
        "2. Many recipe modifications are staged. For example, scripts are loaded on init, but recipe modifications for crafting table and furnace are staged on postinit",
        "   It means recipe modifications MAY NOT be executed after their loading stage",
        "   ZenUtils will re-execute crafttweaker recipe modifications after all custom script load entrypoint on POSTINIT, but other crafttweaker addons not"
    })
    public static String[] customScriptEntrypoint = {};

    static {
        ConfigAnytime.register(Configuration.class);
    }
}
