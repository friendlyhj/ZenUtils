package youyihj.zenutils.api.cotx.block;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.teamacronymcoders.base.client.models.generator.generatedmodel.GeneratedModel;
import com.teamacronymcoders.base.client.models.generator.generatedmodel.IGeneratedModel;
import com.teamacronymcoders.base.client.models.generator.generatedmodel.ModelType;
import com.teamacronymcoders.base.util.files.templates.TemplateFile;
import com.teamacronymcoders.base.util.files.templates.TemplateManager;
import com.teamacronymcoders.contenttweaker.api.ctobjects.resourcelocation.CTResourceLocation;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
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

    private final DirectionalBlockRepresentation representation;

    protected DirectionalBlockContent(DirectionalBlockRepresentation blockRepresentation) {
        super(blockRepresentation);
        this.representation = blockRepresentation;
    }

    public static DirectionalBlockContent create(DirectionalBlockRepresentation blockRepresentation) {
        return new DirectionalBlockContent(blockRepresentation) {
            @Override
            public DirectionalBlockRepresentation.Directions getDirections() {
                return blockRepresentation.getDirections();
            }
        };
    }

    public abstract DirectionalBlockRepresentation.Directions getDirections();

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, getDirections().getBlockProperty());
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return getDirections().toMeta(state);
    }

    @SuppressWarnings("deprecation")
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDirections().toState(meta, getDefaultState());
    }

    @Nullable
    @Override
    public EnumFacing[] getValidRotations(World world, BlockPos pos) {
        return getDirections().getValidFacings();
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        IProperty<EnumFacing> property = getDirections().getBlockProperty();
        switch (getDirections()) {
            case ALL:
                Vec3d lookVec = placer.getLookVec();
                return getDefaultState().withProperty(property, EnumFacing.getFacingFromVector((float) lookVec.x, (float) lookVec.y, (float) lookVec.z));
            case HORIZONTAL:
                return getDefaultState().withProperty(property, placer.getHorizontalFacing());
            case VERTICAL:
                lookVec = placer.getLookVec();
                return getDefaultState().withProperty(property, lookVec.y > 0.0 ? EnumFacing.UP : EnumFacing.DOWN);
            default:
                return getDefaultState();
        }
    }

    @Override
    public List<IGeneratedModel> getGeneratedModels() {
        List<IGeneratedModel> models = Lists.newArrayList();
        this.getResourceLocations(Lists.newArrayList()).forEach(resourceLocation ->  {
            TemplateFile templateFile = TemplateManager.getTemplateFile(new ResourceLocation(Reference.MODID, getDirections().name().toLowerCase(Locale.ENGLISH) + "_directional_block"));
            Map<String, String> replacements = Maps.newHashMap();

            replacements.put("texture", Optional.ofNullable(representation.getTextureLocation())
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
    public DirectionalBlockRepresentation getExpandBlockRepresentation() {
        return representation;
    }
}
