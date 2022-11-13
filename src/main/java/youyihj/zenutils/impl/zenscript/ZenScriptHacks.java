package youyihj.zenutils.impl.zenscript;

import stanhebben.zenscript.definitions.ParsedFunction;
import stanhebben.zenscript.definitions.ParsedFunctionArgument;
import youyihj.zenutils.impl.util.ReflectUtils;

import java.lang.reflect.Field;

/**
 * @author youyihj
 */
public class ZenScriptHacks {
    private static final Field FUNCTION_SIGNATURE;

    static {
        try {
            FUNCTION_SIGNATURE = ReflectUtils.removePrivateFinal(ParsedFunction.class, "signature");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException();
        }
    }

    public static void setFirstArgument(ParsedFunction function, ParsedFunctionArgument toAdd) {
        function.getArguments().add(0, toAdd);
        StringBuilder sig = new StringBuilder();
        sig.append("(");
        for(ParsedFunctionArgument argument : function.getArguments()) {
            sig.append(argument.getType().getSignature());
        }
        sig.append(")");
        sig.append(function.getReturnType().getSignature());
        try {
            FUNCTION_SIGNATURE.set(function, sig.toString());
        } catch (IllegalAccessException e) {
            throw new RuntimeException();
        }
    }
}
