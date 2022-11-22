package youyihj.zenutils.api.player;

import com.google.common.base.Suppliers;
import crafttweaker.annotations.ZenRegister;
import net.minecraft.stats.IStatType;
import net.minecraft.stats.StatBase;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenMethod;
import youyihj.zenutils.ZenUtils;
import youyihj.zenutils.api.util.ReflectionInvoked;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.function.Supplier;

/**
 * @author youyihj
 */
@ZenRegister
@ZenExpansion("mods.zenutils.IStatFormatter")
public class DefaultStatFormatters {
    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getIntegerInstance(Locale.US);
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("########0.00");

    private static final Supplier<IStatFormatter> SIMPLE = Suppliers.memoize(() -> DefaultStatFormatters::numberFormat);
    private static final Supplier<IStatFormatter> TIME = fromType(StatBase.timeStatType);
    private static final Supplier<IStatFormatter> DISTANCE = fromType(StatBase.distanceStatType);
    private static final Supplier<IStatFormatter> DIVIDE_BY_TEN = fromType(StatBase.divideByTen);

    private static Supplier<IStatFormatter> fromType(IStatType type) {
        return Suppliers.memoize(() -> ZenUtils.statFormatterAdapter.adapt(type));
    }

    @ZenMethod
    public static String numberFormat(int amount) {
        return NUMBER_FORMAT.format(amount);
    }

    @ZenMethod
    public static String decimalFormat(double amount) {
        return DECIMAL_FORMAT.format(amount);
    }

    @ZenMethod
    public static IStatFormatter simple() {
        return SIMPLE.get();
    }

    @ZenMethod
    public static IStatFormatter time() {
        return TIME.get();
    }

    @ZenMethod
    public static IStatFormatter distance() {
        return DISTANCE.get();
    }

    @ZenMethod
    public static IStatFormatter divideByTen() {
        return DIVIDE_BY_TEN.get();
    }

    @ReflectionInvoked
    public static IStatFormatter simple(String unused) {
        return simple();
    }
}
