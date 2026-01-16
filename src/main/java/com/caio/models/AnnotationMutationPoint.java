package com.caio.models;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

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

    private AnnotationMutationPoint(
            TargetType targetType,
            String ownerClass,
            String annotationDesc,
            ClassNode targetElement,
            List<Object> values,
            byte[] bytes,
            MethodNode method) {

        this.targetType = targetType;
        this.ownerClass = ownerClass.replace( "/", ".");
        this.annotationDesc = annotationDesc;
        this.targetElement = targetElement;
        this.values = values != null ? values : List.of();
        this.bytes = bytes;
        this.method = method;
    }



    /** Factory para METHOD */
    public static AnnotationMutationPoint forMethod(
            String ownerClass,
            String annotationDesc,
            ClassNode classNode,
            List<Object> values,
            byte[] bytes,
            MethodNode method) {

        return new AnnotationMutationPoint(
                TargetType.METHOD,
                ownerClass,
                annotationDesc,
                classNode,
                values,
                bytes,
                method
        );
    }


    /** Factory para CLASS */
    public static AnnotationMutationPoint forClass(
            String ownerClass,
            String annotationDesc,
            ClassNode classNode,
            List<Object> values,
            byte[] bytes) {

        return new AnnotationMutationPoint(
                TargetType.CLASS,
                ownerClass,
                annotationDesc,
                classNode,
                values,
                bytes,
                null   // não existe método
        );
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

