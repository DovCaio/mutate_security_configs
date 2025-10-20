package com.caio.models;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.FieldNode;

import java.nio.file.Path;
import java.util.List;

public class AnnotationMutationPoint {


    public enum TargetType { CLASS, METHOD }

    private TargetType targetType;   // Onde está a annotation
    private String ownerClass;       // Nome da classe (qualificado)
    private String annotationDesc;   // Descriptor ASM
    private ClassNode targetElement;    // ClassNode / MethodNode
    private List<Object> values;     // Lista de pares key/value (ASM)
    private byte[] bytes;
    private MethodNode method;

    public AnnotationMutationPoint(TargetType targetType, String ownerClass,
                                   String annotationDesc, ClassNode targetElement,
                                   List<Object> values, byte[] bytes,
                                   MethodNode method) {
        this.targetType = targetType;
        this.ownerClass = ownerClass;
        this.annotationDesc = annotationDesc;
        this.targetElement = targetElement;
        this.values = values;
        this.bytes = bytes;
        this.method = method;
    }

    public AnnotationMutationPoint(TargetType targetType, String ownerClass,
                                   String annotationDesc, ClassNode targetElement,
                                   List<Object> values, byte[] bytes) {
        this.targetType = targetType;
        this.ownerClass = ownerClass;
        this.annotationDesc = annotationDesc;
        this.targetElement = targetElement;
        this.values = values;
        this.bytes = bytes;
    }

    public AnnotationMutationPoint(TargetType targetType, String ownerClass,
                                   String annotationDesc, ClassNode targetElement,
                                   List<Object> values) {
        this.targetType = targetType;
        this.ownerClass = ownerClass;
        this.annotationDesc = annotationDesc;
        this.targetElement = targetElement;
        this.values = values;
        this.bytes = null;
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

    public ClassNode getTargetElement() {
        return targetElement;
    }

    public void setTargetElement(ClassNode targetElement) {
        this.targetElement = targetElement;
    }

    public List<Object> getValues() {
        return values;
    }

    public void setValues(List<Object> values) {
        this.values = values;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }


    public MethodNode getMethod() {
        return method;
    }

    public void setMethod(MethodNode method) {
        this.method = method;
    }
}

