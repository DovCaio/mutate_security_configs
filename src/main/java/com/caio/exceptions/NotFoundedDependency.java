package com.caio.exceptions;

public class NotFoundedDependency extends RuntimeException {

    public NotFoundedDependency(String dependency){
        super("Não foi encontrado a dependência: " + dependency);
    }
    
}
