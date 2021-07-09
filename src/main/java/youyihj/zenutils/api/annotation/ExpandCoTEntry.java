package youyihj.zenutils.api.annotation;

import com.teamacronymcoders.contenttweaker.modules.vanilla.blocks.BlockContent;
import com.teamacronymcoders.contenttweaker.modules.vanilla.blocks.BlockRepresentation;
import com.teamacronymcoders.contenttweaker.modules.vanilla.items.ItemContent;
import com.teamacronymcoders.contenttweaker.modules.vanilla.items.ItemRepresentation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark a class as an expand cot entry. The class should extend {@link BlockContent} or {@link ItemContent}.
 *
 * @author youyihj
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ExpandCoTEntry {

    /**
     * Mark a method as a representation getter. Used for late get Contenttweaker entries.
     *
     * The method should have zero parameters and return a class which extends {@link BlockRepresentation} or {@link ItemRepresentation}.
     * Moreover, the return type is exposed to zenscript.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface RepresentationGetter {

    }
}
