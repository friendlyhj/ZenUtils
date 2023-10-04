package youyihj.zenutils.impl.player;

import com.mojang.authlib.GameProfile;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

import java.lang.ref.WeakReference;
import java.util.UUID;

/**
 * @author youyihj
 */
public class FakePlayerHolder {
    private static WeakReference<FakePlayer> playerRef;
    private static final GameProfile PROFILE = new GameProfile(UUID.fromString("c813ca2f-1514-3c3e-bdfa-a70cd84240fa"), "[ZenUtils]");

    public static FakePlayer get(WorldServer world) {
        FakePlayer ret = playerRef != null ? playerRef.get() : null;
        if (ret == null) {
            ret = FakePlayerFactory.get(world, PROFILE);
            playerRef = new WeakReference<>(ret);
        }
        return ret;
    }
}
