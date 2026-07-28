package youyihj.zenutils.impl.mixin.crafttweaker;

import org.objectweb.asm.tree.*;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import youyihj.zenutils.api.util.ReflectionInvoked;

import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import static org.objectweb.asm.Opcodes.GETSTATIC;

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
        if (targetClassName.equals("crafttweaker.mc1120.CraftTweaker")) {
            transformCraftTweaker(targetClass);
        }
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

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
