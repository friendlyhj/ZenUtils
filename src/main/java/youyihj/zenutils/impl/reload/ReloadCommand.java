package youyihj.zenutils.impl.reload;

import com.google.common.base.Suppliers;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.mc1120.commands.CraftTweakerCommand;
import crafttweaker.runtime.ScriptLoader;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import stanhebben.zenscript.ZenModule;
import youyihj.zenutils.ZenUtils;
import youyihj.zenutils.api.reload.ScriptReloadEvent;

import java.util.function.Supplier;

import static crafttweaker.mc1120.commands.SpecialMessagesChat.getClickableCommandText;
import static crafttweaker.mc1120.commands.SpecialMessagesChat.getNormalMessage;

public class ReloadCommand extends CraftTweakerCommand {
    private static final String SCRIPT_LOADER_NAME = "reloadable";
    public static final Supplier<ScriptLoader> RELOADABLE_LOADER = Suppliers.memoize(() -> {
        ScriptLoader loader = CraftTweakerAPI.tweaker.getOrCreateLoader(SCRIPT_LOADER_NAME);
        loader.setMainName(SCRIPT_LOADER_NAME);
        // compatible to old scripts
        loader.addAliases("reloadableevents");
        return loader;
    });

    public ReloadCommand() {
        super("reload");
    }

    @Override
    protected void init() {
        setDescription(
                getClickableCommandText(TextFormatting.DARK_GREEN + "/ct reload", "/ct reload", true),
                getNormalMessage(TextFormatting.DARK_AQUA + "Reload reloadable scripts")
        );
    }

    @Override
    public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) {
        if (server.isDedicatedServer()) {
            sender.sendMessage(getNormalMessage(TextFormatting.DARK_RED + "The command only can be run in integrated server (SinglePlayer)!"));
            return;
        }
        sender.sendMessage(getNormalMessage(TextFormatting.AQUA + "Beginning reload scripts"));
        sender.sendMessage(getNormalMessage("Only scripts that marked " + TextFormatting.GRAY + "#loader reloadable " + TextFormatting.RESET + "can be reloaded."));
        sender.sendMessage(getNormalMessage(TextFormatting.YELLOW + "Most recipe modifications are not reloadable, they will be ignored."));
        ZenUtils.tweaker.freezeActionApplying();
        ZenModule.loadedClasses.clear();
        ZenUtils.crafttweakerLogger.clear();
        MinecraftForge.EVENT_BUS.post(new ScriptReloadEvent.Pre(sender));
        ScriptLoader loader = RELOADABLE_LOADER.get();
        loader.setLoaderStage(ScriptLoader.LoaderStage.NOT_LOADED);
        CraftTweakerAPI.tweaker.loadScript(false, loader);
        if (loader.getLoaderStage() == ScriptLoader.LoaderStage.ERROR) {
            sender.sendMessage(getNormalMessage(TextFormatting.DARK_RED + "Failed to reload scripts"));
        } else {
            sender.sendMessage(getNormalMessage("Reloaded successfully"));
        }
        MinecraftForge.EVENT_BUS.post(new ScriptReloadEvent.Post(sender));
    }
}
