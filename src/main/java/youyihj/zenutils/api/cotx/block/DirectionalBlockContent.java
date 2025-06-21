package youyihj.zenutils.api.cotx.block;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.teamacronymcoders.base.client.models.generator.generatedmodel.GeneratedModel;
import com.teamacronymcoders.base.client.models.generator.generatedmodel.IGeneratedModel;
import com.teamacronymcoders.base.client.models.generator.generatedmodel.ModelType;
import com.teamacronymcoders.base.util.files.templates.TemplateFile;
import com.teamacronymcoders.base.util.files.templates.TemplateManager;
import com.teamacronymcoders.contenttweaker.api.ctobjects.blockpos.MCBlockPos;
import com.teamacronymcoders.contenttweaker.api.ctobjects.enums.Facing;
import com.teamacronymcoders.contenttweaker.api.ctobjects.enums.Hand;
import com.teamacronymcoders.contenttweaker.api.ctobjects.resourcelocation.CTResourceLocation;
import com.teamacronymcoders.contenttweaker.api.ctobjects.world.MCWorld;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import youyihj.zenutils.Reference;
import youyihj.zenutils.api.cotx.annotation.ExpandContentTweakerEntry;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * @author youyihj
 */
@ExpandContentTweakerEntry
public abstract class DirectionalBlockContent extends ExpandBlockContent {
    public static final IProperty<DirectionalBlockRepresentation.PlaneRotation> PLANE_ROTATION_PROPERTY = PropertyEnum.create("plane_rot", DirectionalBlockRepresentation.PlaneRotation.class);

    protected DirectionalBlockContent(DirectionalBlockRepresentation blockRepresentation) {
        super(blockRepresentation);
    }

    public static DirectionalBlockContent create(DirectionalBlockRepresentation blockRepresentation) {
        if (blockRepresentation.isPlaneRotatable() && blockRepresentation.getDirections() == DirectionalBlockRepresentation.Directions.ALL) {
            return new OmniDirectionalBlockContent(blockRepresentation) {
                @Override
                public DirectionalBlockRepresentation getExpandBlockRepresentation() {
                    return blockRepresentation;
                }
            };
        } else {
            return new DirectionalBlockContent(blockRepresentation) {
                @Override
                protected BlockStateContainer createBlockState() {
                    if (!blockRepresentation.isPlaneRotatable()) {
                        return new BlockStateContainer(this, getDirections().getBlockProperty());
                    } else {
                        return new BlockStateContainer(this, getDirections().getBlockProperty(), PLANE_ROTATION_PROPERTY);
                    }
                }

                @Override
                public DirectionalBlockRepresentation getExpandBlockRepresentation() {
                    return blockRepresentation;
                }
            };
        }
    }

    public DirectionalBlockRepresentation.Directions getDirections() {
        return getExpandBlockRepresentation().getDirections();
    }

    @Override
    protected abstract BlockStateContainer createBlockState();

    @SuppressWarnings("deprecation")
    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
        if (getDirections() != DirectionalBlockRepresentation.Directions.VERTICAL) {
            return state.withProperty(getDirections().getBlockProperty(), mirrorIn.mirror(state.getValue(getDirections().getBlockProperty())));
        } else {
            return super.withMirror(state, mirrorIn);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        if (getDirections() != DirectionalBlockRepresentation.Directions.VERTICAL) {
            return state.withProperty(getDirections().getBlockProperty(), rot.rotate(state.getValue(getDirections().getBlockProperty())));
        } else {
            return super.withRotation(state, rot);
        }
    }

    @Override
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
        if (!getExpandBlockRepresentation().isPlaneRotatable()) {
            return super.rotateBlock(world, pos, axis);
        } else {
            IBlockState state = world.getBlockState(pos);
            if (axis == state.getValue(getDirections().getBlockProperty())) {
                world.setBlockState(pos, state.withProperty(PLANE_ROTATION_PROPERTY, state.getValue(PLANE_ROTATION_PROPERTY).rotateClockWise90()));
                return true;
            } else {
                return super.rotateBlock(world, pos, axis);
            }
        }
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return getDirections().toMeta(state, getExpandBlockRepresentation().isPlaneRotatable());
    }

    @SuppressWarnings("deprecation")
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDirections().toState(meta, getDefaultState(), getExpandBlockRepresentation().isPlaneRotatable());
    }

    @Nullable
    @Override
    public EnumFacing[] getValidRotations(World world, BlockPos pos) {
        return getDirections().getValidFacings();
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        IProperty<EnumFacing> property = getDirections().getBlockProperty();
        EnumFacing blockFacing = getExpandBlockRepresentation().placementFacingFunction
                .apply(new MCWorld(world), new MCBlockPos(pos), Facing.of(facing), hitX, hitY, hitZ, meta, CraftTweakerMC.getIEntityLivingBase(placer), Hand.of(hand))
                .getInternal();
        if (getExpandBlockRepresentation().isPlacingOpposite()) {
            blockFacing = blockFacing.getOpposite();
        }
        if (property.getAllowedValues().contains(blockFacing)) {
            return getDefaultState().withProperty(property, blockFacing);
        } else {
            return getDefaultState();
        }
    }

    @Override
    public List<IGeneratedModel> getGeneratedModels() {
        List<IGeneratedModel> models = Lists.newArrayList();
        String templateFileName = (getExpandBlockRepresentation().isPlaneRotatable() ? "plane_rotatable_" : "") + getDirections().name().toLowerCase(Locale.ENGLISH) + "_directional_block";
        this.getResourceLocations(Lists.newArrayList()).forEach(resourceLocation ->  {
            TemplateFile templateFile = TemplateManager.getTemplateFile(new ResourceLocation(Reference.MODID, templateFileName));
            Map<String, String> replacements = Maps.newHashMap();
            replacements.put("texture", Optional.ofNullable(getExpandBlockRepresentation().getTextureLocation())
                    .map(CTResourceLocation::getInternal)
                    .map(ResourceLocation::toString)
                    .orElseGet(() -> new ResourceLocation(resourceLocation.getNamespace(),
                            "blocks/" + resourceLocation.getPath()).toString()));

            templateFile.replaceContents(replacements);

            models.add(new GeneratedModel(resourceLocation.getPath(), ModelType.BLOCKSTATE,
                    templateFile.getFileContents()));
        });
        return models;
    }

    @Override
    public abstract DirectionalBlockRepresentation getExpandBlockRepresentation();
}
