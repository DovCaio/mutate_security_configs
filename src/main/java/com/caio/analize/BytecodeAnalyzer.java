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
import java.util.Map;
import java.util.HashMap;

public class BytecodeAnalyzer {


    private List<AnnotationMutationPoint> mutationsPoints;

    private List<AnnotationMutationPoint> mainClasses;

    private Map<Path, String> controllers;

    public BytecodeAnalyzer() {
        this.mutationsPoints = new ArrayList<AnnotationMutationPoint>();
        this.mainClasses = new ArrayList<AnnotationMutationPoint>();
        this.controllers = new HashMap<Path, String>();
    }

    public void analyze(List<Path> classFilePath) throws IOException {
        analyzeClass(classFilePath);
        analizeMutabelsControllers();
    }

    private void analyzeClass(List<Path> classFilePath) throws IOException {

        if (classFilePath == null)
            throw new IllegalArgumentException("O classFilePath não deve ser null");

        for (Path path : classFilePath) {
            String content = Files.readString(path);

            if (isController(content)) {
                controllers.put(path, content);
            }

        }
    }

    private boolean isController(String content) throws IOException { 
        if (content.contains("@RestController") || content.contains("@Controller"))
            return true;
        return false;
    }

    private void analizeMutabelsControllers(){

        if (controllers == null || controllers.isEmpty())
            throw new NoOneAnnotationMutableFinded();


        for (Map.Entry<Path, String> entry : controllers.entrySet()) {
            Path path = entry.getKey();
            String content = entry.getValue();

            if (content.contains("@PreAuthorize") || content.contains("@PostAuthorize")) {
                System.out.println("Controller mutável encontrado: " + path.toString());
            }

        }
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

}