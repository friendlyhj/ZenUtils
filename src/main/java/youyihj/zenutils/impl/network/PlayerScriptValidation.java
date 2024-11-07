package youyihj.zenutils.impl.network;

import com.google.common.base.Suppliers;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import stanhebben.zenscript.ZenModule;
import youyihj.zenutils.ZenUtils;
import youyihj.zenutils.api.world.ZenUtilsWorld;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Supplier;

/**
 * @author youyihj
 */
public class PlayerScriptValidation {
    public static boolean isZenScriptAnonymousFunction(byte[] bytecodes) {
        ClassReader cr = new ClassReader(bytecodes);
        ClassNode classNode = new ClassNode();
        cr.accept(classNode, ClassReader.SKIP_CODE);
        return classNode.interfaces.size() == 1 && // anonymous functions only implement one interface
                !(
                        classNode.methods.stream().anyMatch(it -> "__script__".equals(it.name)) || // plain script
                        (classNode.access & Opcodes.ACC_SYNTHETIC) != 0 || // synthetic class
                        classNode.name.startsWith("any") || // IAny class
                        "__ZenMain__".equals(classNode.name) // main class
                );
    }

    @Mod.EventBusSubscriber(Side.CLIENT)
    public static final class ClientEventHandler {
        @SubscribeEvent
        public static void onEntityJoin(EntityJoinWorldEvent event) {
            if (ZenUtilsNetworkHandler.INSTANCE.shouldSendScripts() && runsOnMultiplayer() && event.getWorld().isRemote && event.getEntity().getUniqueID().equals(Minecraft.getMinecraft().player.getUniqueID())) {
                sendScriptsToServer();
            }
        }

        private static void sendScriptsToServer() {
            for (Map.Entry<String, byte[]> entry : ZenModule.classes.entrySet()) {
                String name = entry.getKey();
                byte[] bytecode = entry.getValue();
                if (bytecode.length != 0 && isZenScriptAnonymousFunction(bytecode)) {
                    ZenUtilsNetworkHandler.INSTANCE.sendValidateScriptMessage(bytecode, name);
                }
            }
        }

        private static boolean runsOnMultiplayer() {
            return FMLCommonHandler.instance().getMinecraftServerInstance() == null;
        }
    }

    public static final class ServerEventHandler {
        /* package-private */ static Multimap<EntityPlayerMP, String> validatedScriptsByPlayer = Multimaps.newSetMultimap(new WeakHashMap<>(), HashSet::new);

        private static final Supplier<Set<String>> shouldValidatedScripts = Suppliers.memoize(() -> {
            Set<String> scripts = new HashSet<>();
            for (Map.Entry<String, byte[]> entry : ZenModule.classes.entrySet()) {
                String name = entry.getKey();
                byte[] bytecode = entry.getValue();
                if (bytecode.length != 0 && isZenScriptAnonymousFunction(bytecode)) {
                    scripts.add(name);
                }
            }
            return scripts;
        });

        @SubscribeEvent
        public static void onEntityJoin(EntityJoinWorldEvent event) {
            Entity entity = event.getEntity();
            if (entity instanceof EntityPlayerMP) {
                EntityPlayerMP player = (EntityPlayerMP) entity;
                ZenUtilsWorld.catenation(CraftTweakerMC.getIWorld(event.getWorld()))
                        .sleep(100)
                        .then((w, ctx) -> {
                            if (!validatedScriptsByPlayer.containsKey(player) || !validatedScriptsByPlayer.get(player).equals(shouldValidatedScripts.get())) {
                                player.connection.disconnect(new TextComponentTranslation("message.zenutils.validate"));
                            } else {
                                ZenUtils.forgeLogger.info("Validated scripts of client {}", player.getName());
                            }
                        })
                        .stopWhen((w, ctx) -> !entity.isEntityAlive() || !entity.isAddedToWorld())
                        .start();
            }
        }
    }
}
