package com.caio.exceptions;


public class DependenciePathIsNull  extends NullPointerException{
    
    DependenciePathIsNull(String msg) {
        super(msg);
    }

    public DependenciePathIsNull() {
        super("O path do diretório das dependências não foi encontrado!");
    }

}
