package youyihj.zenutils.impl.zenscript;

import stanhebben.zenscript.ZenTokener;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.expression.ExpressionString;
import stanhebben.zenscript.parser.ParseException;
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.parser.expression.ParsedExpression;
import stanhebben.zenscript.parser.expression.ParsedExpressionValue;
import stanhebben.zenscript.util.ZenPosition;

import java.io.IOException;
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

    // problem: whitespace is not token
    private static List<ParsedExpression> parse(ZenTokener tokener, ZenPosition position, IEnvironmentGlobal environment) throws ParseException {
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
                        literals = new StringBuilder();
                        expressions.add(ParsedExpression.read(tokener, environment));
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
        return expressions;
    }

    // problem: could not handle nested expression properly
    private static List<ParsedExpression> parse(ZenPosition position, IEnvironmentGlobal environment, String value) throws ParseException {
        List<ParsedExpression> expressions = new ArrayList<>();
        StringBuilder toParseTokens = new StringBuilder();
        StringBuilder literals = new StringBuilder();
        char[] charArray = value.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            switch (c) {
                case '\\':
                    i++;
                    char nextChar = charArray[i];
                    switch (charArray[i]) {
                        case '`':
                        case '$':
                        case '\\':
                            literals.append(nextChar);
                            break;
                        case 'n':
                            literals.append('\n');
                            break;
                        case 't':
                            literals.append('\t');
                            break;
                        case 'b':
                            literals.append('\b');
                            break;
                        case 'f':
                            literals.append('\f');
                            break;
                        case 'r':
                            literals.append('\r');
                            break;
                        case 'u':
                            if (i + 4 > charArray.length) {
                                throw new ParseException(position.getFile(), position.getLine(), position.getLineOffset(), "too short unicode escape sequence");
                            }
                            String unicode = value.substring(i + 1, i + 5);
                            int unicodeInt;
                            try {
                                unicodeInt = Integer.parseInt(unicode, 16);
                            } catch (NumberFormatException e) {
                                throw new ParseException(position.getFile(), position.getLine(), position.getLineOffset(), "invalid hex of unicode escape sequence");
                            }
                            literals.append(Character.toChars(unicodeInt));
                            i += 4;
                            break;
                    }
                    break;
                case '$':
                    if (i + 1 < charArray.length && charArray[i + 1] == '{') {
                        expressions.add(new ParsedExpressionValue(position, new ExpressionString(position, literals.toString())));
                        literals = new StringBuilder();
                        i += 2;
                        int layer = 0;
                        for (; i < charArray.length; i++) {
                            char expressionChar = charArray[i];
                            if (expressionChar == '}') {
                                if (layer == 0) {
                                    ZenTokener tokener = parseExpression(position, environment, toParseTokens.toString());
                                    expressions.add(ParsedExpression.read(tokener, environment));
                                    toParseTokens = new StringBuilder();
                                    break;
                                } else {
                                    layer--;
                                    toParseTokens.append("}");
                                }
                            } else {
                                toParseTokens.append(expressionChar);
                                if (expressionChar == '{' && charArray[i - 1] == '$') {
                                    int j = i - 2;
                                    while (charArray[j] == '\\') {
                                        j--;
                                    }
                                    if (i - j % 2 == 0) {
                                        layer++;
                                    }
                                }
                            }
                        }
                    } else {
                        literals.append('$');
                    }
                    break;
                default:
                    literals.append(c);
                    break;
            }
        }
        if (toParseTokens.length() > 0) {
            throw new ParseException(position.getFile(), position.getLine(), position.getLineOffset(), "Unclosed expression bracket");
        }
        if (literals.length() > 0) {
            expressions.add(new ParsedExpressionValue(position, new ExpressionString(position, literals.toString())));
        }
        return expressions;
    }

    private static ZenTokener parseExpression(ZenPosition position, IEnvironmentGlobal environment, String expression) throws ParseException {
        ZenTokener tokener;
        try {
            tokener = new ZenTokener(expression + ";", environment.getEnvironment(), "", false);
        } catch (IOException e) {
            throw new ParseException(position.getFile(), position.getLine(), position.getLineOffset(), "empty expression");
        }
        tokener.setFile(position.getFile());
        return tokener;
    }
}
