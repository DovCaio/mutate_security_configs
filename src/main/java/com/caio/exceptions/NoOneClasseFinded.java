package com.caio.exceptions;

public class NoOneClasseFinded extends RuntimeException {

    public NoOneClasseFinded(String message) {
        super(message);
    }

    public NoOneClasseFinded() {
        super("Nenhuma classe encontrada no caminho fornecido.");
    }
}
