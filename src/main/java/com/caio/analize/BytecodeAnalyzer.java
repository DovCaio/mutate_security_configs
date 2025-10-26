package com.caio.analize;

import com.caio.exceptions.NoOneAnnotationMutableFinded;
import com.caio.models.AnnotationMutationPoint;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public class BytecodeAnalyzer {

    private static final List<String> TARGETS_DESC = List.of(
            "Lorg/springframework/security/access/prepost/PreAuthorize;",
            "Lorg/springframework/security/access/prepost/PostAuthorize;" // talvez esse diretório esteja errado
    );

    private List<AnnotationMutationPoint> mutationsPoints;

    private List<AnnotationMutationPoint> allClasses;


    public BytecodeAnalyzer() {
        this.mutationsPoints = new ArrayList<AnnotationMutationPoint>();
        this.allClasses = new ArrayList<AnnotationMutationPoint>();
    }

    public void analyzeClass(List<Path> classFilePath) throws IOException {

        if (classFilePath == null) throw new IllegalArgumentException("O classFilePath não deve ser null");

        for (Path path :  classFilePath) {
            byte[] bytes = Files.readAllBytes(path);
            ClassReader reader = new ClassReader(bytes);
            ClassNode classNode = new ClassNode();
            reader.accept(classNode, 0);


            if (isController(classNode)) {
                classeAnnotations(classNode, bytes);
                methodAnnotations(classNode, bytes);
            }
            //Ele ainda teria que analizar dentro do config de segurança do springboot, por que é possível de usar da mesma forma essas annotations
        }
        if (this.mutationsPoints != null && this.mutationsPoints.isEmpty()) throw new NoOneAnnotationMutableFinded();
        
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


    private void classeAnnotations(ClassNode classNode, byte[] bytes){
        if (classNode.visibleAnnotations != null) {
            for (AnnotationNode an : classNode.visibleAnnotations) {
                if (an.values != null ){

                    AnnotationMutationPoint amp = new AnnotationMutationPoint(
                                AnnotationMutationPoint.TargetType.CLASS,
                                classNode.name,
                                an.desc,
                                classNode,
                                List.copyOf(an.values),
                                bytes
                    );
                    allClasses.add(amp);
                    if ( TARGETS_DESC.contains(an.desc)) {
                        
                        mutationsPoints.add(amp);
                    }
                }

            }
        }
    }

    private void methodAnnotations(ClassNode classNode, byte[] bytes) {
        if (classNode.methods != null ) {
            for (MethodNode method : classNode.methods) {
                if (method.visibleAnnotations != null ) {
                    for (AnnotationNode an : method.visibleAnnotations) {
                        if (TARGETS_DESC.contains(an.desc)) {

                            AnnotationMutationPoint amp = new AnnotationMutationPoint(
                                    AnnotationMutationPoint.TargetType.METHOD,
                                    classNode.name,
                                    an.desc,
                                    classNode,
                                    List.copyOf(an.values),
                                    bytes,
                                    method
                            );
                            mutationsPoints.add(amp);

                        }

                    }
                }
            }
        }

    }

    public List<AnnotationMutationPoint> getMutationsPoints() {
        return mutationsPoints;
    }

    public void setMutationsPoints(List<AnnotationMutationPoint> mutationsPoints) {
        this.mutationsPoints = mutationsPoints;
    }

    public List<AnnotationMutationPoint> getAllClasses() {
        return allClasses;
    }

    public void setAllClasses(List<AnnotationMutationPoint> allClasses) {
        this.allClasses = allClasses;
    }

    

}
