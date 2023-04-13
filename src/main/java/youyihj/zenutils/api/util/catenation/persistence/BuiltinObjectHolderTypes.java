package youyihj.zenutils.api.util.catenation.persistence;

import crafttweaker.api.data.IData;
import crafttweaker.api.entity.IEntity;
import crafttweaker.api.player.IPlayer;
import crafttweaker.api.world.IBlockPos;
import youyihj.zenutils.ZenUtils;
import youyihj.zenutils.impl.util.catenation.persistence.BlockPosObjectHolder;
import youyihj.zenutils.impl.util.catenation.persistence.DataObjectHolder;
import youyihj.zenutils.impl.util.catenation.persistence.EntityObjectHolder;
import youyihj.zenutils.impl.util.catenation.persistence.PlayerObjectHolder;

/**
 * @author youyihj
 */
public class BuiltinObjectHolderTypes {
    public static final ICatenationObjectHolder.Type<IPlayer> PLAYER = ICatenationObjectHolder.Type.of(PlayerObjectHolder::new, IPlayer.class, ZenUtils.rl("player"));
    public static final ICatenationObjectHolder.Type<IBlockPos> POSITION = ICatenationObjectHolder.Type.of(BlockPosObjectHolder::new, IBlockPos.class, ZenUtils.rl("pos"));
    public static final ICatenationObjectHolder.Type<IData> DATA = ICatenationObjectHolder.Type.of(DataObjectHolder::new, IData.class, ZenUtils.rl("data"));
    public static final ICatenationObjectHolder.Type<IEntity> ENTITY = ICatenationObjectHolder.Type.of(EntityObjectHolder::new, IEntity.class, ZenUtils.rl("entity"));

}
