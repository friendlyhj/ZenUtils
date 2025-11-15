package youyihj.zenutils.impl.mixin.crafttweaker;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import stanhebben.zenscript.type.ZenTypeZenClass;

/**
 * @author youyihj
 */
@Mixin(value = ZenTypeZenClass.class, remap = false)
public abstract class MixinZenTypeZenClass {

    /**
     * @author youyihj
     * @reason wrong impl
     */
    @Overwrite
    public boolean isPointer() {
        return true;
    }

}
