package youyihj.zenutils.impl.zenscript;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import stanhebben.zenscript.ZenTokener;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.parser.expression.ParsedExpression;
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
        Pair<String, List<ParsedExpression>> parsed = parse(environment, value);
        return new ParsedExpression(position) {
            @Override
            public IPartialExpression compile(IEnvironmentMethod environment, ZenType predictedType) {
                return new ExpressionTemplateString(position, parsed.getKey(), parsed.getValue());
            }
        };
    }

    private static Pair<String, List<ParsedExpression>> parse(IEnvironmentGlobal environment, String value){
        StringBuilder format = new StringBuilder();
        List<ParsedExpression> expressions = new ArrayList<>();
        StringBuilder toParseTokens = new StringBuilder();
        boolean isInExpression = false;
        boolean frontDollar = false;
        for (char c : value.toCharArray()) {
            if (c == '$') {
                frontDollar = true;
                format.append("%s");
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
                format.append(c);
            }
            frontDollar = false;
        }
        return Pair.of(format.toString(), expressions);
    }
}
