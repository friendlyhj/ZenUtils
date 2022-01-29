package youyihj.zenutils.api.cotx.annotation;

import com.teamacronymcoders.contenttweaker.modules.vanilla.blocks.BlockContent;
import com.teamacronymcoders.contenttweaker.modules.vanilla.blocks.BlockRepresentation;
import com.teamacronymcoders.contenttweaker.modules.vanilla.items.ItemContent;
import com.teamacronymcoders.contenttweaker.modules.vanilla.items.ItemRepresentation;

import java.lang.annotation.*;

/**
 * Mark a class as an expand ContentTweaker entry. The class should extend {@link BlockContent} or {@link ItemContent}.
 *
 * @author youyihj
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface ExpandContentTweakerEntry {

    /**
     * Mark a method as a representation getter. Used for late get ContentTweaker entries.
     *
     * The method should have zero parameters and return a class which extends {@link BlockRepresentation} or {@link ItemRepresentation}.
     * Moreover, the return type is exposed to zenscript.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface RepresentationGetter {

    }
}
