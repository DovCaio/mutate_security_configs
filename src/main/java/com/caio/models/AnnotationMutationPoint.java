package com.caio.models;


import java.nio.file.Path;

public class AnnotationMutationPoint {


    public enum TargetType { CLASS, METHOD }
    public enum AnnotationType { PRE, POST }

    public AnnotationType annotationName;
    public String originalValue;
    public String mutatedValue;
    public TargetType targetType;
    public Path filePath;
    public Long lineNumber;

    public AnnotationMutationPoint(
        String originalValue,
        String mutatedValue,
        TargetType targetType,
        Path filePath,
        Long lineNumber
            ) {

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

    public Long getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(Long lineNumber) {
        this.lineNumber = lineNumber;
    }

    

}

