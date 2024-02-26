package youyihj.zenutils.api.zenscript;

import net.minecraftforge.fml.relauncher.Side;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Automatically register a class to zenscript on specific sides with specific mod dependencies.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SidedZenRegister {
    Side[] value() default {Side.CLIENT, Side.SERVER};

    /**
     * Only registers the class when all mod dependencies are installed
     * The difference of {@link crafttweaker.annotations.ModOnly} is it can define multiple dependencies
     * and doesn't try to load the class when dependencies are not satisfied
     */
    String[] modDeps() default {};
}
