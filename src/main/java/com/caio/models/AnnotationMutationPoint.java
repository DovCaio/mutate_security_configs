package com.caio.models;


import java.nio.file.Path;

public class AnnotationMutationPoint {


    public enum TargetType { CLASS, METHOD }
    public enum AnnotationType { PRE, POST }

    public String packageName;
    public String className;
    public String methodName;
    public AnnotationType annotationName;
    public String originalValue;
    public String mutatedValue;
    public TargetType targetType;
    public Path filePath;
    public Integer lineNumber;
    

    public AnnotationMutationPoint(
        String packageName,
        String classname,
        String originalValue,
        String mutatedValue,
        TargetType targetType,
        Path filePath,
        Integer lineNumber
            ) {
        this.packageName = packageName;
        this.originalValue = originalValue;
        this.mutatedValue = mutatedValue;
        this.targetType = targetType;
        this.filePath = filePath;
        this.lineNumber = lineNumber;
    }

    public AnnotationType getAnnotationName() {
        return annotationName;
    }

    public void setAnnotationName(AnnotationType annotationName) {
        this.annotationName = annotationName;
    }

    public String getOriginalValue() {
        return originalValue;
    }

    public void setOriginalValue(String originalValue) {
        this.originalValue = originalValue;
    }

    public String getMutatedValue() {
        return mutatedValue;
    }

    public void setMutatedValue(String mutatedValue) {
        this.mutatedValue = mutatedValue;
    }

    public TargetType getTargetType() {
        return targetType;
    }

    public void setTargetType(TargetType targetType) {
        this.targetType = targetType;
    }

    public Path getFilePath() {
        return filePath;
    }

    public void setFilePath(Path filePath) {
        this.filePath = filePath;
    }

    public Integer getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getClassName() {
        return className;
    }

            

}

