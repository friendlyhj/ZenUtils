package youyihj.zenutils.impl.mixin.crafttweaker;

import crafttweaker.api.event.IPlayerEvent;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import crafttweaker.mc1120.events.handling.MCPlayerAnvilUpdateEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.AnvilUpdateEvent;
import org.spongepowered.asm.mixin.*;
import youyihj.zenutils.Reference;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/**
 * @author youyihj
 */
@Mixin(value = MCPlayerAnvilUpdateEvent.class, remap = false)
@Implements(@Interface(iface = IPlayerEvent.class, prefix = "zu$"))
public abstract class MixinMCPlayerAnvilUpdateEvent {
    @Shadow
    @Final
    private AnvilUpdateEvent event;

    @Unique
    private static final MethodHandle zu$playerGetter;

    static {
        MethodHandles.Lookup lookup = MethodHandles.publicLookup();
        try {
            //noinspection JavaLangInvokeHandleSignature
            zu$playerGetter = Reference.IS_CLEANROOM
                    ? lookup.findVirtual(AnvilUpdateEvent.class, "getPlayer", MethodType.methodType(EntityPlayer.class))
                    : lookup.findVirtual(AnvilUpdateEvent.class, "zu$getPlayer", MethodType.methodType(EntityPlayer.class));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException("AnvilUpdateEvent#getPlayer() is not patched", e);
        }
    }

    public IPlayer zu$getPlayer() {
        try {
            return CraftTweakerMC.getIPlayer((EntityPlayer) zu$playerGetter.invokeExact(event));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
