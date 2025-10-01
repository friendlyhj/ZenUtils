package youyihj.zenutils.api.cotx.block;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.teamacronymcoders.base.client.models.generator.generatedmodel.GeneratedModel;
import com.teamacronymcoders.base.client.models.generator.generatedmodel.IGeneratedModel;
import com.teamacronymcoders.base.client.models.generator.generatedmodel.ModelType;
import com.teamacronymcoders.base.util.files.templates.TemplateFile;
import com.teamacronymcoders.base.util.files.templates.TemplateManager;
import com.teamacronymcoders.contenttweaker.api.ctobjects.aabb.MCAxisAlignedBB;
import com.teamacronymcoders.contenttweaker.api.ctobjects.blockpos.MCBlockPos;
import com.teamacronymcoders.contenttweaker.api.ctobjects.blockstate.MCBlockState;
import com.teamacronymcoders.contenttweaker.api.ctobjects.enums.Facing;
import com.teamacronymcoders.contenttweaker.api.ctobjects.enums.Hand;
import com.teamacronymcoders.contenttweaker.api.ctobjects.resourcelocation.CTResourceLocation;
import com.teamacronymcoders.contenttweaker.api.ctobjects.world.MCWorld;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.mc1120.world.MCBlockAccess;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.model.TRSRTransformation;
import org.apache.commons.lang3.tuple.Pair;
import youyihj.zenutils.Reference;
import youyihj.zenutils.api.cotx.annotation.ExpandContentTweakerEntry;
import youyihj.zenutils.impl.util.SimpleCache;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
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

    private final SimpleCache<Pair<EnumFacing, DirectionalBlockRepresentation.PlaneRotation>, AxisAlignedBB> boxCache = new SimpleCache<>(this::getBox);

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

    @Nonnull
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        AxisAlignedBB box = boxCache.get(Pair.of(
                state.getValue(getDirections().getBlockProperty()),
                getExpandBlockRepresentation().isPlaneRotatable() ? state.getValue(PLANE_ROTATION_PROPERTY) : DirectionalBlockRepresentation.PlaneRotation.DOWN)
        );
        if (getExpandBlockRepresentation().boundingBoxFunction == null) {
            return box;
        } else {
            return getExpandBlockRepresentation().boundingBoxFunction.apply(
                    new MCAxisAlignedBB(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ),
                    new MCBlockState(state),
                    new MCBlockAccess(source),
                    new MCBlockPos(pos)
            ).getInternal();
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
        this.getResourceLocations(Lists.newArrayList()).forEach(resourceLocation -> {
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

    private AxisAlignedBB getBox(Pair<EnumFacing, DirectionalBlockRepresentation.PlaneRotation> key) {
        AxisAlignedBB aabb = getExpandBlockRepresentation().getAxisAlignedBB().getInternal();
        Matrix4f rot = getRotationMatrix(key.getLeft(), key.getRight());
        Point3f minPoint = new Point3f((float) aabb.minX, (float) aabb.minY, (float) aabb.minZ);
        Point3f maxPoint = new Point3f((float) aabb.maxX, (float) aabb.maxY, (float) aabb.maxZ);
        rot.transform(minPoint);
        rot.transform(maxPoint);
        return new AxisAlignedBB(minPoint.x, minPoint.y, minPoint.z, maxPoint.x, maxPoint.y, maxPoint.z);
    }

    private static Matrix4f getRotationMatrix(EnumFacing facing, DirectionalBlockRepresentation.PlaneRotation planeRot) {
        float x, y, z;
        switch (planeRot) {
            case DOWN:
                z = 0;
                break;
            case UP:
                z = 180;
                break;
            case LEFT:
                z = 90;
                break;
            case RIGHT:
                z = 270;
                break;
            default:
                throw new IllegalArgumentException("unknown plane rotation");
        }
        switch (facing) {
            case DOWN:
                x = 270;
                y = 0;
                break;
            case UP:
                x = 90;
                y = 0;
                break;
            case NORTH:
                x = 0;
                y = 0;
                break;
            case SOUTH:
                x = 0;
                y = 180;
                break;
            case WEST:
                x = 0;
                y = 90;
                break;
            case EAST:
                x = 0;
                y = 270;
                break;
            default:
                throw new IllegalArgumentException("unknown facing");
        }
        Quat4f quat4f = TRSRTransformation.quatFromXYZDegrees(new Vector3f(x, y, z));
        Matrix4f rotMat = new Matrix4f();
        rotMat.setIdentity();
        rotMat.setRotation(quat4f);
        Matrix4f translationMat = new Matrix4f();
        translationMat.setIdentity();
        translationMat.setTranslation(new Vector3f(-0.5f, -0.5f, -0.5f));
        Matrix4f postTranslationMat = new Matrix4f();
        postTranslationMat.setIdentity();
        postTranslationMat.setTranslation(new Vector3f(0.5f, 0.5f, 0.5f));
        Matrix4f result = new Matrix4f();
        result.mul(postTranslationMat, rotMat);
        result.mul(translationMat);
        return result;
    }
}
