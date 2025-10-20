package com.caio.utli;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

public class ClassNodeCloner {

    public static ClassNode cloneClassNode(ClassNode original) throws Exception {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        original.accept(cw);
        byte[] classBytes = cw.toByteArray();

        ClassReader cr = new ClassReader(classBytes);
        ClassNode cloned = new ClassNode();
        cr.accept(cloned, 0);

        return cloned;
    }

}
