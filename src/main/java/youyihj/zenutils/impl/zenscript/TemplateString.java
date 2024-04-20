package youyihj.zenutils.impl.zenscript;

import org.apache.commons.lang3.StringUtils;
import stanhebben.zenscript.ZenTokener;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.ExpressionString;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.parser.expression.ParsedExpression;
import stanhebben.zenscript.parser.expression.ParsedExpressionValue;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.StringUtil;
import stanhebben.zenscript.util.ZenPosition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author youyihj
 */
public class TemplateString {
    public static final int TOKEN_ID = 196;
    public static final String TOKEN_REGEX = "`([^`\\\\]|\\\\([`\\\\/bfnrt]|u[0-9a-fA-F]{4}))*`";

    @SuppressWarnings("unused")
    public static ParsedExpression getExpression(Token token, ZenPosition position, IEnvironmentGlobal environment) {
        String value = token.getValue();
        // TODO: handle escaped `

        // handle \n \t unicode escape
        value = StringUtil.unescapeString(StringUtils.wrap(value, '"'));
        // remove wrapping `
        value = value.substring(1, value.length() - 1);
        List<ParsedExpression> parsed = parse(position, environment, value);
        return new ParsedExpression(position) {
            @Override
            public IPartialExpression compile(IEnvironmentMethod environment, ZenType predictedType) {
                return new ExpressionTemplateString(position, parsed);
            }
        };
    }

    private static List<ParsedExpression> parse(ZenPosition position, IEnvironmentGlobal environment, String value){
        List<ParsedExpression> expressions = new ArrayList<>();
        StringBuilder toParseTokens = new StringBuilder();
        StringBuilder literals = new StringBuilder();
        boolean isInExpression = false;
        boolean frontDollar = false;
        for (char c : value.toCharArray()) {
            if (c == '$') {
                expressions.add(new ParsedExpressionValue(position, new ExpressionString(position, literals.toString())));
                literals = new StringBuilder();
                frontDollar = true;
                continue;
            } else if (frontDollar && c == '{') {
                isInExpression = true;
            } else if (isInExpression) {
                if (c == '}') {
                    isInExpression = false;
                    try {
                        expressions.add(ParsedExpression.read(new ZenTokener(toParseTokens + ";", environment.getEnvironment(), "", false), environment));
                    } catch (IOException e) {
                        throw new AssertionError(e);
                    }
                    toParseTokens = new StringBuilder();
                } else {
                    toParseTokens.append(c);
                }
            } else {
                literals.append(c);
            }
            frontDollar = false;
        }
        if (literals.length() > 0) {
            expressions.add(new ParsedExpressionValue(position, new ExpressionString(position, literals.toString())));
        }
        return expressions;
    }
}
