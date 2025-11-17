package com.caio.exceptions;

public class TypeOfAnnotationPointMutationNonDetected extends RuntimeException{
    public TypeOfAnnotationPointMutationNonDetected() {
        super("O tipo dessa mutação não é reconhecido!");
    }
}
