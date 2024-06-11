package youyihj.zenutils.impl.mixin.crafttweaker;

import org.objectweb.asm.tree.*;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import youyihj.zenutils.api.util.ReflectionInvoked;
import youyihj.zenutils.impl.zenscript.TemplateString;

import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import static org.objectweb.asm.Opcodes.*;

/**
 * @author youyihj
 */
@ReflectionInvoked
public class CraftTweakerMixinPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return "";
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return Collections.emptyList();
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        //noinspection DuplicatedCode
        if (targetClassName.endsWith("ParsedExpression")) {
            MethodNode method = targetClass.methods.stream()
                                                   .filter(it -> it.name.equals("readPrimaryExpression"))
                                                   .findFirst()
                                                   .orElseThrow(NoSuchMethodError::new);
            InsnList instructions = method.instructions;
            ListIterator<AbstractInsnNode> iter = instructions.iterator();
            while (iter.hasNext()) {
                AbstractInsnNode node = iter.next();
                if (node.getType() == AbstractInsnNode.LOOKUPSWITCH_INSN) {
                    LookupSwitchInsnNode lookupSwitchInsnNode = (LookupSwitchInsnNode) node;
                    lookupSwitchInsnNode.keys.add(TemplateString.T_TEMPLATE_STRING);
                    LabelNode label = new LabelNode();
                    lookupSwitchInsnNode.labels.add(label);
                    instructions.insert(lookupSwitchInsnNode, label);
                    InsnList insnList = new InsnList();
                    insnList.add(new FrameNode(F_SAME, 0, null, 0, null));
                    insnList.add(new VarInsnNode(ALOAD, 1));
                    insnList.add(new VarInsnNode(ALOAD, 0));
                    insnList.add(new VarInsnNode(ALOAD, 2));
                    insnList.add(new MethodInsnNode(INVOKESTATIC, "youyihj/zenutils/impl/zenscript/TemplateString", "getExpression", "(Lstanhebben/zenscript/ZenTokener;Lstanhebben/zenscript/util/ZenPosition;Lstanhebben/zenscript/compiler/IEnvironmentGlobal;)Lstanhebben/zenscript/parser/expression/ParsedExpression;", false));
                    insnList.add(new InsnNode(ARETURN));
                    instructions.insert(label, insnList);
                    break;
                }
            }
        }
        if (targetClassName.endsWith("TemplateStringTokener")) {
            targetClass.fields.stream()
                              .filter(it -> it.name.equals("startPosition"))
                              .findFirst()
                              .ifPresent(it -> it.access |= ACC_FINAL);
            targetClass.methods.stream().filter(it -> it.name.equals("<init>")).findFirst().ifPresent(it -> {
                InsnList insnList = new InsnList();
                insnList.add(new VarInsnNode(ALOAD, 0));
                insnList.add(new VarInsnNode(ALOAD, 2));
                insnList.add(new FieldInsnNode(PUTFIELD, "youyihj/zenutils/impl/zenscript/TemplateStringTokener", "startPosition", "Lstanhebben/zenscript/util/ZenPosition;"));
                insnList.add(new VarInsnNode(ALOAD, 0));
                insnList.add(new InsnNode(ICONST_1));
                insnList.add(new FieldInsnNode(PUTFIELD, "youyihj/zenutils/impl/zenscript/TemplateStringTokener", "constructing", "Z"));
                it.instructions.insert(insnList);
            });
        }
        if (targetClassName.endsWith("ParsedExpressionFunction")) {
            MethodNode method = targetClass.methods.stream()
                                                   .filter(it -> it.name.equals("compile"))
                                                   .findFirst()
                                                   .orElseThrow(NoSuchMethodError::new);
            InsnList instructions = method.instructions;
            ListIterator<AbstractInsnNode> iter = instructions.iterator();
            while (iter.hasNext()) {
                AbstractInsnNode node = iter.next();
                if (node.getType() == AbstractInsnNode.TYPE_INSN) {
                    TypeInsnNode typeInsnNode = (TypeInsnNode) node;
                    if (typeInsnNode.getOpcode() == CHECKCAST && "stanhebben/zenscript/type/ZenTypeNative".equals(typeInsnNode.desc)) {
                        instructions.insert(node, new InsnNode(ACONST_NULL));
                        instructions.remove(node.getPrevious());
                        instructions.remove(node);
                    }
                }
                if (node.getType() == AbstractInsnNode.METHOD_INSN) {
                    MethodInsnNode methodInsnNode = (MethodInsnNode) node;
                    if ("getNativeClass".equals(methodInsnNode.name)) {
                        InsnList callPredictTypeClass = new InsnList();
                        callPredictTypeClass.add(new VarInsnNode(ALOAD, 2));
                        callPredictTypeClass.add(new MethodInsnNode(INVOKEVIRTUAL, "stanhebben/zenscript/type/ZenType", "toJavaClass", "()Ljava/lang/Class;", false));
                        instructions.insert(node, callPredictTypeClass);
                        instructions.remove(node.getPrevious());
                        instructions.remove(node);
                    }
                }
            }
        }
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
