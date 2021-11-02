package youyihj.zenutils.api.cotx.block;

import com.teamacronymcoders.base.registrysystem.BlockRegistry;
import com.teamacronymcoders.contenttweaker.ContentTweaker;
import com.teamacronymcoders.contenttweaker.api.ctobjects.blockmaterial.IBlockMaterialDefinition;
import com.teamacronymcoders.contenttweaker.modules.vanilla.blocks.BlockRepresentation;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenProperty;
import youyihj.zenutils.api.cotx.function.IBlockActivated;
import youyihj.zenutils.api.cotx.function.IEntityCollided;
import youyihj.zenutils.api.cotx.function.IEntityWalk;
import youyihj.zenutils.api.cotx.function.IPlacementChecker;
import youyihj.zenutils.api.cotx.tile.TileEntityRepresentation;

/**
 * @author youyihj
 */
@ZenRegister
@ModOnly("contenttweaker")
@ZenClass("mods.zenutils.cotx.Block")
public class ExpandBlockRepresentation extends BlockRepresentation {
    public ExpandBlockRepresentation(String unlocalizedName, IBlockMaterialDefinition blockMaterial) {
        setUnlocalizedName(unlocalizedName);
        setBlockMaterial(blockMaterial);
    }

    @ZenProperty
    public TileEntityRepresentation tileEntity;

    @ZenProperty
    public IBlockActivated onBlockActivated = null;

    @ZenProperty
    public IEntityWalk onEntityWalk = null;

    @ZenProperty
    public IEntityCollided onEntityCollidedWithBlock = null;

    @ZenProperty
    public IPlacementChecker placementChecker = null;

    @Override
    public void register() {
        ContentTweaker.instance.getRegistry(BlockRegistry.class, "BLOCK").register(new ExpandBlockContent(this));
    }
}
