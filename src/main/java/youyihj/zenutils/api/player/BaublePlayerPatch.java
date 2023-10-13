package youyihj.zenutils.api.player;

import baubles.api.cap.BaublesCapabilities;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenMethod;
import youyihj.zenutils.api.item.CrTItemHandler;

/**
 * @author youyihj
 */
@ZenRegister
@ZenExpansion("crafttweaker.player.IPlayer")
@ModOnly("baubles")
public class BaublePlayerPatch {
    @ZenMethod
    public static CrTItemHandler getPlayerBaubleItemHandler(IPlayer player) {
        return CrTItemHandler.of(CraftTweakerMC.getPlayer(player).getCapability(BaublesCapabilities.CAPABILITY_BAUBLES, null));
    }
}
