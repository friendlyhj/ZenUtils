package youyihj.zenutils.api.ftbq.event;

import com.feed_the_beast.ftbquests.events.ObjectCompletedEvent;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.ftbq.ObjectCompletedEvent")
@ModOnly("ftbquests")
public class CTObjectCompletedEvent {
    public CTObjectCompletedEvent(ObjectCompletedEvent<?> event) {
        this.event = event;
    }

    private final ObjectCompletedEvent<?> event;

    @ZenGetter("onlineMembers")
    public List<IPlayer> getOnlineMembers() {
        return event.getOnlineMembers().stream().map(CraftTweakerMC::getIPlayer).collect(Collectors.toList());
    }

    @ZenGetter("notifyPlayers")
    public List<IPlayer> getNotifyPlayers() {
        return event.getNotifiedPlayers().stream().map(CraftTweakerMC::getIPlayer).collect(Collectors.toList());
    }
}
