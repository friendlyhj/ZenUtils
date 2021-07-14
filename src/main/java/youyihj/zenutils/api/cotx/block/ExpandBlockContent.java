package youyihj.zenutils.api.cotx.block;

import com.teamacronymcoders.contenttweaker.api.ctobjects.blockpos.MCBlockPos;
import com.teamacronymcoders.contenttweaker.api.ctobjects.blockstate.MCBlockState;
import com.teamacronymcoders.contenttweaker.api.ctobjects.entity.EntityHelper;
import com.teamacronymcoders.contenttweaker.api.ctobjects.entity.player.CTPlayer;
import com.teamacronymcoders.contenttweaker.api.ctobjects.enums.Facing;
import com.teamacronymcoders.contenttweaker.api.ctobjects.enums.Hand;
import com.teamacronymcoders.contenttweaker.api.ctobjects.world.MCWorld;
import com.teamacronymcoders.contenttweaker.modules.vanilla.blocks.BlockContent;
import crafttweaker.mc1120.util.MCPosition3f;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import youyihj.zenutils.api.cotx.annotation.ExpandContentTweakerEntry;
import youyihj.zenutils.api.cotx.tile.TileEntityContent;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * @author youyihj
 */
@ExpandContentTweakerEntry
public class ExpandBlockContent extends BlockContent {
    private final ExpandBlockRepresentation expandBlockRepresentation;

    public ExpandBlockContent(ExpandBlockRepresentation blockRepresentation) {
        super(blockRepresentation);
        this.expandBlockRepresentation = blockRepresentation;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return Objects.nonNull(expandBlockRepresentation.onBlockActivated) &&
                expandBlockRepresentation.onBlockActivated.activate(
                        new MCWorld(worldIn),
                        new MCBlockPos(pos),
                        new MCBlockState(state),
                        new CTPlayer(playerIn),
                        Hand.of(hand),
                        Facing.of(facing),
                        new MCPosition3f(hitX, hitY, hitZ)
                );
    }

    @Override
    public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
        if (Objects.nonNull(expandBlockRepresentation.onEntityWalk)) {
            expandBlockRepresentation.onEntityWalk.call(new MCWorld(worldIn), new MCBlockPos(pos), EntityHelper.getIEntity(entityIn));
        }
    }

    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        if (Objects.nonNull(expandBlockRepresentation.onEntityCollidedWithBlock)) {
            expandBlockRepresentation.onEntityCollidedWithBlock.call(new MCWorld(worldIn), new MCBlockPos(pos), new MCBlockState(state), EntityHelper.getIEntity(entityIn));
        }
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return expandBlockRepresentation.tileEntity != null;
    }

    @Override
    @Nullable
    public TileEntity createTileEntity(World world, IBlockState state) {
        return this.hasTileEntity(state) ? new TileEntityContent(expandBlockRepresentation.tileEntity.getId()) : null;
    }

    @ExpandContentTweakerEntry.RepresentationGetter
    public ExpandBlockRepresentation getExpandBlockRepresentation() {
        return expandBlockRepresentation;
    }
}
