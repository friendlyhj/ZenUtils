package youyihj.zenutils.api.util;

import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IWorld;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import youyihj.zenutils.impl.util.JourneyMapPlugin;

/**
 * @author youyihj
 */
@ZenClass("mods.zenutils.JourneyMap")
@ZenRegister
@ModOnly("journeymap")
public class JourneyMap {
    @ZenMethod
    public static void createWaypoint(String name, IBlockPos pos, IWorld world, int color) {
        JourneyMapPlugin.createWaypoint(name, pos, world, color);
    }
}
