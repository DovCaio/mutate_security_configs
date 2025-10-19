package com.caio.models;

import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.FieldNode;

import java.util.List;

public class AnnotationMutationPoint {
    public enum TargetType { CLASS, METHOD }

    private TargetType targetType;   // Onde está a annotation
    private String ownerClass;       // Nome da classe (qualificado)
    private String annotationDesc;   // Descriptor ASM
    private Object targetElement;    // ClassNode / MethodNode / FieldNode
    private List<Object> values;     // Lista de pares key/value (ASM)

    public AnnotationMutationPoint(TargetType targetType, String ownerClass,
                                   String annotationDesc, Object targetElement,
                                   List<Object> values) {
        this.targetType = targetType;
        this.ownerClass = ownerClass;
        this.annotationDesc = annotationDesc;
        this.targetElement = targetElement;
        this.values = values;
    }

    public TargetType getTargetType() {
        return targetType;
    }

    public void setTargetType(TargetType targetType) {
        this.targetType = targetType;
    }

    public String getOwnerClass() {
        return ownerClass;
    }

    public void setOwnerClass(String ownerClass) {
        this.ownerClass = ownerClass;
    }

    public String getAnnotationDesc() {
        return annotationDesc;
    }

    public void setAnnotationDesc(String annotationDesc) {
        this.annotationDesc = annotationDesc;
    }

    public Object getTargetElement() {
        return targetElement;
    }

    public void setTargetElement(Object targetElement) {
        this.targetElement = targetElement;
    }

    public List<Object> getValues() {
        return values;
    }

    public void setValues(List<Object> values) {
        this.values = values;
    }
}

