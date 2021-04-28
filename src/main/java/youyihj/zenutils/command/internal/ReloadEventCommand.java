package youyihj.zenutils.command.internal;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.mc1120.commands.CraftTweakerCommand;
import crafttweaker.runtime.ScriptLoader;
import crafttweaker.util.EventList;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import stanhebben.zenscript.ZenModule;
import youyihj.zenutils.ZenUtils;
import youyihj.zenutils.util.InternalUtils;

import static crafttweaker.mc1120.commands.SpecialMessagesChat.getClickableCommandText;
import static crafttweaker.mc1120.commands.SpecialMessagesChat.getNormalMessage;

public class ReloadEventCommand extends CraftTweakerCommand {
    public ReloadEventCommand() {
        super("reloadevents");
    }

    @Override
    protected void init() {
        setDescription(getClickableCommandText("\u00A72/ct reloadevents", "/ct reloadevents", true), getNormalMessage(" \u00A73Reload event handlers"));
    }

    @Override
    public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) {
        sender.sendMessage(getNormalMessage("\u00A7bBeginning reload for events"));
        InternalUtils.getAllEventLists().forEach(EventList::clear);
        ZenUtils.tweaker.freezeActionApplying();
        ZenModule.loadedClasses.clear();
        final ScriptLoader loader = CraftTweakerAPI.tweaker.getOrCreateLoader("crafttweaker", "recipeEvent");
        loader.setMainName("crafttweaker");
        loader.setLoaderStage(ScriptLoader.LoaderStage.NOT_LOADED);
        CraftTweakerAPI.tweaker.loadScript(false, loader);
        sender.sendMessage(getNormalMessage("Reload for events successfully"));
    }
}
