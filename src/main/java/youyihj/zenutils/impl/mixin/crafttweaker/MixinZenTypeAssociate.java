package youyihj.zenutils.impl.mixin.crafttweaker;

import org.spongepowered.asm.mixin.Mixin;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeAssociative;
import youyihj.zenutils.impl.zenscript.IOrderlyType;

/**
 * @author youyihj
 */
@Mixin(value = ZenTypeAssociative.class, remap = false)
public abstract class MixinZenTypeAssociate extends ZenType implements IOrderlyType {
    private boolean orderly;

    @Override
    public boolean isOrderly() {
        return orderly;
    }

    @Override
    public void setOrderly() {
        this.orderly = true;
    }
}
