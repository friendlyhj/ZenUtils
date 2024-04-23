package youyihj.zenutils.impl.zenscript;

import stanhebben.zenscript.ZenTokener;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.expression.Expression;
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
    public static final int T_ESCAPE_CHAR = 197;

    @SuppressWarnings("unused")
    public static ParsedExpression getExpression(ZenTokener tokener, ZenPosition position, IEnvironmentGlobal environment) {
        List<ParsedExpression> parsed = parse(tokener, position, environment);
        return new ParsedExpressionValue(position, new ExpressionTemplateString(position, parsed));
    }

    public static Expression getExpression(List<Token> tokens, ZenPosition position, IEnvironmentGlobal environment) {
        return new ExpressionTemplateString(position, parse(tokens, position, environment));
    }

    private static void handleEscapeChar(Token token, StringBuilder sb) {
        char escapeChar = token.getValue().charAt(1);
        switch (escapeChar) {
            case 'n':
                sb.append('\n');
                break;
            case 't':
                sb.append('\t');
                break;
            case 'r':
                sb.append('\r');
                break;
            case 'b':
                sb.append('\b');
                break;
            case 'f':
                sb.append('\f');
                break;
            case '$':
                sb.append('$');
                break;
            case '`':
                sb.append('`');
                break;
            case 'u':
                sb.append(Character.toChars(Integer.parseInt(token.getValue().substring(2), 16)));
                break;
        }
    }

    private static List<ParsedExpression> parse(ZenTokener tokener, ZenPosition position, IEnvironmentGlobal environment) throws ParseException {
        ((ITokenStreamExtension) tokener).setAllowWhitespaceChannel(true);
        tokener.next();
        List<ParsedExpression> expressions = new ArrayList<>();
        StringBuilder literals = new StringBuilder();
        for (Token token = tokener.next(); token.getType() != T_BACKQUOTE; token = tokener.next()) {
            switch (token.getType()) {
                case T_ESCAPE_CHAR:
                    handleEscapeChar(token, literals);
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

    private static List<ParsedExpression> parse(List<Token> tokens, ZenPosition position, IEnvironmentGlobal environment) {
        List<ParsedExpression> expressions = new ArrayList<>();
        StringBuilder literals = new StringBuilder();
        for (int cursor = 0; cursor < tokens.size(); cursor++) {
            Token token = tokens.get(cursor);
            switch (token.getType()) {
                case T_ESCAPE_CHAR:
                    handleEscapeChar(token, literals);
                    break;
                case T_DOLLAR:
                    cursor++;
                    Token nextToken = tokens.get(cursor);
                    if (nextToken.getType() == T_AOPEN) {
                        expressions.add(new ParsedExpressionValue(position, new ExpressionString(position, literals.toString())));
                        literals = new StringBuilder();
                        cursor++;
                        LiteralTokener literalTokener = LiteralTokener.create(tokens.subList(cursor, tokens.size()), environment.getEnvironment());
                        expressions.add(ParsedExpression.read(literalTokener, environment));
                        cursor += literalTokener.readTokenCount();
                        nextToken = tokens.get(cursor);
                        if (nextToken.getType() != T_ACLOSE) {
                            throw new ParseException(nextToken, "} expected");
                        }
                    } else {
                        throw new ParseException(token, "{ expected");
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
        return expressions;
    }
}
