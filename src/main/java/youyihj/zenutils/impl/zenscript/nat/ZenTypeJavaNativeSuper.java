package youyihj.zenutils.impl.zenscript.nat;

import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import youyihj.zenutils.impl.member.ClassData;
import youyihj.zenutils.impl.member.LookupRequester;

import java.util.List;

/**
 * @author youyihj
 */
public class ZenTypeJavaNativeSuper extends ZenTypeJavaNative {
    public ZenTypeJavaNativeSuper(ClassData clazz, List<ZenTypeJavaNative> superClasses) {
        super(clazz, superClasses);
    }

    @Override
    protected JavaNativeMemberSymbol getSymbol(String name, IEnvironmentGlobal environment, boolean isStatic) {
        return super.getSymbol(name, environment, isStatic).special();
    }

    @Override
    LookupRequester getLookupRequester(IEnvironmentGlobal environment) {
        return LookupRequester.SUBCLASS;
    }

    @Override
    public ZenTypeJavaNativeSuper toSuper() {
        return this;
    }
}
