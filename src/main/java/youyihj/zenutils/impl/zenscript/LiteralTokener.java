package youyihj.zenutils.impl.zenscript;

import stanhebben.zenscript.IZenCompileEnvironment;
import stanhebben.zenscript.ZenTokener;
import stanhebben.zenscript.parser.Token;

import java.io.IOException;
import java.util.List;

/**
 * @author youyihj
 */
public class LiteralTokener extends ZenTokener {
    private final List<Token> tokens;
    private int index = 0;

    public LiteralTokener(List<Token> tokens, IZenCompileEnvironment environment) throws IOException {
        super("", environment, "", false);
        this.tokens = tokens;
    }

    public static LiteralTokener create(List<Token> tokens, IZenCompileEnvironment environment) {
        try {
            return new LiteralTokener(tokens, environment);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Token peek() {
        return tokens.get(index);
    }

    @Override
    public boolean hasNext() {
        return index < tokens.size();
    }

    @Override
    public Token next() {
        return tokens.get(index++);
    }

    public int readTokenCount() {
        return index;
    }
}
