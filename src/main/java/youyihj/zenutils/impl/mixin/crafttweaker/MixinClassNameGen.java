package youyihj.zenutils.impl.mixin.crafttweaker;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import stanhebben.zenscript.compiler.ClassNameGenerator;

/**
 * @author youyihj
 */
@Mixin(value = ClassNameGenerator.class, remap = false)
public abstract class MixinClassNameGen {
    @Shadow private String prefix;

    @Unique
    Multiset<String> zu$classNameUsed = HashMultiset.create();

    @Unique
    private String zu$generateInternal(String prefix, String name) {
        String result = prefix + name + zu$classNameUsed.count(name);
        zu$classNameUsed.add(name);
        return result;
    }

    /**
     * @author youyihj
     * @reason Avoid to use a global counter, which makes the names of class declared at the same zenscript position are different on different loader or side.
     */
    @Overwrite
    public String generate() {
        return zu$generateInternal(prefix, "");
    }

    /**
     * @author youyihj
     * @reason Avoid use a global counter, which makes the names of class declared at the same zenscript position are different on different loader or side.
     */
    @Overwrite
    public String generate(String customPrefix) {
        return zu$generateInternal(customPrefix, "");
    }

    /**
     * @author youyihj
     * @reason Avoid use a global counter, which makes the names of class declared at the same zenscript position are different on different loader or side.
     */
    @Overwrite
    public String generateWithMiddleName(String customMiddleName) {
        return zu$generateInternal(prefix, customMiddleName);
    }
}
