package youyihj.zenutils.impl.mixin.crafttweaker;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.definitions.zenclasses.ParsedClassConstructor;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.ZenPosition;
import youyihj.zenutils.impl.mixin.itf.IParsedClassConstructorExtension;
import youyihj.zenutils.impl.zenscript.nat.SymbolCallSuperConstructor;
import youyihj.zenutils.impl.zenscript.nat.ZenTypeJavaNative;

/**
 * @author youyihj
 */
@Mixin(value = ParsedClassConstructor.class, remap = false)
public abstract class MixinParsedClassConstructor implements IParsedClassConstructorExtension {
    private ZenTypeJavaNative superClass;

    @Override
    public void setSuperClass(ZenTypeJavaNative superClass) {
        this.superClass = superClass;
    }

    @WrapWithCondition(
            method = "writeAll",
            at = @At(
                    value = "INVOKE",
                    target = "Lstanhebben/zenscript/util/MethodOutput;loadObject(I)V",
                    ordinal = 0
            )
    )
    private boolean removeDefaultObjectIfHasSuperClass0(MethodOutput instance, int local) {
        return superClass == null;
    }

    @WrapWithCondition(
            method = "writeAll",
            at = @At(
                    value = "INVOKE",
                    target = "Lstanhebben/zenscript/util/MethodOutput;invokeSpecial(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V",
                    ordinal = 0
            )
    )
    private boolean removeDefaultObjectIfHasSuperClass1(MethodOutput instance, String owner, String name, String descriptor) {
        return superClass == null;
    }

    @Inject(method = "injectParameters", at = @At("HEAD"))
    private void injectSuper(IEnvironmentMethod environmentMethod, ZenPosition position, CallbackInfo ci) {
        if (superClass != null) {
            environmentMethod.putValue("super", new SymbolCallSuperConstructor(superClass, environmentMethod), position);
        }
    }
}
