package youyihj.zenutils.impl.zenscript;

import stanhebben.zenscript.ZenParsedFile;
import stanhebben.zenscript.ZenTokener;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.definitions.ParsedFunction;
import stanhebben.zenscript.definitions.ParsedFunctionArgument;
import stanhebben.zenscript.parser.ParseException;
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.type.ZenType;

/**
 * @author youyihj
 */
public class ParsedExpansion {

    private final ParsedFunction function;
    private final ZenType type;
    private final ZenParsedFile owner;

    public ParsedExpansion(ParsedFunction function, ZenType type, ZenParsedFile owner) {
        this.function = function;
        this.type = type;
        this.owner = owner;
    }

    public static ParsedExpansion parse(ZenTokener parser, IEnvironmentGlobal environment, ZenParsedFile owner) {
        parser.next();
        ZenType type = ZenType.read(parser, environment);
        Token token = parser.peek();
        if (token.getType() != ZenTokener.T_DOLLAR) {
            throw new ParseException(token, "$ expected");
        }
        ParsedFunction function = ParsedFunction.parse(parser, environment);
        ZenScriptHacks.setFirstArgument(function, new ParsedFunctionArgument("this", type, null));

        return new ParsedExpansion(function, type, owner);
    }

    public String getName() {
        return function.getName();
    }

    public ParsedFunction getFunction() {
        return function;
    }

    public ZenType getType() {
        return type;
    }

    public ZenParsedFile getOwner() {
        return owner;
    }
}
