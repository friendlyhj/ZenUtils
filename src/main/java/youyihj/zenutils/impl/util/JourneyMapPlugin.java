package youyihj.zenutils.impl.util;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IWorld;
import journeymap.client.api.ClientPlugin;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.IClientPlugin;
import journeymap.client.api.display.Waypoint;
import journeymap.client.api.event.ClientEvent;
import youyihj.zenutils.ZenUtils;

import java.util.Objects;

/**
 * @author youyihj
 */
@ClientPlugin
public class JourneyMapPlugin implements IClientPlugin {
    public static IClientAPI japi;

    public static void createWaypoint(String name, IBlockPos pos, IWorld world, int color) {
        if (japi == null) {
            CraftTweakerAPI.logError("JourneyMap API is not built yet!");
        }
        String id = ZenUtils.MODID + "-" + name + "-" + Objects.hash(pos.getInternal());
        Waypoint waypoint = new Waypoint(ZenUtils.MODID, id, name, world.getDimension(), CraftTweakerMC.getBlockPos(pos));
        waypoint.setColor(color);
        waypoint.setDirty();
        try {
            japi.show(waypoint);
        } catch (Exception e) {
            ZenUtils.forgeLogger.throwing(e);
        }
    }

    @Override
    public void initialize(IClientAPI jmClientApi) {
        japi = jmClientApi;
    }

    @Override
    public String getModId() {
        return ZenUtils.MODID;
    }

    @Override
    public void onEvent(ClientEvent event) {

    }
}
