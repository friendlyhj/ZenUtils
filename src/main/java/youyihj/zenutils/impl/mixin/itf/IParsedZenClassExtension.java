package youyihj.zenutils.impl.mixin.itf;

import youyihj.zenutils.impl.zenscript.nat.ZenTypeJavaNative;

import java.util.List;

/**
 * @author youyihj
 */
public interface IParsedZenClassExtension {
    ZenTypeJavaNative getSuperClass();

    List<ZenTypeJavaNative> getInterfaces();

    void setSuperClass(ZenTypeJavaNative superClass);

    void setInterfaces(List<ZenTypeJavaNative> interfaces);
}
