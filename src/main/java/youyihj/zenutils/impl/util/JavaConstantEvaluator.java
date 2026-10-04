package youyihj.zenutils.impl.util;

public class JavaConstantEvaluator {

    public enum Type {
        BYTE, SHORT, CHAR, INT, LONG, FLOAT, DOUBLE
    }

    public static final class Value {
        public final Type type;
        public final long integral;
        public final double floating;

        private Value(Type type, long integral, double floating) {
            this.type = type;
            this.integral = integral;
            this.floating = floating;
        }

        public static Value ofByte(byte v) {
            return new Value(Type.BYTE, v, 0);
        }

        public static Value ofShort(short v) {
            return new Value(Type.SHORT, v, 0);
        }

        public static Value ofChar(char v) {
            return new Value(Type.CHAR, v, 0);
        }

        public static Value ofInt(int v) {
            return new Value(Type.INT, v, 0);
        }

        public static Value ofLong(long v) {
            return new Value(Type.LONG, v, 0);
        }

        public static Value ofFloat(float v) {
            return new Value(Type.FLOAT, 0, v);
        }

        public static Value ofDouble(double v) {
            return new Value(Type.DOUBLE, 0, v);
        }

        public int asInt() {
            switch (type) {
                case BYTE:
                case SHORT:
                case CHAR:
                case INT:
                case LONG:
                    return (int) integral;

                case FLOAT:
                    return (int) ((float) floating);

                case DOUBLE:
                    return (int) floating;

                default:
                    throw new AssertionError();
            }
        }

        public long asLong() {
            switch (type) {
                case BYTE:
                case SHORT:
                case CHAR:
                case INT:
                case LONG:
                    return integral;

                case FLOAT:
                    return (long) ((float) floating);

                case DOUBLE:
                    return (long) floating;

                default:
                    throw new AssertionError();
            }
        }

        public float asFloat() {
            switch (type) {
                case BYTE:
                case SHORT:
                case CHAR:
                case INT:
                case LONG:
                    return (float) integral;

                case FLOAT:
                case DOUBLE:
                    return (float) floating;

                default:
                    throw new AssertionError();
            }
        }

        public double asDouble() {
            switch (type) {
                case BYTE:
                case SHORT:
                case CHAR:
                case INT:
                case LONG:
                    return (double) integral;

                case FLOAT:
                    return (double) ((float) floating);

                case DOUBLE:
                    return floating;

                default:
                    throw new AssertionError();
            }
        }

        public byte asByte() {
            switch (type) {
                case BYTE:
                case SHORT:
                case CHAR:
                case INT:
                case LONG:
                    return (byte) integral;

                case FLOAT:
                    return (byte) ((float) floating);

                case DOUBLE:
                    return (byte) floating;

                default:
                    throw new AssertionError();
            }
        }

        public short asShort() {
            switch (type) {
                case BYTE:
                case SHORT:
                case CHAR:
                case INT:
                case LONG:
                    return (short) integral;

                case FLOAT:
                    return (short) ((float) floating);

                case DOUBLE:
                    return (short) floating;

                default:
                    throw new AssertionError();
            }
        }
    }

    public static Value evaluate(String expression) {
        Parser p = new Parser(expression);

        Value result = p.parseExpression();

        p.skipWhitespace();

        if (p.hasNext()) {
            throw new IllegalArgumentException(
                    "Unexpected input at position "
                            + p.pos
                            + ": "
                            + expression.substring(p.pos)
            );
        }

        return result;
    }

    private static final class Parser {
        private final String s;
        private int pos;

        private Parser(String s) {
            this.s = s;
        }

        private boolean hasNext() {
            return pos < s.length();
        }

        private void skipWhitespace() {
            while (hasNext() && Character.isWhitespace(s.charAt(pos))) {
                pos++;
            }
        }

        private boolean match(char c) {
            skipWhitespace();

            if (hasNext() && s.charAt(pos) == c) {
                pos++;
                return true;
            }

            return false;
        }

        private Value parseExpression() {
            return parseAdditive();
        }

        private Value parseAdditive() {
            Value left = parseMultiplicative();

            while (true) {
                skipWhitespace();

                if (match('+')) {
                    left = binary(left, parseMultiplicative(), '+');
                } else if (match('-')) {
                    left = binary(left, parseMultiplicative(), '-');
                } else {
                    return left;
                }
            }
        }

        private Value parseMultiplicative() {
            Value left = parseUnary();

            while (true) {
                skipWhitespace();

                if (match('*')) {
                    left = binary(left, parseUnary(), '*');
                } else if (match('/')) {
                    left = binary(left, parseUnary(), '/');
                } else if (match('%')) {
                    left = binary(left, parseUnary(), '%');
                } else {
                    return left;
                }
            }
        }

        private Value parseUnary() {
            skipWhitespace();

            if (match('+')) {
                return unaryPromote(parseUnary());
            }

            if (match('-')) {
                Value v = unaryPromote(parseUnary());

                switch (v.type) {
                    case INT:
                        return Value.ofInt(-v.asInt());

                    case LONG:
                        return Value.ofLong(-v.asLong());

                    case FLOAT:
                        return Value.ofFloat(-v.asFloat());

                    case DOUBLE:
                        return Value.ofDouble(-v.asDouble());

                    default:
                        throw new AssertionError();
                }
            }

            if (hasNext() && s.charAt(pos) == '(') {
                int saved = pos;

                Type castType = tryParseCast();

                if (castType != null) {
                    Value operand = parseUnary();
                    return cast(operand, castType);
                }

                pos = saved;

                match('(');

                Value v = parseExpression();

                if (!match(')')) {
                    throw error("Expected ')'");
                }

                return v;
            }

            return parseLiteral();
        }

        private Type tryParseCast() {
            int start = pos;

            if (!match('(')) {
                return null;
            }

            skipWhitespace();

            int wordStart = pos;

            while (hasNext()
                    && Character.isJavaIdentifierPart(s.charAt(pos))) {
                pos++;
            }

            if (wordStart == pos) {
                pos = start;
                return null;
            }

            String word = s.substring(wordStart, pos);

            Type t = null;

            switch (word) {
                case "byte":
                    t = Type.BYTE;
                    break;
                case "short":
                    t = Type.SHORT;
                    break;
                case "char":
                    t = Type.CHAR;
                    break;
                case "int":
                    t = Type.INT;
                    break;
                case "long":
                    t = Type.LONG;
                    break;
                case "float":
                    t = Type.FLOAT;
                    break;
                case "double":
                    t = Type.DOUBLE;
                    break;
            }

            if (t == null) {
                pos = start;
                return null;
            }

            skipWhitespace();

            if (!match(')')) {
                pos = start;
                return null;
            }

            return t;
        }

        private Value parseLiteral() {
            skipWhitespace();

            int start = pos;

            boolean sawDot = false;
            boolean sawExp = false;
            boolean sawHexExp = false;

            if (pos + 1 < s.length()
                    && s.charAt(pos) == '0'
                    && (s.charAt(pos + 1) == 'x'
                    || s.charAt(pos + 1) == 'X')) {

                pos += 2;

                while (hasNext()) {
                    char c = s.charAt(pos);

                    if (isHexDigit(c) || c == '_') {
                        pos++;
                    } else if (c == '.') {
                        sawDot = true;
                        pos++;
                    } else if (c == 'p' || c == 'P') {
                        sawHexExp = true;
                        pos++;

                        if (hasNext()) {
                            char sign = s.charAt(pos);

                            if (sign == '+' || sign == '-') {
                                pos++;
                            }
                        }
                    } else {
                        break;
                    }
                }

            } else {

                while (hasNext()) {
                    char c = s.charAt(pos);

                    if (Character.isDigit(c) || c == '_') {
                        pos++;
                    } else if (c == '.') {
                        sawDot = true;
                        pos++;
                    } else if (c == 'e' || c == 'E') {
                        sawExp = true;
                        pos++;

                        if (hasNext()) {
                            char sign = s.charAt(pos);

                            if (sign == '+' || sign == '-') {
                                pos++;
                            }
                        }
                    } else {
                        break;
                    }
                }
            }

            if (start == pos) {
                throw error("Expected numeric literal");
            }

            char suffix = 0;

            if (hasNext()) {
                char c = s.charAt(pos);

                if (c == 'f' || c == 'F'
                        || c == 'd' || c == 'D'
                        || c == 'l' || c == 'L') {
                    suffix = c;
                    pos++;
                }
            }

            String token = s.substring(start, pos);

            String core;

            if (suffix == 0) {
                core = token;
            } else {
                core = token.substring(0, token.length() - 1);
            }

            core = core.replace("_", "");

            boolean floatingLiteral =
                    sawDot
                            || sawExp
                            || sawHexExp
                            || suffix == 'f'
                            || suffix == 'F'
                            || suffix == 'd'
                            || suffix == 'D';

            try {
                if (floatingLiteral) {

                    if (suffix == 'f' || suffix == 'F') {
                        return Value.ofFloat(
                                Float.parseFloat(core)
                        );
                    }

                    return Value.ofDouble(
                            Double.parseDouble(core)
                    );
                }

                if (suffix == 'l' || suffix == 'L') {
                    return Value.ofLong(
                            parseIntegerLiteral(core)
                    );
                }

                long value = parseIntegerLiteral(core);

                if (value >= Integer.MIN_VALUE
                        && value <= Integer.MAX_VALUE) {
                    return Value.ofInt((int) value);
                }

                throw error(
                        "Integer literal out of int range: " + core
                );

            } catch (NumberFormatException e) {
                throw error(
                        "Invalid numeric literal: " + token
                );
            }
        }

        private long parseIntegerLiteral(String text) {
            int radix = 10;
            String digits = text;

            if (text.startsWith("0x")
                    || text.startsWith("0X")) {

                radix = 16;
                digits = text.substring(2);

            } else if (text.startsWith("0b")
                    || text.startsWith("0B")) {

                radix = 2;
                digits = text.substring(2);
            }

            return Long.parseLong(digits, radix);
        }

        private boolean isHexDigit(char c) {
            return (c >= '0' && c <= '9')
                    || (c >= 'a' && c <= 'f')
                    || (c >= 'A' && c <= 'F');
        }

        private IllegalArgumentException error(String message) {
            return new IllegalArgumentException(
                    message + " at position " + pos
            );
        }
    }

    public static Value unaryPromote(Value v) {
        switch (v.type) {
            case BYTE:
            case SHORT:
            case CHAR:
                return Value.ofInt(v.asInt());

            default:
                return v;
        }
    }

    public static Type binaryType(Value a, Value b) {

        if (a.type == Type.DOUBLE
                || b.type == Type.DOUBLE) {
            return Type.DOUBLE;
        }

        if (a.type == Type.FLOAT
                || b.type == Type.FLOAT) {
            return Type.FLOAT;
        }

        if (a.type == Type.LONG
                || b.type == Type.LONG) {
            return Type.LONG;
        }

        return Type.INT;
    }

    public static Value binary(Value a, Value b, char op) {

        Type t = binaryType(a, b);

        switch (t) {

            case DOUBLE: {
                double x = a.asDouble();
                double y = b.asDouble();
                double r;

                switch (op) {
                    case '+':
                        r = x + y;
                        break;
                    case '-':
                        r = x - y;
                        break;
                    case '*':
                        r = x * y;
                        break;
                    case '/':
                        r = x / y;
                        break;
                    case '%':
                        r = x % y;
                        break;
                    default:
                        throw new AssertionError();
                }

                return Value.ofDouble(r);
            }

            case FLOAT: {
                float x = a.asFloat();
                float y = b.asFloat();
                float r;

                switch (op) {
                    case '+':
                        r = x + y;
                        break;
                    case '-':
                        r = x - y;
                        break;
                    case '*':
                        r = x * y;
                        break;
                    case '/':
                        r = x / y;
                        break;
                    case '%':
                        r = x % y;
                        break;
                    default:
                        throw new AssertionError();
                }

                return Value.ofFloat(r);
            }

            case LONG: {
                long x = a.asLong();
                long y = b.asLong();
                long r;

                switch (op) {
                    case '+':
                        r = x + y;
                        break;
                    case '-':
                        r = x - y;
                        break;
                    case '*':
                        r = x * y;
                        break;
                    case '/':
                        r = x / y;
                        break;
                    case '%':
                        r = x % y;
                        break;
                    default:
                        throw new AssertionError();
                }

                return Value.ofLong(r);
            }

            case INT: {
                int x = a.asInt();
                int y = b.asInt();
                int r;

                switch (op) {
                    case '+':
                        r = x + y;
                        break;
                    case '-':
                        r = x - y;
                        break;
                    case '*':
                        r = x * y;
                        break;
                    case '/':
                        r = x / y;
                        break;
                    case '%':
                        r = x % y;
                        break;
                    default:
                        throw new AssertionError();
                }

                return Value.ofInt(r);
            }

            default:
                throw new AssertionError();
        }
    }

    public static Value cast(Value v, Type target) {

        switch (target) {

            case BYTE:
                return Value.ofByte(
                        v.asByte()
                );

            case SHORT:
                return Value.ofShort(
                        v.asShort()
                );

            case CHAR:
                return Value.ofChar(
                        (char) v.asDouble()
                );

            case INT:
                if (v.type == Type.FLOAT) {
                    return Value.ofInt(
                            (int) v.asFloat()
                    );
                }

                if (v.type == Type.DOUBLE) {
                    return Value.ofInt(
                            (int) v.asDouble()
                    );
                }

                return Value.ofInt(
                        (int) v.asLong()
                );

            case LONG:
                if (v.type == Type.FLOAT) {
                    return Value.ofLong(
                            (long) v.asFloat()
                    );
                }

                if (v.type == Type.DOUBLE) {
                    return Value.ofLong(
                            (long) v.asDouble()
                    );
                }

                return Value.ofLong(
                        v.asLong()
                );

            case FLOAT:
                return Value.ofFloat(
                        v.asFloat()
                );

            case DOUBLE:
                return Value.ofDouble(
                        v.asDouble()
                );

            default:
                throw new AssertionError();
        }
    }

    /*
    public static void printResult(String expr) {

        Value v = evaluate(expr);

        System.out.println("Expression : " + expr);
        System.out.println("Type       : " + v.type);

        switch (v.type) {

            case FLOAT: {
                float f = v.asFloat();

                System.out.println(
                        "Value      : " + Float.toString(f)
                );

                System.out.printf(
                        "As double  : %.20g%n",
                        (double) f
                );

                System.out.println(
                        "Hex        : " + Float.toHexString(f)
                );

                System.out.printf(
                        "Raw bits   : 0x%08X%n",
                        Float.floatToRawIntBits(f)
                );

                break;
            }

            case DOUBLE: {
                double d = v.asDouble();

                System.out.println(
                        "Value      : " + Double.toString(d)
                );

                System.out.printf(
                        "Precision  : %.20g%n",
                        d
                );

                System.out.println(
                        "Hex        : " + Double.toHexString(d)
                );

                System.out.printf(
                        "Raw bits   : 0x%016X%n",
                        Double.doubleToRawLongBits(d)
                );

                break;
            }

            default:
                System.out.println(
                        "Value      : " + v.integral
                );
        }

        System.out.println();
    }

    public static void main(String[] args) {

        printResult("1.0 / 137.0");

        printResult("0.15");

        printResult("0.15f");

        printResult("(double) 0.15f");

        printResult("0.15f + 0.15f");

        printResult("1 / 137");

        printResult("1.0f / 137.0f");

        printResult("1.0f / 137.0");

        printResult("(float) (1.0 / 137.0)");

        printResult("2147483647 + 1");
    }
     */

}