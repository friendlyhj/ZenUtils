package youyihj.zenutils.api.util.catenation.persistence;

import crafttweaker.api.player.IPlayer;
import youyihj.zenutils.impl.util.catenation.persistence.PlayerObjectHolder;

/**
 * @author youyihj
 */
public class BuiltinObjectHolderTypes {
    public static final ICatenationObjectHolder.Type<IPlayer> PLAYER = ICatenationObjectHolder.Type.of(PlayerObjectHolder::new, IPlayer.class);
}
