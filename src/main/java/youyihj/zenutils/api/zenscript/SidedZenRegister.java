package youyihj.zenutils.api.zenscript;

import net.minecraftforge.fml.relauncher.Side;

/**
 * Automatically register a class to zenscript on specific sides with specific mod dependencies.
 */
public @interface SidedZenRegister {
    Side[] value() default {Side.CLIENT, Side.SERVER};

    /**
     * Only registers the class when all mod dependencies are installed
     * The difference of {@link crafttweaker.annotations.ModOnly} is it can define multiple dependencies
     * and doesn't try to load the class when dependencies are not satisfied
     */
    String[] modDeps() default {};
}
