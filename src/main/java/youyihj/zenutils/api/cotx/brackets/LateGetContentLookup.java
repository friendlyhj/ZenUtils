package youyihj.zenutils.api.cotx.brackets;

import com.teamacronymcoders.contenttweaker.modules.vanilla.blocks.BlockContent;
import com.teamacronymcoders.contenttweaker.modules.vanilla.items.ItemContent;

import java.util.HashSet;
import java.util.Set;

/**
 * @author youyihj
 */
public class LateGetContentLookup {
    private static final Set<ItemContent> items = new HashSet<>();
    private static final Set<BlockContent> blocks = new HashSet<>();

    public static void addItem(ItemContent item) {
        items.add(item);
    }

    public static void addBlock(BlockContent block) {
        blocks.add(block);
    }

    public static void refreshFields() {
        items.forEach(ItemContent::setFields);
        blocks.forEach(BlockContent::setFields);
    }

    public static void clear() {
        items.clear();
        blocks.clear();
    }
}
