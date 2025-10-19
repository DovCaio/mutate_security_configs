package com.caio.exceptions;

public class NoOneAnnotationMutableFinded extends RuntimeException{

    public NoOneAnnotationMutableFinded(String message) {
        super(message);
    }

    public NoOneAnnotationMutableFinded() {
        super("Nenhuma annotation de segurança, possível de ser mutada foi encontrada.");
    }
}
