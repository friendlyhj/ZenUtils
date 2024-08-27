package youyihj.zenutils.impl.member.bytecode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author youyihj
 */
public class MethodParameterParser {
    private int layer = 0;
    private final List<String> paramTypes = new ArrayList<>();
    private StringBuilder sb = new StringBuilder();

    private final String signature;

    public MethodParameterParser(String signature) {
        this.signature = signature;
    }

    List<String> parse() {
        String params = signature.substring(signature.indexOf('(') + 1, signature.indexOf(')'));
        boolean readingLongType = false;

        for (char c : params.toCharArray()) {
            sb.append(c);
            switch (c) {
                case 'V':
                case 'Z':
                case 'C':
                case 'B':
                case 'I':
                case 'F':
                case 'J':
                case 'D':
                    if (!readingLongType && layer == 0) {
                        endType();
                    }
                    break;
                case 'T':
                case 'L':
                    if (!readingLongType) {
                        readingLongType = true;
                        layer++;
                    }
                    break;
                case '<':
                case '>':
                    readingLongType = false;
                    break;
                case ';':
                    layer--;
                    readingLongType = false;
                    if (layer == 0) {
                        endType();
                    }
                    break;
            }
        }
        return paramTypes;
    }

    private void endType() {
        paramTypes.add(sb.toString());
        sb = new StringBuilder();
    }
}
