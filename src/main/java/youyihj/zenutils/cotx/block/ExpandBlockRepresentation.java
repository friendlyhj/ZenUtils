package youyihj.zenutils.cotx.block;

import com.teamacronymcoders.base.registrysystem.BlockRegistry;
import com.teamacronymcoders.contenttweaker.ContentTweaker;
import com.teamacronymcoders.contenttweaker.api.ctobjects.blockmaterial.IBlockMaterialDefinition;
import com.teamacronymcoders.contenttweaker.modules.vanilla.blocks.BlockRepresentation;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenProperty;
import youyihj.zenutils.cotx.function.IBlockActivated;
import youyihj.zenutils.cotx.function.IEntityCollided;
import youyihj.zenutils.cotx.function.IEntityWalk;

@ZenRegister
@ModOnly("contenttweaker")
@ZenClass("mods.zenutils.cotx.Block")
public class ExpandBlockRepresentation extends BlockRepresentation {
    public ExpandBlockRepresentation(String unlocalizedName, IBlockMaterialDefinition blockMaterial) {
        setUnlocalizedName(unlocalizedName);
        setBlockMaterial(blockMaterial);
    }

    @ZenProperty
    public IBlockActivated onBlockActivated = null;

    @ZenProperty
    public IEntityWalk onEntityWalk = null;

    @ZenProperty
    public IEntityCollided onEntityCollidedWithBlock = null;

    @Override
    public void register() {
        ContentTweaker.instance.getRegistry(BlockRegistry.class, "BLOCK").register(new ExpandBlockContent(this));
    }
}
