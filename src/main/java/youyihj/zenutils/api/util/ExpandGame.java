package youyihj.zenutils.api.util;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.game.IGame;
import crafttweaker.api.potions.IPotion;
import crafttweaker.mc1120.brackets.BracketHandlerPotion;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenMethod;

/**
 * @author youyihj
 */
@ZenRegister
@ZenExpansion("crafttweaker.game.IGame")
public class ExpandGame {
    @ZenMethod
    public static IPotion getPotion(IGame game, String name) {
        return BracketHandlerPotion.getPotion(name);
    }
}
