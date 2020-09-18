package youyihj.zenutils.cotx.block;

import com.teamacronymcoders.base.registrysystem.BlockRegistry;
import com.teamacronymcoders.contenttweaker.ContentTweaker;
import com.teamacronymcoders.contenttweaker.modules.vanilla.blocks.BlockRepresentation;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenProperty;
import youyihj.zenutils.cotx.function.IBlockActivated;
import youyihj.zenutils.cotx.function.IEntityWalk;

@ZenRegister
@ModOnly("contenttweaker")
@ZenClass("mods.zenutils.cotx.Block")
public class ExtendBlockRepresentation extends BlockRepresentation {
    public ExtendBlockRepresentation(String unlocalizedName) {
        this.setUnlocalizedName(unlocalizedName);
    }

    @ZenProperty
    public IBlockActivated onBlockActivated = null;

    @ZenProperty
    public IEntityWalk onEntityWalk = null;

    @ZenProperty
    public boolean canSilkTouch = true;

    @Override
    public void register() {
        ContentTweaker.instance.getRegistry(BlockRegistry.class, "BLOCK").register(new ExtendBlockContent(this));
    }
}
