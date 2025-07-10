package youyihj.zenutils.impl.mixin.crafttweaker;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeArray;
import stanhebben.zenscript.type.ZenTypeArrayBasic;
import stanhebben.zenscript.type.natives.IJavaMethod;
import stanhebben.zenscript.type.natives.JavaMethod;
import stanhebben.zenscript.util.ZenPosition;
import stanhebben.zenscript.util.ZenTypeUtil;
import youyihj.zenutils.impl.util.InternalUtils;
import youyihj.zenutils.impl.zenscript.ExpressionArraySub;
import youyihj.zenutils.impl.zenscript.PartialArrayOperation;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @author youyihj
 */
@Mixin(value = ZenTypeArrayBasic.class, remap = false)
public abstract class MixinZenTypeArrayBasic extends ZenTypeArray {
    @Unique
    private static final Map<Class<?>, ListMultimap<String, IJavaMethod>> ARRAY_OPERATIONS = InternalUtils.make(new HashMap<>(), map -> {
        for (Method method : ArrayUtils.class.getMethods()) {
            if (method.getName().startsWith("to") || !Modifier.isStatic(method.getModifiers())) {
                continue;
            }
            map.computeIfAbsent(method.getParameterTypes()[0], key -> ArrayListMultimap.create())
                    .put(method.getName(), new JavaMethod(method, ZenTypeUtil.EMPTY_REGISTRY));
        }
        map.get(int[].class).put("sort", JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, Arrays.class, "sort", int[].class));
        map.get(long[].class).put("sort", JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, Arrays.class, "sort", long[].class));
        map.get(double[].class).put("sort", JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, Arrays.class, "sort", double[].class));
        map.get(float[].class).put("sort", JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, Arrays.class, "sort", float[].class));
        map.get(byte[].class).put("sort", JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, Arrays.class, "sort", byte[].class));
        map.get(short[].class).put("sort", JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, Arrays.class, "sort", short[].class));
        map.get(Object[].class).put("sort", JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, Arrays.class, "sort", Object[].class));
        map.get(Object[].class).put("sort", JavaMethod.get(ZenTypeUtil.EMPTY_REGISTRY, Arrays.class, "sort", Object[].class, Comparator.class));
    });

    public MixinZenTypeArrayBasic(ZenType base) {
        super(base);
    }

    @Override
    public IPartialExpression getMember(ZenPosition position, IEnvironmentGlobal environment, IPartialExpression value, String name) {
        List<IJavaMethod> methods = ARRAY_OPERATIONS.get(getBaseType().isPointer() ? Object[].class : toJavaClass()).get(name);
        if (!methods.isEmpty()) {
            return new PartialArrayOperation(position, methods, this, value);
        }
        return super.getMember(position, environment, value, name);
    }

    @Inject(method = "indexGet", at = @At("HEAD"), cancellable = true)
    private void addSubArrayMember(ZenPosition position, IEnvironmentGlobal environment, Expression array, Expression index, CallbackInfoReturnable<Expression> cir) {
        if (index.getType() == ZenType.INTRANGE) {
            cir.setReturnValue(new ExpressionArraySub(position, array, index));
        }
    }
}
