package youyihj.zenutils.cotx.block;

import com.teamacronymcoders.contenttweaker.api.ctobjects.enums.Facing;
import com.teamacronymcoders.contenttweaker.api.ctobjects.enums.Hand;
import com.teamacronymcoders.contenttweaker.modules.vanilla.blocks.BlockContent;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Objects;

public class ExpandBlockContent extends BlockContent {
    private ExpandBlockRepresentation expandBlockRepresentation;

    public ExpandBlockContent(ExpandBlockRepresentation blockRepresentation) {
        super(blockRepresentation);
        this.expandBlockRepresentation = blockRepresentation;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return Objects.nonNull(expandBlockRepresentation.onBlockActivated) &&
                expandBlockRepresentation.onBlockActivated.activate(
                        CraftTweakerMC.getIWorld(worldIn),
                        CraftTweakerMC.getIBlockPos(pos),
                        CraftTweakerMC.getBlockState(state),
                        CraftTweakerMC.getIPlayer(playerIn),
                        Hand.of(hand),
                        Facing.of(facing),
                        new float[]{hitX, hitY, hitZ}
                );
    }

    @Override
    public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
        if (Objects.nonNull(expandBlockRepresentation.onEntityWalk)) {
            expandBlockRepresentation.onEntityWalk.call(CraftTweakerMC.getIWorld(worldIn), CraftTweakerMC.getIBlockPos(pos), CraftTweakerMC.getIEntity(entityIn));
        }
    }

    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        if (Objects.nonNull(expandBlockRepresentation.onEntityCollidedWithBlock)) {
            expandBlockRepresentation.onEntityCollidedWithBlock.call(CraftTweakerMC.getIWorld(worldIn), CraftTweakerMC.getIBlockPos(pos), CraftTweakerMC.getBlockState(state),CraftTweakerMC.getIEntity(entityIn));
        }
    }
}
