package youyihj.zenutils.impl.reload;

import crafttweaker.CrafttweakerImplementationAPI;
import crafttweaker.api.event.MTEventManager;
import crafttweaker.api.event.PlayerLoggedInEvent;
import crafttweaker.api.event.PlayerLoggedOutEvent;
import crafttweaker.util.EventList;
import crafttweaker.util.IEventHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import youyihj.zenutils.ZenUtils;
import youyihj.zenutils.api.cotx.brackets.LateGetContentLookup;
import youyihj.zenutils.api.reload.ScriptReloadEvent;
import youyihj.zenutils.api.util.ZenUtilsGlobal;
import youyihj.zenutils.impl.util.InternalUtils;
import youyihj.zenutils.impl.util.ReflectUtils;

import java.lang.reflect.Field;

/**
 * @author youyihj
 */
@Mod.EventBusSubscriber
public class ReloadEventHandler {
    @SubscribeEvent
    public static void onReloadPre(ScriptReloadEvent.Pre event) {
        InternalUtils.getAllEventLists().forEach(EventList::clear);
        reRegisterInternalEvents();
        // remove duplicate recipe name warning, since we don't register new recipes
        ZenUtilsGlobal.addRegexLogFilter("Recipe name \\[.*\\] has duplicate uses, defaulting to calculated hash!");

        if (InternalUtils.isContentTweakerInstalled()) {
            LateGetContentLookup.refreshFields();
            LateGetContentLookup.clear();
        }

        ZenUtils.tweaker.onReload();
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
}
