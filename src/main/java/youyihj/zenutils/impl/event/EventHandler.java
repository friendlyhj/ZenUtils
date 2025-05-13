package youyihj.zenutils.impl.event;

import crafttweaker.api.item.IIngredient;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.mc1120.events.ActionApplyEvent;
import crafttweaker.mc1120.furnace.MCFurnaceManager;
import crafttweaker.mc1120.recipes.MCRecipeManager;
import crafttweaker.mc1120.util.CraftTweakerHacks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.tuple.Pair;
import youyihj.zenutils.api.util.catenation.persistence.BuiltinObjectHolderTypes;
import youyihj.zenutils.api.util.catenation.persistence.CatenationPersistenceAPI;
import youyihj.zenutils.impl.util.FireEntityRemoveEventListener;
import youyihj.zenutils.impl.util.catenation.persistence.CatenationPersistenceImpl;

import java.util.List;

/**
 * @author youyihj
 */
@Mod.EventBusSubscriber
public class EventHandler {
    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event) {
        World world = event.getWorld();
        world.addEventListener(new FireEntityRemoveEventListener(world));
        if (!world.isRemote) {
            CatenationPersistenceImpl.loadCatenations(CraftTweakerMC.getIWorld(world));
        }
    }

    @SubscribeEvent
    public static void onEntityJoiningWorld(EntityJoinWorldEvent event) {
        Entity entity = event.getEntity();
        if (!entity.world.isRemote) {
            if (entity instanceof EntityPlayer) {
                CatenationPersistenceAPI.receiveObject(BuiltinObjectHolderTypes.PLAYER, CraftTweakerMC.getIPlayer((EntityPlayer) entity));
            }
            CatenationPersistenceAPI.receiveObject(BuiltinObjectHolderTypes.ENTITY, CraftTweakerMC.getIEntity(entity));
        }
    }

    @SubscribeEvent
    public static void clearStagedActions(ActionApplyEvent.Post event) {
        MCRecipeManager.recipesToRemove.clear();
        MCRecipeManager.recipesToAdd.clear();
        MCFurnaceManager.recipesToRemove.clear();
        MCFurnaceManager.recipesToAdd.clear();
        List<Pair<IIngredient, Boolean>> outputs = CraftTweakerHacks.getPrivateObject(MCRecipeManager.actionRemoveRecipesNoIngredients, "outputs");
        outputs.clear();;
    }
}
