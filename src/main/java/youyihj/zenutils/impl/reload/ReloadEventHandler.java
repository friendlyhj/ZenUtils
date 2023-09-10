package youyihj.zenutils.impl.reload;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.CrafttweakerImplementationAPI;
import crafttweaker.api.entity.IEntityDefinition;
import crafttweaker.api.event.MTEventManager;
import crafttweaker.api.event.PlayerLoggedInEvent;
import crafttweaker.api.event.PlayerLoggedOutEvent;
import crafttweaker.mc1120.entity.MCEntityDefinition;
import crafttweaker.util.EventList;
import crafttweaker.util.IEventHandler;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import youyihj.zenutils.ZenUtils;
import youyihj.zenutils.api.cotx.brackets.LateGetContentLookup;
import youyihj.zenutils.api.reload.ScriptReloadEvent;
import youyihj.zenutils.api.util.ZenUtilsGlobal;
import youyihj.zenutils.impl.util.InternalUtils;
import youyihj.zenutils.impl.util.ReflectUtils;
import youyihj.zenutils.impl.util.ScriptStatus;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author youyihj
 */
@Mod.EventBusSubscriber
public class ReloadEventHandler {
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onReloadPre(ScriptReloadEvent.Pre event) {
        InternalUtils.setScriptStatus(ScriptStatus.RELOAD);
        InternalUtils.getAllEventLists().forEach(EventList::clear);
        reRegisterInternalEvents();
        refreshEntityDrops();
        // remove duplicate recipe name warning, since we don't register new recipes
        ZenUtilsGlobal.addRegexLogFilter("Recipe name \\[.*\\] has duplicate uses, defaulting to calculated hash!");

        int reloadActionCount = ZenUtils.tweaker.getReloadableActions().size();
        if (reloadActionCount != 0) {
            event.getRequester().sendMessage(new TextComponentString(reloadActionCount + " actions reloaded."));
        }
        ZenUtils.tweaker.onReload();
    }

    @SubscribeEvent
    public static void onReloadPost(ScriptReloadEvent.Post event) {
        if (InternalUtils.isContentTweakerInstalled()) {
            LateGetContentLookup.refreshFields();
            LateGetContentLookup.clear();
        }
        InternalUtils.setScriptStatus(ScriptStatus.STARTED);
    }

    @SuppressWarnings("unchecked")
    private static void reRegisterInternalEvents() {
        try {
            Field listenLogin = ReflectUtils.removePrivate(CrafttweakerImplementationAPI.class, "LISTEN_LOGIN");
            Field listenLogout = ReflectUtils.removePrivate(CrafttweakerImplementationAPI.class, "LISTEN_LOGOUT");
            MTEventManager events = CrafttweakerImplementationAPI.events;
            events.onPlayerLoggedIn((IEventHandler<PlayerLoggedInEvent>) listenLogin.get(null));
            events.onPlayerLoggedOut((IEventHandler<PlayerLoggedOutEvent>) listenLogout.get(null));
            events.onPlayerInteract(CrafttweakerImplementationAPI.LISTEN_BLOCK_INFO);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static void refreshEntityDrops() {
        List<IEntityDefinition> entityDefinitions = CraftTweakerAPI.game.getEntities();
        entityDefinitions.clear();
        ForgeRegistries.ENTITIES.forEach(it -> entityDefinitions.add(new MCEntityDefinition(it)));
    }
}
