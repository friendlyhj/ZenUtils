package youyihj.zenutils.api.cotx.block;

import com.google.common.collect.BiMap;
import com.google.common.collect.EnumHashBiMap;
import com.teamacronymcoders.base.registrysystem.BlockRegistry;
import com.teamacronymcoders.contenttweaker.ContentTweaker;
import com.teamacronymcoders.contenttweaker.api.ctobjects.blockmaterial.IBlockMaterialDefinition;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenProperty;
import youyihj.zenutils.Reference;
import youyihj.zenutils.api.cotx.function.IPlacementFacingFunction;
import youyihj.zenutils.api.zenscript.SidedZenRegister;

import java.util.Locale;

/**
 * @author youyihj
 */
@SidedZenRegister(modDeps = Reference.MOD_COT)
@ZenClass("mods.zenutils.cotx.DirectionalBlock")
public class DirectionalBlockRepresentation extends ExpandBlockRepresentation {
    private final Directions directions;
    private final boolean planeRotatable;
    private final boolean placingOpposite;

    @ZenProperty
    public IPlacementFacingFunction placementFacingFunction;

    public DirectionalBlockRepresentation(String unlocalizedName, IBlockMaterialDefinition blockMaterial, Directions directions, boolean planeRotatable, boolean placingOpposite) {
        super(unlocalizedName, blockMaterial);
        this.directions = directions;
        this.planeRotatable = planeRotatable;
        this.placingOpposite = placingOpposite;
        this.placementFacingFunction = IPlacementFacingFunction.placer(this);
    }

    public Directions getDirections() {
        return directions;
    }

    public boolean isPlaneRotatable() {
        return planeRotatable;
    }

    public boolean isPlacingOpposite() {
        return placingOpposite;
    }

    @Override
    public void register() {
        ContentTweaker.instance.getRegistry(BlockRegistry.class, "BLOCK").register(DirectionalBlockContent.create(this));
    }

    public enum Directions {
        ALL(EnumFacing.values()),
        HORIZONTAL(EnumFacing.Plane.HORIZONTAL.facings()),
        VERTICAL(EnumFacing.Plane.VERTICAL.facings());

        private final BiMap<EnumFacing, Integer> metaFacingMapping = EnumHashBiMap.create(EnumFacing.class);
        private final IProperty<EnumFacing> blockProperty;
        private final EnumFacing[] validFacings;

        Directions(EnumFacing[] validFacings) {
            this.validFacings = validFacings;
            this.blockProperty = PropertyEnum.create("facing", EnumFacing.class, validFacings);
            fillMetaMapping();
        }

        public EnumFacing[] getValidFacings() {
            return validFacings;
        }

        public IProperty<EnumFacing> getBlockProperty() {
            return blockProperty;
        }

        public int toMeta(IBlockState state, boolean planeRotatable) {
            int facingMeta = metaFacingMapping.get(state.getValue(blockProperty));
            if (planeRotatable) {
                return facingMeta * 4 + state.getValue(DirectionalBlockContent.PLANE_ROTATION_PROPERTY).ordinal();
            } else {
                return facingMeta;
            }
        }

        public IBlockState toState(int meta, IBlockState defaultState, boolean planeRotatable) {
            if (planeRotatable) {
                return defaultState.withProperty(blockProperty, metaFacingMapping.inverse().get(meta / 4))
                        .withProperty(DirectionalBlockContent.PLANE_ROTATION_PROPERTY, PlaneRotation.indexOf(meta % 4));
            } else {
                return defaultState.withProperty(blockProperty, metaFacingMapping.inverse().get(meta));
            }
        }

        private void fillMetaMapping() {
            for (int i = 0; i < validFacings.length; i++) {
                metaFacingMapping.put(validFacings[i], i);
            }
        }
    }

    public enum PlaneRotation implements IStringSerializable {
        DOWN,
        UP,
        LEFT,
        RIGHT;

        private static final PlaneRotation[] VALUES = PlaneRotation.values();

        @Override
        public String getName() {
            return name().toLowerCase(Locale.ENGLISH);
        }

        public PlaneRotation rotateClockWise90() {
            switch (this) {
                case DOWN:
                    return LEFT;
                case UP:
                    return RIGHT;
                case LEFT:
                    return UP;
                case RIGHT:
                    return DOWN;
                default:
                    return this;
            }
        }

        public static PlaneRotation indexOf(int index) {
            return VALUES[index];
        }
    }
}
