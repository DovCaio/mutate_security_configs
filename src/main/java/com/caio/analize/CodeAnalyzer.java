package com.caio.analize;

import com.caio.exceptions.NoOneAnnotationMutableFinded;
import com.caio.models.AnnotationMutationPoint;
import com.caio.models.AnnotationMutationPoint.AnnotationType;

import java.io.IOException;
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

    public record AuthorizationOccurrence(String value, Integer lineNumber) {

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

    private String extractPackageName(String content) {

        Pattern pattern = Pattern.compile("package\\s+([a-zA-Z_][\\.\\w]*);");

        Matcher matcher = pattern.matcher(content);

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return "";
        }
    }

    private String extractClassName(String content) {

        Pattern pattern = Pattern.compile("public\\s+class\\s+([a-zA-Z_][\\w]*)");

        Matcher matcher = pattern.matcher(content);

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return "";
        }
    }

    private String extractMethod(String content, Integer lineNumber) {

        String[] lines = content.split("\n");

        if (lineNumber < 1 || lineNumber > lines.length) {
            return "";
        }

        String result = "";

        Pattern pattern = Pattern.compile(
                "\\s*(public|private|protected)\\s+([\\w<>\\[\\]?]+)\\s+([a-zA-Z_][\\w]*)\\s*\\(.*\\)");

        for (int i = lineNumber - 1; i < lines.length; i++) {
            String line = lines[i];

            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                result = matcher.group(3);
                break;
            } else if (line.contains("class ")) { // O espaço é muito importante
                return "";
            }
        }

        return result;
    }

    private List<AuthorizationOccurrence> findOriginalsValues(String content) { // Uma regex resolve isso daqui fácil

        Pattern pattern = Pattern.compile(
                "@(?:PostAuthorize|PreAuthorize)\\s*\\(\\s*\"([^\"]*)\"\\s*\\)");

        Matcher matcher = pattern.matcher(content);
        List<AuthorizationOccurrence> results = new ArrayList<>();
        while (matcher.find()) {
            Integer line = 1;
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

    private void analizeMutabelsControllers() {
        if (controllers == null || controllers.isEmpty())
            throw new NoOneAnnotationMutableFinded();

        Map<Path, String> aux = cloneControllersMap();

        for (Map.Entry<Path, String> entry : controllers.entrySet()) {
            processControllerEntry(entry, aux);
        }

        if (aux == null || aux.isEmpty())
            throw new NoOneAnnotationMutableFinded();

        this.controllers = aux;
    }

    private Map<Path, String> cloneControllersMap() {
        return this.getControllers().entrySet().stream()
                .collect(HashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), HashMap::putAll);
    }

    private void processControllerEntry(Map.Entry<Path, String> entry, Map<Path, String> aux) {
        Path path = entry.getKey();
        String content = entry.getValue();

        boolean containsPre = content.contains("@PreAuthorize");
        boolean containsPost = content.contains("@PostAuthorize");

        if (!(containsPre || containsPost)) {
            aux.remove(path);
        } else {
            List<AuthorizationOccurrence> originalValues = findOriginalsValues(content);
            for (int i = 0; i < originalValues.size(); i++) {
                addMutationPointFromOccurrence(content, path, originalValues.get(i), containsPre, containsPost);
            }
        }
    }

    private void addMutationPointFromOccurrence(String content, Path path, AuthorizationOccurrence occurrence, boolean containsPre, boolean containsPost) {
        String originalValue = occurrence.value;
        String mutatedValue = "";
        String packageName = extractPackageName(content);
        String className = extractClassName(content);
        String methodName = extractMethod(content, occurrence.lineNumber);
        AnnotationMutationPoint.TargetType targetType;
        if (methodName.equals("")) {
            targetType = AnnotationMutationPoint.TargetType.CLASS;
        } else {
            targetType = AnnotationMutationPoint.TargetType.METHOD;
        }
        AnnotationMutationPoint annotationMutationPoint = new AnnotationMutationPoint(
                packageName,
                className,
                methodName,
                originalValue,
                mutatedValue,
                targetType,
                path,
                occurrence.lineNumber);
        if (containsPre) {
            AnnotationType annotationType = AnnotationType.PRE;
            annotationMutationPoint.setAnnotationName(annotationType);
        } else {
            AnnotationType annotationType = AnnotationType.POST;
            annotationMutationPoint.setAnnotationName(annotationType);
        }
        this.mutationsPoints.add(annotationMutationPoint);
    }

    public List<String> getRoles() {

        List<String> roles = new ArrayList<>();

        Pattern outer = Pattern.compile("(hasRole|hasAnyRole)\\s*\\(([^)]*)\\)");
        Pattern inner = Pattern.compile("['\"]([^'\"]+)['\"]");

        
        for (AnnotationMutationPoint amp : this.mutationsPoints) {
            String originalValue = amp.getOriginalValue();

            Matcher outerMatcher = outer.matcher(originalValue);

            while (outerMatcher.find()) {

                String params = outerMatcher.group(2);

                Matcher innerMatcher = inner.matcher(params);

                while (innerMatcher.find()) {

                    String role = innerMatcher.group(1);


                    if (!roles.contains(role)) {
                        roles.add(role);
                    }
                }
            }
        }

        return roles;
    }

    public List<String> getAuthorities() {

        List<String> authorities = new ArrayList<>();

        Pattern outer = Pattern.compile("(hasAuthority|hasAnyAuthority)\\s*\\(([^)]*)\\)");
        Pattern inner = Pattern.compile("['\"]([^'\"]+)['\"]");

        for (AnnotationMutationPoint amp : this.mutationsPoints) {

            String originalValue = amp.getOriginalValue();

            Matcher outerMatcher = outer.matcher(originalValue);

            while (outerMatcher.find()) {

                String params = outerMatcher.group(2);

                Matcher innerMatcher = inner.matcher(params);

                while (innerMatcher.find()) {

                    String authority = innerMatcher.group(1);


                    if (!authorities.contains(authority)) {
                        authorities.add(authority);
                    }
                }
            }
        }

        return authorities;
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