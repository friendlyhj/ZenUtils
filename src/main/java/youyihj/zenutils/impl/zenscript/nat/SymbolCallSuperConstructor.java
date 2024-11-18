package youyihj.zenutils.impl.zenscript.nat;

import stanhebben.zenscript.compiler.ITypeRegistry;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.util.ZenPosition;
import youyihj.zenutils.impl.member.LookupRequester;

/**
 * @author youyihj
 */
public class SymbolCallSuperConstructor implements IZenSymbol {
    private final ZenTypeJavaNative superClass;
    private final ITypeRegistry types;

    public SymbolCallSuperConstructor(ZenTypeJavaNative superClass, ITypeRegistry types) {
        this.superClass = superClass;
        this.types = types;
    }

    @Override
    public IPartialExpression instance(ZenPosition position) {
        return new PartialCallSuperConstructor(position, superClass.getClassData().constructors(LookupRequester.SUBCLASS), types);
    }
}
