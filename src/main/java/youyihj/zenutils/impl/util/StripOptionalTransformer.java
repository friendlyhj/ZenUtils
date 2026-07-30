package youyihj.zenutils.impl.util;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.Optional;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureWriter;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.util.Annotations;

import java.util.ArrayList;
import java.util.List;

/**
 * @author youyihj
 */
public class StripOptionalTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);

        List<AnnotationNode> stripInterfaces = new ArrayList<>();

        AnnotationNode ifaceAnnotation = Annotations.getVisible(classNode, Optional.Interface.class);
        if (ifaceAnnotation != null) {
            stripInterfaces.add(ifaceAnnotation);
        }

        AnnotationNode ifacesAnnotation = Annotations.getVisible(classNode, Optional.InterfaceList.class);
        if (ifacesAnnotation != null) {
            //noinspection unchecked
            stripInterfaces.addAll((List<AnnotationNode>) ifacesAnnotation.values.get(1));
        }

        for (AnnotationNode annotationNode : stripInterfaces) {
            stripInterface(classNode, annotationNode);
        }

        stripMethods(classNode);

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    private void stripInterface(ClassNode classNode, AnnotationNode annotation) {
        String iface = Annotations.getValue(annotation, "iface");
        String modid = Annotations.getValue(annotation, "modid");
        boolean striprefs = Annotations.getValue(annotation, "striprefs", Boolean.FALSE);

        if (InternalUtils.isModLoaded(modid)) {
            return;
        }

        String ifaceName = iface.replace('.', '/');
        boolean found = classNode.interfaces.remove(ifaceName);
        if (found && classNode.signature != null) {
            SignatureReader sr = new SignatureReader(classNode.signature);
            final RemovingSignatureWriter signatureWriter = new RemovingSignatureWriter(ifaceName);
            sr.accept(signatureWriter);
            classNode.signature = signatureWriter.toString();
        }
        if (found && striprefs) {
            classNode.methods.removeIf(node -> node.desc.contains(ifaceName));
        }
    }

    private void stripMethods(ClassNode classNode) {
        classNode.methods.removeIf(methodNode -> {
            AnnotationNode optionalMethodAnnotation = Annotations.getVisible(methodNode, Optional.Method.class);
            if (optionalMethodAnnotation != null) {
                String modid = Annotations.getValue(optionalMethodAnnotation, "modid");
                return !InternalUtils.isModLoaded(modid);
            }
            return false;
        });
    }

    private static class RemovingSignatureWriter extends SignatureWriter {
        private final String itfName;

        RemovingSignatureWriter(String itfName) {
            this.itfName = itfName;
        }

        @Override
        public void visitClassType(String name) {
            if (name.equals(itfName)) {
                super.visitClassType("java/lang/Object");
            } else {
                super.visitClassType(name);
            }
        }
    }
}
