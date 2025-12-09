package com.caio.analize;

import com.caio.exceptions.NoOneAnnotationMutableFinded;
import com.caio.models.AnnotationMutationPoint;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class BytecodeAnalyzer {

    private static final List<String> TARGETS_DESC = List.of(
            "Lorg/springframework/security/access/prepost/PreAuthorize;",
            "Lorg/springframework/security/access/prepost/PostAuthorize;");

    private List<AnnotationMutationPoint> mutationsPoints;

    private List<AnnotationMutationPoint> mainClasses;

    private List<AnnotationMutationPoint> testClasses;

    private Dependencies dependencies;

    private List<String> classNameTest = new ArrayList<>(); // --- IGNORE ---

    public BytecodeAnalyzer() {
        this.mutationsPoints = new ArrayList<AnnotationMutationPoint>();
        this.mainClasses = new ArrayList<AnnotationMutationPoint>();
        this.testClasses = new ArrayList<AnnotationMutationPoint>();
        this.classNameTest = new ArrayList<String>();
        this.dependencies = new Dependencies();
    }

    public void analyzeClass(List<Path> classFilePath) throws IOException {

        if (classFilePath == null)
            throw new IllegalArgumentException("O classFilePath não deve ser null");

        for (Path path : classFilePath) {
            byte[] bytes = Files.readAllBytes(path);
            ClassReader reader = new ClassReader(bytes);
            ClassNode classNode = new ClassNode();
            reader.accept(classNode, 0);

            if (isController(classNode)) {
                classeAnnotations(classNode, bytes);
                methodAnnotations(classNode, bytes);
            }
            // Ele ainda teria que analizar dentro do config de segurança do springboot, por
            // que é possível de usar da mesma forma essas annotations

            findmainClasses(classNode, bytes);

        }

        if (this.mutationsPoints != null && this.mutationsPoints.isEmpty())
            throw new NoOneAnnotationMutableFinded();

    }

    private boolean isController(ClassNode cn) throws IOException { // Detecta se é um controller, isso apartir das
                                                                    // classes que ela possui, para quando o operador é
                                                                    // para toda a classe, ou seja, todos os endpoints,
                                                                    // quando isso é feito colocamos a annotattion dele
                                                                    // na classe

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

    private void classeAnnotations(ClassNode classNode, byte[] bytes) {
        if (classNode.visibleAnnotations != null) {
            for (AnnotationNode an : classNode.visibleAnnotations) {
                if (an.values != null && TARGETS_DESC.contains(an.desc)) {

                    AnnotationMutationPoint amp = AnnotationMutationPoint.forClass(
                            classNode.name,
                            an.desc,
                            classNode,
                            List.copyOf(an.values),
                            bytes);
                    mutationsPoints.add(amp);
                }

            }
        }
    }

    private void methodAnnotations(ClassNode classNode, byte[] bytes) {
        if (classNode.methods != null) {
            for (MethodNode method : classNode.methods) {
                if (method.visibleAnnotations != null) {
                    for (AnnotationNode an : method.visibleAnnotations) {
                        if (TARGETS_DESC.contains(an.desc)) {

                            AnnotationMutationPoint amp = AnnotationMutationPoint.forMethod(
                                    classNode.name,
                                    an.desc,
                                    classNode,
                                    List.copyOf(an.values),
                                    (byte[]) bytes,
                                    method); // Tá indo nullo por algum motivo
                            mutationsPoints.add(amp);
                        }

                    }
                }
            }
        }

    }

    public void findmainClasses(ClassNode classNode, byte[] bytes) {

        AnnotationMutationPoint amp = AnnotationMutationPoint.forClass(
                classNode.name,
                null,
                classNode,
                List.of(),
                bytes);

        boolean isTestClass = hasTestAnnotations(classNode);

        if (isTestClass) {
            testClasses.add(amp);
            classNameTest.add(classNode.name);
        } else {
            mainClasses.add(amp);

        }

    }

    private boolean hasTestAnnotations(ClassNode classNode) {
        List<AnnotationNode> annotations = collectAllAnnotations(classNode);
        return annotations.stream().anyMatch(this::isTestRelatedAnnotation);
    }

    private List<AnnotationNode> collectAllAnnotations(ClassNode classNode) {
        List<AnnotationNode> all = new ArrayList<>();

        addAnnotations(all, classNode.visibleAnnotations);
        addAnnotations(all, classNode.invisibleAnnotations);

        if (classNode.methods != null) {
            for (MethodNode method : classNode.methods) {
                addAnnotations(all, method.visibleAnnotations);
                addAnnotations(all, method.invisibleAnnotations);
            }
        }

        if (classNode.fields != null) {
            for (FieldNode field : classNode.fields) {
                addAnnotations(all, field.visibleAnnotations);
                addAnnotations(all, field.invisibleAnnotations);
            }
        }

        return all;
    }

    private void addAnnotations(List<AnnotationNode> target, List<AnnotationNode> source) {
        if (source != null)
            target.addAll(source);
    }

    private boolean isTestRelatedAnnotation(AnnotationNode annotation) {
        String desc = annotation.desc;

        if (desc.startsWith("L") && desc.endsWith(";")) {
            desc = desc.substring(1, desc.length() - 1);
        }
        return desc.startsWith("org/junit")
                || desc.startsWith("org/mockito")
                || desc.startsWith("org/springframework/boot/test")
                || desc.startsWith("org/springframework/test")
                || desc.startsWith("org/testng");
    }

    public void transformPathIntoUrl(List<Path> repositoriePath) throws Exception {
        dependencies.extractJars(repositoriePath);
    }

    public List<AnnotationMutationPoint> getMutationsPoints() {
        return mutationsPoints;
    }

    public void setMutationsPoints(List<AnnotationMutationPoint> mutationsPoints) {
        this.mutationsPoints = mutationsPoints;
    }

    public List<AnnotationMutationPoint> getmainClasses() {
        return mainClasses;
    }

    public List<AnnotationMutationPoint> getTestClasses() {
        return testClasses;
    }

    public List<URL> getDependenciesJarURL() {
        return this.dependencies.getJarUrls();
    }

    public List<String> getClassNameTest() {
        return this.classNameTest;
    }
}