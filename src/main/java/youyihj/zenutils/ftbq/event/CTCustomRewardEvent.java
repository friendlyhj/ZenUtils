package youyihj.zenutils.ftbq.event;

import com.feed_the_beast.ftbquests.events.CustomRewardEvent;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import youyihj.zenutils.ftbq.CTReward;

/**
 * @author youyihj
 */
@ZenRegister
@ZenClass("mods.zenutils.ftbq.CustomRewardEvent")
@ModOnly("ftbquests")
public class CTCustomRewardEvent {
    private final CustomRewardEvent event;

    public CTCustomRewardEvent(CustomRewardEvent event) {
        this.event = event;
    }

    @ZenGetter("player")
    public IPlayer getPlayer() {
        return CraftTweakerMC.getIPlayer(event.getPlayer());
    }

    @ZenGetter("notify")
    public boolean getNotify() {
        return event.getNotify();
    }

    @ZenGetter("reward")
    public CTReward getReward() {
        return new CTReward(event.getReward());
    }
}
