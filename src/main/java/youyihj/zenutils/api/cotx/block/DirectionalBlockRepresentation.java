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
import stanhebben.zenscript.annotations.ZenClass;

/**
 * @author youyihj
 */
@ZenClass("mods.zenutils.cotx.DirectionalBlock")
public class DirectionalBlockRepresentation extends ExpandBlockRepresentation {
    private final Directions directions;

    public DirectionalBlockRepresentation(String unlocalizedName, IBlockMaterialDefinition blockMaterial, Directions directions) {
        super(unlocalizedName, blockMaterial);
        this.directions = directions;
    }

    public Directions getDirections() {
        return directions;
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

        public int toMeta(IBlockState state) {
            return metaFacingMapping.get(state.getValue(blockProperty));
        }

        public IBlockState toState(int meta, IBlockState defaultState) {
            return defaultState.withProperty(blockProperty, metaFacingMapping.inverse().get(meta));
        }

        private void fillMetaMapping() {
            for (int i = 0; i < validFacings.length; i++) {
                metaFacingMapping.put(validFacings[i], i);
            }
        }
    }
}
