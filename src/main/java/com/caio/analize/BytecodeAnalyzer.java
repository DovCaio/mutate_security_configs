package com.caio.analize;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class BytecodeAnalyzer {


    private static final String[] TARGETS_DESC = {"Lorg/springframework/security/access/prepost/PreAuthorize;", "Lorg/springframework/security/access/prepost/PostAuthorize;"};

    public static void analyzeClass(Path classFilePath) throws IOException {
        byte[] bytes = Files.readAllBytes(classFilePath);
        ClassReader reader = new ClassReader(bytes);
        ClassNode classNode = new ClassNode();
        reader.accept(classNode, 0);

        //desc(classNode);

        if (isController(classNode)) {
            classeAnnotations(classNode);
            methodAnnotations(classNode);
        }

    }

    private static boolean isController(ClassNode cn) throws IOException { //Detecta se é um controller, isso apartir das classes que ela possui, para quando o operador é para toda a classe, ou seja, todos os endpoints, quando isso é feito colocamos a annotattion dele na classe

        List<AnnotationNode> annotations = cn.visibleAnnotations;
        if (annotations != null) {
            for (AnnotationNode an : annotations) {
                String desc = an.desc; // ex: "Lorg/springframework/stereotype/Controller;"
                if (desc.equals("Lorg/springframework/stereotype/Controller;")
                        || desc.equals("Lorg/springframework/web/bind/annotation/RestController;")) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void classeAnnotations(ClassNode classNode){
        if (classNode.visibleAnnotations != null) {
            for (AnnotationNode an : classNode.visibleAnnotations) {
                System.out.println("    " + an.desc);
                if (an.values != null) {
                    for (int i = 0; i < an.values.size(); i += 2) {
                        String key = (String) an.values.get(i);
                        Object val = an.values.get(i + 1);
                        System.out.println("      " + key + " = " + val);
                    }
                }
            }
        }
    }

    private static void methodAnnotations(ClassNode classNode) {
        if (classNode.methods != null ) {
            for (MethodNode method : classNode.methods) {
                if (method.visibleAnnotations != null) {
                    System.out.println("  Método: " + method.name);
                    for (AnnotationNode an : method.visibleAnnotations) {
                        System.out.println("    " + an.desc);
                    }
                }
            }
        }

    }

    private static  void desc(ClassNode classNode) { //Apenas para depurar
        System.out.println("Classe: " + classNode.name);
        for (MethodNode method : classNode.methods) {
            System.out.println("  Método: " + method.name + method.desc);
            System.out.println("    Número de instruções: " + method.instructions.size());
        }
    }
}
