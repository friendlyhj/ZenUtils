package youyihj.zenutils.impl.mixin.crafttweaker;

import com.google.gson.JsonElement;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import org.apache.commons.lang3.tuple.Pair;
import org.objectweb.asm.ClassWriter;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import stanhebben.zenscript.ZenTokener;
import stanhebben.zenscript.compiler.EnvironmentClass;
import stanhebben.zenscript.compiler.EnvironmentScript;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.definitions.zenclasses.ParsedClassConstructor;
import stanhebben.zenscript.definitions.zenclasses.ParsedZenClass;
import stanhebben.zenscript.expression.ExpressionInvalid;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.parser.ParseException;
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;
import youyihj.zenutils.impl.member.ClassData;
import youyihj.zenutils.impl.mixin.itf.IEnvironmentClassExtension;
import youyihj.zenutils.impl.mixin.itf.IParsedClassConstructorExtension;
import youyihj.zenutils.impl.mixin.itf.IParsedZenClassExtension;
import youyihj.zenutils.impl.runtime.ScriptStatus;
import youyihj.zenutils.impl.util.InternalUtils;
import youyihj.zenutils.impl.zenscript.MixinPreprocessor;
import youyihj.zenutils.impl.zenscript.mixin.ExpressionMixinThis;
import youyihj.zenutils.impl.zenscript.mixin.MixinAnnotationTranslator;
import youyihj.zenutils.impl.zenscript.mixin.ZenMixin;
import youyihj.zenutils.impl.zenscript.nat.ExpressionSuper;
import youyihj.zenutils.impl.zenscript.nat.NativeClassValidate;
import youyihj.zenutils.impl.zenscript.nat.ParsedZenClassCompile;
import youyihj.zenutils.impl.zenscript.nat.ZenTypeJavaNative;

import java.util.ArrayList;
import java.util.List;

/**
 * @author youyihj
 */
@Mixin(value = ParsedZenClass.class, remap = false)
public abstract class MixinParsedZenClass implements IParsedZenClassExtension {

    @Shadow
    @Final
    public ZenPosition position;

    @Shadow
    @Final
    @Mutable
    public String className;

    @Shadow
    public Class<?> thisClass;

    @Shadow
    @Final
    public String name;

    @Unique
    List<MixinPreprocessor> preprocessors;

    @Unique
    List<String> mixinTargets;

    @Unique
    ZenTypeJavaNative superClass;

    @Unique
    List<ZenTypeJavaNative> interfaces = new ArrayList<>();

    @Inject(method = "parse", at = @At(value = "INVOKE", target = "Lstanhebben/zenscript/ZenTokener;required(ILjava/lang/String;)Lstanhebben/zenscript/parser/Token;", ordinal = 1))
    private static void readParentClasses(ZenTokener parser, IEnvironmentGlobal environmentGlobal, CallbackInfoReturnable<ParsedZenClass> cir, @Share("superClass") LocalRef<ZenTypeJavaNative> superClass, @Share("interfaces") LocalRef<List<ZenTypeJavaNative>> interfacesRef) {
        List<ZenTypeJavaNative> interfaces = new ArrayList<>();
        Token extendsKeyword = parser.optional(ZenTokener.T_ID);
        if (extendsKeyword != null && extendsKeyword.getValue().equals("extends")) {
            ZenType firstSuperClass = ZenType.read(parser, environmentGlobal);
            if (firstSuperClass instanceof ZenTypeJavaNative) {
                ZenTypeJavaNative firstSuperClass1 = (ZenTypeJavaNative) firstSuperClass;
                if (firstSuperClass1.getClassData().isInterface()) {
                    interfaces.add(firstSuperClass1);
                } else {
                    superClass.set(firstSuperClass1);
                }
            } else {
                environmentGlobal.error(parser.peek().getPosition(), "super class only supports native type currently");
            }
            while (parser.optional(ZenTokener.T_COMMA) != null) {
                ZenType interfaceType = ZenType.read(parser, environmentGlobal);
                if (interfaceType instanceof ZenTypeJavaNative) {
                    ZenTypeJavaNative interfaceJavaType = (ZenTypeJavaNative) interfaceType;
                    if (interfaceJavaType.getClassData().isInterface()) {
                        interfaces.add(interfaceJavaType);
                    } else {
                        environmentGlobal.error(parser.peek().getPosition(), "only multi-inheritance interfaces");
                    }
                } else {
                    environmentGlobal.error(parser.peek().getPosition(), "super class only supports native type currently");
                }
            }
        }
        interfacesRef.set(interfaces);
    }

    @Inject(method = "parse", at = @At(value = "INVOKE", target = "Lstanhebben/zenscript/compiler/EnvironmentScript;putValue(Ljava/lang/String;Lstanhebben/zenscript/symbols/IZenSymbol;Lstanhebben/zenscript/util/ZenPosition;)V"))
    private static void setParentClasses(ZenTokener parser, IEnvironmentGlobal environmentGlobal, CallbackInfoReturnable<ParsedZenClass> cir, @Local ParsedZenClass classTemplate, @Share("superClass") LocalRef<ZenTypeJavaNative> superClass, @Share("interfaces") LocalRef<List<ZenTypeJavaNative>> interfacesRef) {
        ((IParsedZenClassExtension) classTemplate).setSuperClass(superClass.get());
        ((IParsedZenClassExtension) classTemplate).setInterfaces(interfacesRef.get());
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void initAnnotation(ZenPosition position, String name, String className, EnvironmentScript classEnvironment, CallbackInfo ci) {
        this.preprocessors = MixinAnnotationTranslator.findAnnotation(position);
        boolean isMixinClass;
        for (MixinPreprocessor preprocessor : preprocessors) {
            Pair<String, JsonElement> annotation = preprocessor.getAnnotation();
            if (annotation.getLeft().equals("Mixin")) {
                isMixinClass = true;
                mixinTargets = MixinAnnotationTranslator.getMixinTargets(annotation.getRight().getAsJsonObject());
                for (String target : mixinTargets) {
                    if (target.startsWith("youyihj")) {
                        classEnvironment.error(position, "why you try to mixin zenutils itself?");
                    }
                    ClassData classData;
                    try {
                        classData = InternalUtils.getClassDataFetcher().forName(target);
                        if (classData.fetcher() != InternalUtils.getClassDataFetcher()) {
                            if (InternalUtils.getScriptStatus() == ScriptStatus.INIT) {
                                isMixinClass = false;
                                classEnvironment.warning(position, "Skip loading mixin class " + name + ", because the target " + target + " is a non-mod class or already loaded");
                            }
                        } else if (!NativeClassValidate.isValid(classData, true)) {
                            isMixinClass = false;
                            classEnvironment.warning(position, "Skip loading mixin class " + name + ", because the target " + target + " is not accessible");
                        }
                    } catch (ClassNotFoundException e) {
                        isMixinClass = false;
                        classEnvironment.warning(position, "Skip loading mixin class " + name + ", because the target " + target + " is not found");
                    }
                }
                if (isMixinClass) {
                    this.className = ZenMixin.handleMixinClassName(name);
                }
                break;
            }
        }
    }

    @ModifyConstant(method = "writeClass", constant = @Constant(stringValue = "java/lang/Object"))
    private String setSuperClassASM(String original) {
        if (superClass != null) {
            return superClass.getClassData().internalName();
        } else {
            return original;
        }
    }

    @ModifyArg(method = "writeClass", at = @At(value = "INVOKE", target = "Lorg/objectweb/asm/ClassWriter;visit(IILjava/lang/String;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;)V"))
    private String[] setInterfacesASM(String[] original) {
        return interfaces.stream()
                .map(it -> it.getClassData().internalName())
                .toArray(String[]::new);
    }

    @Inject(method = "writeClass", at = @At(value = "INVOKE", target = "Lorg/objectweb/asm/ClassWriter;visit(IILjava/lang/String;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;)V", shift = At.Shift.AFTER))
    private void applyAnnotation(IEnvironmentGlobal environmentGlobal, CallbackInfo ci, @Local ClassWriter cw) {
        for (MixinPreprocessor mixinPreprocessor : preprocessors) {
            Pair<String, JsonElement> annotation = mixinPreprocessor.getAnnotation();
            MixinAnnotationTranslator.translate(
                    annotation.getLeft(), annotation.getRight(),
                    cw::visitAnnotation,
                    it -> new ParseException(position.getFile(), position.getLine() - 1, 0, it)
            );
        }
    }

    @Inject(method = "writeClass", at = @At(value = "INVOKE", target = "Lstanhebben/zenscript/compiler/IEnvironmentGlobal;putClass(Ljava/lang/String;[B)V", shift = At.Shift.AFTER), cancellable = true)
    private void fixClassCompile(IEnvironmentGlobal environmentGlobal, CallbackInfo ci, @Local byte[] thisClassArray) {
        if (className.startsWith("youyihj/zenutils/impl/mixin")) {
            thisClass = Object.class;
        } else {
            thisClass = ParsedZenClassCompile.compile(className, thisClassArray);
        }
        ci.cancel();
    }

    @WrapOperation(method = "writeClass", at = @At(value = "INVOKE", target = "Lstanhebben/zenscript/compiler/EnvironmentClass;putValue(Ljava/lang/String;Lstanhebben/zenscript/symbols/IZenSymbol;Lstanhebben/zenscript/util/ZenPosition;)V"))
    private void injectSymbols(EnvironmentClass instance, String name, IZenSymbol value, ZenPosition position, Operation<Void> original) {
        original.call(instance, name, value, position);
        if (mixinTargets != null && mixinTargets.size() == 1) {
            instance.putValue("this0", position1 -> new ExpressionMixinThis(position1, mixinTargets.get(0).replace('.', '/'), instance), position);
            ((IEnvironmentClassExtension) instance).setMixinTargets(mixinTargets);
            ArrayList<ZenTypeJavaNative> superClasses = new ArrayList<>(interfaces);
            if (superClass != null) {
                superClasses.add(superClass);
            }
            ((IEnvironmentClassExtension) instance).setSubClasses(superClasses);
        }
        if (superClass != null) {
            instance.putValue("super", position1 -> new ExpressionSuper(position1, superClass), position);
        }
    }

    @Inject(method = "getMember", at = @At(value = "INVOKE", target = "Lstanhebben/zenscript/compiler/IEnvironmentGlobal;error(Ljava/lang/String;)V"), cancellable = true)
    private void addSuperMembers(ZenPosition position, IEnvironmentGlobal environment, IPartialExpression value, String name, boolean isStatic, CallbackInfoReturnable<IPartialExpression> cir) {
        if (superClass != null) {
            IPartialExpression superClassMember = superClass.getMember(position, environment, value, name, false);
            if (!(superClassMember instanceof ExpressionInvalid)) {
                cir.setReturnValue(superClassMember);
                return;
            }
        }
        for (ZenTypeJavaNative anInterface : interfaces) {
            IPartialExpression interfaceMember = anInterface.getMember(position, environment, value, name, false);
            if (!(interfaceMember instanceof ExpressionInvalid)) {
                cir.setReturnValue(interfaceMember);
                return;
            }
        }
    }

    @Inject(method = "addConstructor", at = @At("HEAD"))
    private void setConstructorOwner(ParsedClassConstructor parsedClassConstructor, CallbackInfo ci) {
        ((IParsedClassConstructorExtension) parsedClassConstructor).setSuperClass(superClass);
    }

    @Override
    public ZenTypeJavaNative getSuperClass() {
        return superClass;
    }

    @Override
    public void setSuperClass(ZenTypeJavaNative superClass) {
        this.superClass = superClass;
    }

    @Override
    public List<ZenTypeJavaNative> getInterfaces() {
        return interfaces;
    }

    @Override
    public void setInterfaces(List<ZenTypeJavaNative> interfaces) {
        this.interfaces = interfaces;
    }
}
