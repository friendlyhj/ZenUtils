package youyihj.zenutils.api.item;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import stanhebben.zenscript.annotations.IterableSimple;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import youyihj.zenutils.impl.util.TotallyImmutableItemStack;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * @author youyihj
 */
@ZenClass("mods.zenutils.ItemHandler")
@IterableSimple("crafttweaker.item.IItemStack")
@ZenRegister
public class CrTItemHandler implements Iterable<IItemStack> {
    private final IItemHandler itemHandler;

    private CrTItemHandler(IItemHandler itemHandler) {
        Objects.requireNonNull(itemHandler);
        this.itemHandler = itemHandler;
    }

    public static CrTItemHandler of(IItemHandler itemHandler) {
        return itemHandler == null ? null : new CrTItemHandler(itemHandler);
    }

    @ZenGetter
    public int size() {
        return itemHandler.getSlots();
    }

    @Override
    @Nonnull
    public Iterator<IItemStack> iterator() {
        return IntStream.range(0, size()).mapToObj(this::getStackInSlot).iterator();
    }

    @ZenMethod
    public IItemStack getStackInSlot(int slot) {
        ItemStack stack = itemHandler.getStackInSlot(slot);
        return stack.isEmpty() ? null : new TotallyImmutableItemStack(stack);
    }

    @ZenMethod
    public IItemStack insertItem(int slot, IItemStack stack, boolean simulate) {
        return CraftTweakerMC.getIItemStack(itemHandler.insertItem(slot, CraftTweakerMC.getItemStack(stack), simulate));
    }

    @ZenMethod
    public IItemStack extractItem(int slot, int amount, boolean simulate) {
        return CraftTweakerMC.getIItemStack(itemHandler.extractItem(slot, amount, simulate));
    }

    @ZenMethod
    public int getSlotLimit(int slot) {
        return itemHandler.getSlotLimit(slot);
    }

    @ZenMethod
    public boolean isItemValid(int slot, IItemStack stack) {
        return itemHandler.isItemValid(slot, CraftTweakerMC.getItemStack(stack));
    }
}
