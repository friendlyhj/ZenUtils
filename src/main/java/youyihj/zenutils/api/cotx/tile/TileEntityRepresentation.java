package youyihj.zenutils.api.cotx.tile;

import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenProperty;
import youyihj.zenutils.Reference;
import youyihj.zenutils.api.cotx.function.ITileEntityTick;
import youyihj.zenutils.api.zenscript.SidedZenRegister;

/**
 * @author youyihj
 */
@SidedZenRegister(modDeps = Reference.MOD_COT)
@ZenClass("mods.zenutils.cotx.TileEntity")
public class TileEntityRepresentation {
    private final int id;

    public TileEntityRepresentation(int id) {
        this.id = id;
    }

    @ZenProperty
    public ITileEntityTick onTick = ((tileEntity, world, pos) -> {});

    @ZenGetter("id")
    public int getId() {
        return id;
    }

    @ZenMethod
    public void register() {
        TileEntityManager.registerTileEntity(this);
    }
}
