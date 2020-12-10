package youyihj.zenutils.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import youyihj.zenutils.ZenUtils;

import javax.annotation.Nullable;

/**
 * @author youyihj
 */
@Mod.EventBusSubscriber
public class ZenWorldCapabilityHandler {
    @CapabilityInject(IZenWorldCapability.class)
    public static Capability<IZenWorldCapability> ZEN_WORLD_CAPABILITY = null;

    @SubscribeEvent
    public static void attachToWorld(AttachCapabilitiesEvent<World> event) {
        event.addCapability(new ResourceLocation(ZenUtils.MODID, "zen_world_cap"), new ZenWorldCapabilityProvider());
    }

    @SubscribeEvent
    public static void attachToChunk(AttachCapabilitiesEvent<Chunk> event) {
        event.addCapability(new ResourceLocation(ZenUtils.MODID, "zen_world_cap"), new ZenWorldCapabilityProvider());
    }

    public static void register() {
        CapabilityManager.INSTANCE.register(IZenWorldCapability.class, new Capability.IStorage<IZenWorldCapability>() {
            @Nullable
            @Override
            public NBTBase writeNBT(Capability<IZenWorldCapability> capability, IZenWorldCapability instance, EnumFacing side) {
                return instance.serializeNBT();
            }

            @Override
            public void readNBT(Capability<IZenWorldCapability> capability, IZenWorldCapability instance, EnumFacing side, NBTBase nbt) {
                if (nbt instanceof NBTTagCompound) {
                    instance.deserializeNBT(((NBTTagCompound) nbt));
                }
            }
        }, ZenWorldCapability::new);
    }
}
