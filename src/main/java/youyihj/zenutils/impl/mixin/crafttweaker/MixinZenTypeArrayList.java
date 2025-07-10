package youyihj.zenutils.impl.mixin.crafttweaker;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeArray;
import stanhebben.zenscript.type.ZenTypeArrayList;
import stanhebben.zenscript.util.ZenPosition;
import youyihj.zenutils.impl.zenscript.ExpressionListSub;
import youyihj.zenutils.impl.zenscript.ListExpansionMethods;
import youyihj.zenutils.impl.zenscript.PartialListOperation;

/**
 * @author youyihj
 */
@Mixin(value = ZenTypeArrayList.class, remap = false)
public abstract class MixinZenTypeArrayList extends ZenTypeArray {
    public MixinZenTypeArrayList(ZenType base) {
        super(base);
    }

    @Inject(method = "getMember", at = @At("HEAD"), cancellable = true)
    private void addMembers(ZenPosition position, IEnvironmentGlobal environment, IPartialExpression value, String name, CallbackInfoReturnable<IPartialExpression> cir) {
        switch (name) {
            case "add":
                cir.setReturnValue(new PartialListOperation(position, ListExpansionMethods.ADD, value, this, true, false));
                return;
            case "addAll":
                cir.setReturnValue(new PartialListOperation(position, ListExpansionMethods.ADD_ALL, value, this, true, true));
                return;
            case "clone":
                cir.setReturnValue(new PartialListOperation(position, ListExpansionMethods.CLONE, value, this, false, true));
                return;
            case "clear":
                cir.setReturnValue(new PartialListOperation(position, ListExpansionMethods.CLEAR, value, this, true, false));
                return;
            case "indexOf":
                cir.setReturnValue(new PartialListOperation(position, ListExpansionMethods.INDEX_OF, value, this, false, false));
                return;
            case "isEmpty":
                cir.setReturnValue(new PartialListOperation(position, ListExpansionMethods.IS_EMPTY, value, this, false, false));
                return;
            case "isNotEmpty":
                cir.setReturnValue(new PartialListOperation(position, ListExpansionMethods.IS_NOT_EMPTY, value, this, false, true));
                return;
            case "isSorted":
                cir.setReturnValue(new PartialListOperation(position, ListExpansionMethods.IS_SORTED, value, this, false, true));
                return;
            case "lastIndexOf":
                cir.setReturnValue(new PartialListOperation(position, ListExpansionMethods.LAST_INDEX_OF, value, this, false, false));
                return;
            case "removeAll":
                cir.setReturnValue(new PartialListOperation(position, ListExpansionMethods.REMOVE_ALL, value, this, true, true));
                return;
            case "removeAllOccurrences":
                cir.setReturnValue(new PartialListOperation(position, ListExpansionMethods.REMOVE_ALL_OCCURRENCES, value, this, true, true));
                return;
            case "removeByIndex":
                cir.setReturnValue(new PartialListOperation(position, ListExpansionMethods.REMOVE_BY_INDEX, value, this, true, true));
                return;
            case "reverse":
                cir.setReturnValue(new PartialListOperation(position, ListExpansionMethods.REVERSE, value, this, true, true));
                return;
            case "sort":
                cir.setReturnValue(new PartialListOperation(position, ListExpansionMethods.SORT, value, this, true, true));
                return;
            case "shift":
                cir.setReturnValue(new PartialListOperation(position, ListExpansionMethods.SHIFT, value, this, true, true));
                return;
            case "swap":
                cir.setReturnValue(new PartialListOperation(position, ListExpansionMethods.SWAP, value, this, true, true));
        }
    }

    @Inject(method = "indexGet", at = @At("HEAD"), cancellable = true)
    private void addSubListMember(ZenPosition position, IEnvironmentGlobal environment, Expression array, Expression index, CallbackInfoReturnable<Expression> cir) {
        if (index.getType() == ZenType.INTRANGE) {
            cir.setReturnValue(new ExpressionListSub(position, array, index));
        }
    }
}
