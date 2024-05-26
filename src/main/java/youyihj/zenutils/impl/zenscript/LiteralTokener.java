package youyihj.zenutils.impl.zenscript;

import com.google.common.collect.PeekingIterator;
import stanhebben.zenscript.IZenCompileEnvironment;
import stanhebben.zenscript.ZenParsedFile;
import stanhebben.zenscript.ZenTokener;
import stanhebben.zenscript.parser.Token;

import java.io.IOException;

/**
 * @author youyihj
 */
public class LiteralTokener extends ZenTokener {
    private final PeekingIterator<Token> tokens;
    private int readCounter = 0;

    public LiteralTokener(PeekingIterator<Token> tokens, IZenCompileEnvironment environment) throws IOException {
        super("", environment, "", false);
        this.tokens = tokens;
    }

    public static LiteralTokener create(PeekingIterator<Token> tokens, IZenCompileEnvironment environment, ZenParsedFile file) {
        try {
            LiteralTokener tokener = new LiteralTokener(tokens, environment);
            tokener.setFile(file);
            return tokener;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Token peek() {
        return tokens.peek();
    }

    @Override
    public boolean hasNext() {
        return tokens.hasNext();
    }

    @Override
    public Token next() {
        readCounter++;
        return tokens.next();
    }

    public int readTokenCount() {
        return readCounter;
    }
}
