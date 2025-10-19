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

    private static final List<String> TARGETS_DESC = List.of(
            "Lorg/springframework/security/access/prepost/PreAuthorize;",
            "Lorg/springframework/security/access/prepost/PostAuthorize;" // talvez esse diretório esteja errado
    );

    private List<Path> classFilePath;

    public BytecodeAnalyzer(List<Path> classFilePath) {
        if (classFilePath == null) throw  new IllegalArgumentException("O classsFilePathd não pode ser null.");
        this.classFilePath = classFilePath;
    }

    public void analyzeClass() throws IOException {
        for (Path path :  classFilePath) {
            byte[] bytes = Files.readAllBytes(path);
            ClassReader reader = new ClassReader(bytes);
            ClassNode classNode = new ClassNode();
            reader.accept(classNode, 0);

            //desc(classNode);

            if (isController(classNode)) {
                classeAnnotations(classNode);
                methodAnnotations(classNode);
                //Ele ainda teria que analizar dentro do config de segurança do springboot, por que é possível de usar da mesma forma essas annotations
            }
        }



    }

    private boolean isController(ClassNode cn) throws IOException { //Detecta se é um controller, isso apartir das classes que ela possui, para quando o operador é para toda a classe, ou seja, todos os endpoints, quando isso é feito colocamos a annotattion dele na classe

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

    private void classeAnnotations(ClassNode classNode){
        if (classNode.visibleAnnotations != null) {
            for (AnnotationNode an : classNode.visibleAnnotations) {
                if (an.values != null && TARGETS_DESC.contains(an.desc)) {
                    System.out.println("    " + an.desc);
                    for (int i = 0; i < an.values.size(); i += 2) {
                        String key = (String) an.values.get(i);
                        Object val = an.values.get(i + 1);
                        System.out.println("      " + key + " = " + val);
                    }
                }
            }
        }
    }

    private void methodAnnotations(ClassNode classNode) {
        if (classNode.methods != null ) {
            for (MethodNode method : classNode.methods) {
                if (method.visibleAnnotations != null ) {
                    for (AnnotationNode an : method.visibleAnnotations) {
                        if (TARGETS_DESC.contains(an.desc)) {
                            System.out.println("  Método: " + method.name);
                            System.out.println("    Annotation" + an.desc);
                        }

                    }
                }
            }
        }

    }

    private   void desc(ClassNode classNode) { //Apenas para depurar
        System.out.println("Classe: " + classNode.name);
        for (MethodNode method : classNode.methods) {
            System.out.println("  Método: " + method.name + method.desc);
            System.out.println("    Número de instruções: " + method.instructions.size());
        }
    }
}
