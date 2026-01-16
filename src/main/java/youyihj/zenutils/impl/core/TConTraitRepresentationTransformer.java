package youyihj.zenutils.impl.core;

import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.*;
import youyihj.zenutils.api.util.ReflectionInvoked;

/**
 * @author youyihj
 */
@ReflectionInvoked
public class TConTraitRepresentationTransformer implements IClassTransformer, Opcodes {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if ("com.teamacronymcoders.contenttweaker.modules.tinkers.traits.TConTraitRepresentation".equals(transformedName)) {
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
            ClassVisitor visitor = new ASMTConTraitRepresentation(ASM5, cw);
            new ClassReader(basicClass).accept(visitor, 0);
            return cw.toByteArray();
        }
        if ("c4.conarm.integrations.contenttweaker.traits.ConArmTraitRepresentation".equals(transformedName)) {
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
            ClassVisitor visitor = new ASMConArmTraitRepresentation(ASM5, cw);
            new ClassReader(basicClass).accept(visitor, 0);
            return cw.toByteArray();
        }

        return basicClass;
    }

    private static class ASMTConTraitRepresentation extends ClassVisitor {
        public ASMTConTraitRepresentation(int api, ClassVisitor cv) {
            super(api, cv);
        }

        private final String[][] fields = {
                {"afterBlockBreak", "AfterBlockBreak"},
                {"beforeBlockBreak", "BeforeBlockBreak"},
                {"onBlockHarvestDrops", "BlockHarvestDrops"},
                {"calcDamage", "Damage"},
                {"calcCrit", "IsCriticalHit"},
                {"getMiningSpeed", "MiningSpeed"},
                {"onHit", "OnHit"},
                {"onUpdate", "OnUpdate"},
                {"afterHit", "AfterHit"},
                {"calcKnockBack", "KnockBack"},
                {"onBlock", "OnBlock"},
                {"onToolDamage", "OnToolDamage"},
                {"calcToolHeal", "OnToolHeal"},
                {"onToolRepair", "OnToolRepair"},
                {"onPlayerHurt", "OnPlayerHurt"},
                {"canApplyTogetherTrait", "CanApplyTogetherTrait"},
                {"canApplyTogetherEnchantment", "CanApplyTogetherEnchantment"},
                {"extraInfo", "ExtraInfo"}
        };

        @Override
        public void visitEnd() {
            for (String[] data : fields) {
                String name = data[0];
                String typeDesc = "Lcom/teamacronymcoders/contenttweaker/modules/tinkers/utils/Functions$" + data[1] + ";";
                MethodVisitor mv = this.visitMethod(ACC_PUBLIC, "set" + StringUtils.capitalize(name), "(" + typeDesc + ")V", null, null);
                AnnotationVisitor av = mv.visitAnnotation("Lstanhebben/zenscript/annotations/ZenSetter;", true);
                av.visit("value", name);
                av.visitEnd();
                mv.visitCode();
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKEVIRTUAL, "com/teamacronymcoders/contenttweaker/modules/tinkers/traits/TConTraitRepresentation", "getTrait", "()Lslimeknights/tconstruct/library/traits/ITrait;", false);
                mv.visitTypeInsn(CHECKCAST, "com/teamacronymcoders/contenttweaker/modules/tinkers/traits/CoTTrait");
                mv.visitVarInsn(ALOAD, 1);
                mv.visitFieldInsn(PUTFIELD, "com/teamacronymcoders/contenttweaker/modules/tinkers/traits/CoTTrait", name, typeDesc);
                mv.visitInsn(RETURN);
                mv.visitEnd();
            }

            super.visitEnd();
        }
    }

    private static class ASMConArmTraitRepresentation extends ClassVisitor {
        public ASMConArmTraitRepresentation(int api, ClassVisitor cv) {
            super(api, cv);
        }

        private final String[][] fields = {
                {"onUpdate", "OnUpdate"},
                {"onArmorRepair", "OnArmorRepair"},
                {"canApplyTogetherTrait", "CanApplyTogetherTrait"},
                {"canApplyTogetherEnchantment", "CanApplyTogetherEnchantment"},
                {"extraInfo", "ExtraInfo"},
                {"onArmorTick", "OnArmorTick"},
                {"getModifications", "GetModifications"},
                {"onItemPickup", "OnItemPickup"},
                {"onHeal", "OnHeal"},
                {"onHurt", "OnHurt"},
                {"onDamaged", "OnDamaged"},
                {"onFalling", "OnFalling"},
                {"onJumping", "OnJumping"},
                {"onKnockback", "OnKnockback"},
                {"onArmorDamaged", "OnArmorDamaged"},
                {"onArmorHealed", "OnArmorHealed"},
                {"onArmorEquip", "OnArmorEquip"},
                {"onArmorRemove", "OnArmorRemove"},
                {"onAbility", "OnAbility"},
                {"getAbilityLevel", "GetAbilityLevel"}
        };

        @Override
        public void visitEnd() {
            for (String[] data : fields) {
                String name = data[0];
                String typeDesc = "Lc4/conarm/integrations/contenttweaker/utils/ArmorFunctions$" + data[1] + ";";
                MethodVisitor mv = this.visitMethod(ACC_PUBLIC, "set" + StringUtils.capitalize(name), "(" + typeDesc + ")V", null, null);
                AnnotationVisitor av = mv.visitAnnotation("Lstanhebben/zenscript/annotations/ZenSetter;", true);
                av.visit("value", name);
                av.visitEnd();
                mv.visitCode();
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKEVIRTUAL, "c4/conarm/integrations/contenttweaker/traits/ConArmTraitRepresentation", "getTrait", "()Lslimeknights/tconstruct/library/traits/ITrait;", false);
                mv.visitTypeInsn(CHECKCAST, "c4/conarm/integrations/contenttweaker/traits/CoTArmorTrait");
                mv.visitVarInsn(ALOAD, 1);
                mv.visitFieldInsn(PUTFIELD, "c4/conarm/integrations/contenttweaker/traits/CoTArmorTrait", name, typeDesc);
                mv.visitInsn(RETURN);
                mv.visitEnd();
            }

            super.visitEnd();
        }
    }
}
