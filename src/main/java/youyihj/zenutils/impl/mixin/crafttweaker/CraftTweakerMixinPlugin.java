package youyihj.zenutils.impl.mixin.crafttweaker;

import org.objectweb.asm.tree.*;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import stanhebben.zenscript.ZenTokener;
import youyihj.zenutils.api.util.ReflectionInvoked;
import youyihj.zenutils.impl.zenscript.ExtendZenTokens;

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
        switch (targetClassName) {
            case "stanhebben.zenscript.parser.expression.ParsedExpression":
                transformParsedExpression(targetClass);
                break;
            case "stanhebben.zenscript.parser.expression.ParsedExpressionFunction":
                transformParsedExpressionFunction(targetClass);
                break;
            case "youyihj.zenutils.impl.zenscript.TemplateStringTokener":
                transformTemplateStringTokener(targetClass);
                break;
            case "crafttweaker.mc1120.CraftTweaker":
                transformCraftTweaker(targetClass);
                break;
        }
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    private void transformParsedExpression(ClassNode targetClass) {
        for (MethodNode method : targetClass.methods) {
            InsnList instructions = method.instructions;
            ListIterator<AbstractInsnNode> iter = instructions.iterator();
            switch (method.name) {
                case "readPrimaryExpression":
                    while (iter.hasNext()) {
                        AbstractInsnNode node = iter.next();
                        if (node.getType() == AbstractInsnNode.LOOKUPSWITCH_INSN) {
                            LookupSwitchInsnNode lookupSwitchInsnNode = (LookupSwitchInsnNode) node;
                            lookupSwitchInsnNode.keys.add(ExtendZenTokens.T_TEMPLATE_STRING);
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
                    break;
                case "readPostfixExpression":
                    boolean afterInstanceOfCheck = false;
                    LabelNode lblCheckQuestDot = new LabelNode();
                    LabelNode lblExitLoop = null;
                    LabelNode lblContinueLoop = null;
                    while (iter.hasNext()) {
                        AbstractInsnNode node = iter.next();
                        if (!afterInstanceOfCheck && node instanceof IntInsnNode) {
                            IntInsnNode intInsnNode = (IntInsnNode) node;
                            if (intInsnNode.getOpcode() == SIPUSH && intInsnNode.operand == ZenTokener.T_INSTANCEOF) {
                                afterInstanceOfCheck = true;
                                continue;
                            }
                        }
                        if (afterInstanceOfCheck) {
                            if (node instanceof LabelNode) {
                                lblContinueLoop = ((LabelNode) node);
                            }
                            if (node instanceof JumpInsnNode) {
                                JumpInsnNode jumpInsnNode = (JumpInsnNode) node;
                                if (jumpInsnNode.getOpcode() == IFNULL) {
                                    lblExitLoop = jumpInsnNode.label;
                                    jumpInsnNode.label = lblCheckQuestDot;
                                } else if (jumpInsnNode.getOpcode() == GOTO) {
                                    break;
                                }
                            }
                        }
                    }
                    if (lblExitLoop == null || lblContinueLoop == null) {
                        throw new RuntimeException();
                    }
                    InsnList continueAfterInstanceOfHandle = new InsnList();
                    continueAfterInstanceOfHandle.add(new JumpInsnNode(GOTO, lblContinueLoop));
                    InsnList handleQuestDot = new InsnList();
                    handleQuestDot.add(lblCheckQuestDot);
                    handleQuestDot.add(new FrameNode(F_SAME, 0, null, 0, null));
                    handleQuestDot.add(new VarInsnNode(ALOAD, 1));
                    handleQuestDot.add(new IntInsnNode(SIPUSH, ExtendZenTokens.T_QUEST_DOT));
                    handleQuestDot.add(new MethodInsnNode(INVOKEVIRTUAL, "stanhebben/zenscript/ZenTokener", "optional", "(I)Lstanhebben/zenscript/parser/Token;", false));
                    handleQuestDot.add(new JumpInsnNode(IFNULL, lblExitLoop));
                    handleQuestDot.add(new VarInsnNode(ALOAD, 0));
                    handleQuestDot.add(new VarInsnNode(ALOAD, 1));
                    handleQuestDot.add(new VarInsnNode(ALOAD, 2));
                    handleQuestDot.add(new VarInsnNode(ALOAD, 3));
                    handleQuestDot.add(new MethodInsnNode(INVOKESTATIC, "stanhebben/zenscript/parser/expression/ParsedExpression", "handleOptionalChaining","(Lstanhebben/zenscript/util/ZenPosition;Lstanhebben/zenscript/ZenTokener;Lstanhebben/zenscript/compiler/IEnvironmentGlobal;Lstanhebben/zenscript/parser/expression/ParsedExpression;)Lstanhebben/zenscript/parser/expression/ParsedExpression;", false));
                    handleQuestDot.add(new VarInsnNode(ASTORE, 3));
                    instructions.insertBefore(lblContinueLoop, continueAfterInstanceOfHandle);
                    instructions.insertBefore(lblContinueLoop, handleQuestDot);
                    break;
            }
        }
    }

    private void transformTemplateStringTokener(ClassNode targetClass) {
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

    private void transformParsedExpressionFunction(ClassNode targetClass) {
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

    private void transformCraftTweaker(ClassNode targetClass) {
        MethodNode method = targetClass.methods.stream()
                .filter(it -> it.name.equals("onConstruction"))
                .findFirst()
                .orElseThrow(NoSuchMethodError::new);
        InsnList instructions = method.instructions;
        ListIterator<AbstractInsnNode> iter = instructions.iterator();
        while (iter.hasNext()) {
            AbstractInsnNode node = iter.next();
            if (node.getType() == AbstractInsnNode.FIELD_INSN) {
                FieldInsnNode fieldInsnNode = (FieldInsnNode) node;
                if (fieldInsnNode.getOpcode() == GETSTATIC && fieldInsnNode.name.equals("logger")) {
                    iter.remove();
                    for (int i = 0; i < 8; i++) {
                        iter.next();
                        iter.remove();
                    }
                    break;
                }
            }
        }
    }
}
