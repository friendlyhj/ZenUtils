package youyihj.zenutils.impl.zenscript;

import stanhebben.zenscript.ZenTokener;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.expression.ExpressionString;
import stanhebben.zenscript.parser.ParseException;
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.parser.expression.ParsedExpression;
import stanhebben.zenscript.parser.expression.ParsedExpressionValue;
import stanhebben.zenscript.util.ZenPosition;

import java.util.ArrayList;
import java.util.List;

import static stanhebben.zenscript.ZenTokener.*;

/**
 * @author youyihj
 */
public class TemplateString {
    public static final int T_BACKQUOTE = 196;
    public static final int T_ESCAPE_CHARS = 197;

    @SuppressWarnings("unused")
    public static ParsedExpression getExpression(ZenTokener tokener, ZenPosition position, IEnvironmentGlobal environment) {
        List<ParsedExpression> parsed = parse(tokener, position, environment);
        return new ParsedExpressionValue(position, new ExpressionTemplateString(position, parsed));
    }

    private static List<ParsedExpression> parse(ZenTokener tokener, ZenPosition position, IEnvironmentGlobal environment) throws ParseException {
        ((ITokenStreamExtension) tokener).setAllowWhitespaceChannel(true);
        tokener.next();
        List<ParsedExpression> expressions = new ArrayList<>();
        StringBuilder literals = new StringBuilder();
        for (Token token = tokener.next(); token.getType() != T_BACKQUOTE; token = tokener.next()) {
            switch (token.getType()) {
                case T_ESCAPE_CHARS:
                    char escapeChar = token.getValue().charAt(1);
                    switch (escapeChar) {
                        case 'n':
                            literals.append('\n');
                            break;
                        case 't':
                            literals.append('\t');
                            break;
                        case 'r':
                            literals.append('\r');
                            break;
                        case 'b':
                            literals.append('\b');
                            break;
                        case 'f':
                            literals.append('\f');
                            break;
                        case '$':
                            literals.append('$');
                            break;
                        case '`':
                            literals.append('`');
                            break;
                        case 'u':
                            literals.append(Character.toChars(Integer.parseInt(token.getValue().substring(2), 16)));
                            break;
                    }
                    break;
                case T_DOLLAR:
                    if (tokener.optional(T_AOPEN) != null) {
                        expressions.add(new ParsedExpressionValue(position, new ExpressionString(position, literals.toString())));
                        ((ITokenStreamExtension) tokener).setAllowWhitespaceChannel(false);
                        literals = new StringBuilder();
                        expressions.add(ParsedExpression.read(tokener, environment));
                        ((ITokenStreamExtension) tokener).setAllowWhitespaceChannel(true);
                        tokener.required(T_ACLOSE, "} expected");
                    } else {
                        literals.append('$');
                    }
                    break;
                default:
                    literals.append(token.getValue());
                    break;
            }
        }
        if (literals.length() > 0) {
            expressions.add(new ParsedExpressionValue(position, new ExpressionString(position, literals.toString())));
        }
        if (expressions.isEmpty()) {
            expressions.add(new ParsedExpressionValue(position, new ExpressionString(position, "")));
        }
        ((ITokenStreamExtension) tokener).setAllowWhitespaceChannel(false);
        return expressions;
    }
}
