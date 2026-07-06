package youyihj.zenutils.impl.zenscript;

import stanhebben.zenscript.parser.CompiledDFA;
import stanhebben.zenscript.parser.NFA;
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.parser.TokenStream;
import stanhebben.zenscript.util.ZenPosition;
import youyihj.zenutils.api.util.ReflectionInvoked;
import youyihj.zenutils.impl.mixin.crafttweaker.TokenStreamAccessor;

import java.io.IOException;

import static stanhebben.zenscript.ZenTokener.*;

/**
 * @author youyihj
 */
public class TemplateStringTokener extends TokenStream {

    public static final int T_FALLBACK = Integer.MAX_VALUE;
    public static final String T_FALLBACK_REGEX = ".";

    private static CompiledDFA DFA;

    @ReflectionInvoked(asm = true)
    private /* final */ ZenPosition startPosition;
    @ReflectionInvoked(asm = true)
    private boolean constructing;

    public static void setupDFAFromZenTokener() {
        String[] regexp = new String[] {
                "$",
                "\\{",
                "\\}",
                ExtendZenTokens.T_ESCAPE_CHAR_REGEX,
                T_FALLBACK_REGEX
        };
        int[] finals = new int[] {
                T_DOLLAR,
                T_AOPEN,
                T_ACLOSE,
                ExtendZenTokens.T_ESCAPE_CHAR,
                T_FALLBACK
        };
        DFA = new NFA(regexp, finals).toDFA().optimize().compile();
    }

    public TemplateStringTokener(String data, @ReflectionInvoked(asm = true) ZenPosition startPosition) throws IOException {
        // written by asm
        // this.startPosition = startPosition;
        // this.constructing = true;
        super(data, DFA);
        setFile(startPosition.getFile());
    }

    public static TemplateStringTokener create(String data, ZenPosition startPosition) {
        try {
            return new TemplateStringTokener(data, startPosition);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Token process(Token token) {
        if (constructing) {
            TokenStreamAccessor accessor = (TokenStreamAccessor) this;
            accessor.setLine(startPosition.getLine());
            accessor.setLineOffset(startPosition.getLineOffset());
            constructing = false;
        }
        if (token == null) {
            return new Token("", -1, startPosition);
        }
        ZenPosition position = token.getPosition();
        int offsetLine = position.getLine() + startPosition.getLine() - 1;
        int offsetLineOffset = position.getLine() == 1 ? position.getLineOffset() : position.getLineOffset() + startPosition.getLineOffset() - 1;
        ZenPosition offsetPosition = new ZenPosition(startPosition.getFile(), offsetLine, offsetLineOffset, position.getFileName());
        return new Token(token.getValue(), token.getType(), offsetPosition);
    }
}
