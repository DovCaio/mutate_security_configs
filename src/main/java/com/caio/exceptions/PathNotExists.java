package com.caio.exceptions;

public class PathNotExists extends RuntimeException {

    public PathNotExists(String message) {
        super(message);
    }

    public PathNotExists() {
        super("O caminho especificado não existe ou não é um diretório.");
    }

}
