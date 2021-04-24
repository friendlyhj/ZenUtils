package youyihj.zenutils.util.object;

import crafttweaker.api.item.IMutableItemStack;
import crafttweaker.mc1120.item.MCItemStack;
import net.minecraft.item.ItemStack;

public class TotallyImmutableItemStack extends MCItemStack {
    public TotallyImmutableItemStack(ItemStack itemStack) {
        super(itemStack);
    }

    @Override
    public IMutableItemStack mutable() {
        throw new UnsupportedOperationException("The ItemStack is totally immutable. You can't make it mutable");
    }
}
