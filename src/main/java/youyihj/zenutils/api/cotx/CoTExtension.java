package youyihj.zenutils.api.cotx;

import crafttweaker.CraftTweakerAPI;
import youyihj.zenutils.api.cotx.block.CrTCoTBlockStateBridge;
import youyihj.zenutils.api.cotx.block.DirectionalBlockRepresentation;
import youyihj.zenutils.api.cotx.block.ExpandBlockRepresentation;
import youyihj.zenutils.api.cotx.brackets.BracketHandlerCoTBlock;
import youyihj.zenutils.api.cotx.brackets.BracketHandlerCoTItem;
import youyihj.zenutils.api.cotx.function.*;
import youyihj.zenutils.api.cotx.item.EnergyItemRepresentation;
import youyihj.zenutils.api.cotx.item.ExpandItemRepresentation;
import youyihj.zenutils.api.cotx.tile.ExpandWorldForTile;
import youyihj.zenutils.api.cotx.tile.TileData;
import youyihj.zenutils.api.cotx.tile.TileEntityContent;
import youyihj.zenutils.api.cotx.tile.TileEntityRepresentation;

import java.util.Arrays;
import java.util.List;

/**
 * @author youyihj
 */
public class CoTExtension {
    public static void registerClasses() {
        List<Class<?>> classes = Arrays.asList(
                ExpandItemRepresentation.class,
                ExpandBlockRepresentation.class,
                EnergyItemRepresentation.class,
                DirectionalBlockRepresentation.class,
                CrTCoTBlockStateBridge.class,
                ExpandWorldForTile.class,
                TileData.class,
                TileEntityRepresentation.class,
                TileEntityContent.class,
                ExpandHand.class,
                ExpandVanillaFactory.class,
                BracketHandlerCoTBlock.class,
                BracketHandlerCoTItem.class,
                IBlockActivated.class,
                IEntityCollided.class,
                IEntityItemUpdate.class,
                IEntityWalk.class,
                IGetEntityLifeSpan.class,
                IPlacementChecker.class,
                ITileEntityTick.class
        );
        classes.forEach(CraftTweakerAPI::registerClass);
    }
}
