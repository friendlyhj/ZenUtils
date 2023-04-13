package youyihj.zenutils.impl.event;

import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import youyihj.zenutils.api.util.catenation.persistence.BuiltinObjectHolderTypes;
import youyihj.zenutils.api.util.catenation.persistence.CatenationPersistenceAPI;
import youyihj.zenutils.api.util.catenation.persistence.ICatenationObjectHolder;
import youyihj.zenutils.impl.util.FireEntityRemoveEventListener;
import youyihj.zenutils.impl.util.catenation.persistence.CatenationPersistenceImpl;

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
            CatenationPersistenceImpl.onWorldLoad(CraftTweakerMC.getIWorld(world));
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
    public static void registerObjectHolder(RegistryEvent.Register<ICatenationObjectHolder.TypeRegistryEntry> event) {
        event.getRegistry().registerAll(
                BuiltinObjectHolderTypes.PLAYER.getRegistryEntry(),
                BuiltinObjectHolderTypes.ENTITY.getRegistryEntry(),
                BuiltinObjectHolderTypes.DATA.getRegistryEntry(),
                BuiltinObjectHolderTypes.POSITION.getRegistryEntry()
        );
    }
}
