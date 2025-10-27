package com.caio.exceptions;

public class PomFileNotFoundException extends  RuntimeException {

    public PomFileNotFoundException(String message) {
        super(message);
    }

    public PomFileNotFoundException() {
        super("O pom file não foi encontrado");
    }
}
