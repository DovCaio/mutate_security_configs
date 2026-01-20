package com.caio.analize;

import com.caio.exceptions.NoOneAnnotationMutableFinded;
import com.caio.models.AnnotationMutationPoint;
import com.caio.models.AnnotationMutationPoint.AnnotationType;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashMap;

public class CodeAnalyzer {

    private List<AnnotationMutationPoint> mutationsPoints;

    private List<AnnotationMutationPoint> mainClasses;

    private Map<Path, String> controllers;

    public record AuthorizationOccurrence(String value, Long lineNumber) {

    }

    public CodeAnalyzer() {
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

    private List<AuthorizationOccurrence> findOriginalsValues(String content) { // Uma regex resolve isso daqui fácil

        Pattern pattern = Pattern.compile(
                "@(?:PostAuthorize|PreAuthorize)\\s*\\(\\s*\"([^\"]*)\"\\s*\\)");

        Matcher matcher = pattern.matcher(content);
        List<AuthorizationOccurrence> results = new ArrayList<>();
        while (matcher.find()) {
            Long line = 1L;
            for (int i = 0; i < matcher.start(); i++) {
                if (content.charAt(i) == '\n') {
                    line++;
                }
            }
            AuthorizationOccurrence occurrence = new AuthorizationOccurrence(matcher.group(1), line);
            results.add(occurrence);
        }
        return results;

    }

    private void analizeMutabelsControllers() { // Isso daqui vai se tornar um long method, mas por enquanto tá ok

        if (controllers == null || controllers.isEmpty())
            throw new NoOneAnnotationMutableFinded(); // Talvez esse erro devesse ser diferente

        for (Map.Entry<Path, String> entry : controllers.entrySet()) {
            Path path = entry.getKey();
            String content = entry.getValue();

            boolean containsPre = content.contains("@PreAuthorize");
            boolean containsPost = content.contains("@PostAuthorize");

            if (!(containsPre || containsPost)) { // Deixa somente os que podem ser possíveis de mutar
                this.controllers.remove(path);
            } else {
                List<AuthorizationOccurrence> originalValues = findOriginalsValues(content);

                for (int i = 0; i < originalValues.size(); i++) {
                    String originalValue = originalValues.get(i).value;
                    String mutatedValue = ""; // Isso daqui é definido em outro lugar
                    AnnotationMutationPoint annotationMutationPoint = new AnnotationMutationPoint(
                            originalValue,
                            mutatedValue,
                            AnnotationMutationPoint.TargetType.METHOD, //Isso daqui não está certo, tem que ser verificado se de fato é de nível método ou classe
                            path,
                            originalValues.get(i).lineNumber);

                    if (containsPre) {
                        AnnotationType annotationType = AnnotationType.PRE;
                        annotationMutationPoint.setAnnotationName(annotationType);
                    } else {
                        AnnotationType annotationType = AnnotationType.POST;
                        annotationMutationPoint.setAnnotationName(annotationType);
                    }
                    this.mutationsPoints.add(annotationMutationPoint);
                }
            }

        }

        if (controllers == null || controllers.isEmpty())
            throw new NoOneAnnotationMutableFinded();
    }

    public Map<Path, String> getControllers() {
        return controllers;
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