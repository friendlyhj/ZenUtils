package youyihj.zenutils.impl.util.catenation;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.world.IWorld;
import youyihj.zenutils.api.util.catenation.Catenation;

/**
 * @author youyihj
 */
public class CatenationBuilder extends AbstractCatenationBuilder {
    private final IWorld world;

    public CatenationBuilder(IWorld world) {
        this.world = world;
    }

    @Override
    protected void register(Catenation catenation) {
        if (world.isRemote()) {
            CraftTweakerAPI.logWarning("This catenation is only run on server, but the world is on client side.");
            CraftTweakerAPI.logWarning("If it is expected, please call `client.catenation()` to build catenation that is run on client.");
        } else {
            CatenationManager.addCatenation(CraftTweakerMC.getWorld(world), catenation);
        }
    }
}
