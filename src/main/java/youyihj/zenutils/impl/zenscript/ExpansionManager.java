package youyihj.zenutils.impl.zenscript;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import stanhebben.zenscript.type.ZenType;

/**
 * @author youyihj
 */
public class ExpansionManager {
    private static final Table<ZenType, String, ParsedExpansion> expansions = HashBasedTable.create();

    public static void register(ParsedExpansion expansion) {
        expansions.put(expansion.getType(), expansion.getName(), expansion);
    }

    public static ParsedExpansion getExpansion(ZenType type, String name) {
        return expansions.get(type, name);
    }
}
