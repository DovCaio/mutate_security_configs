package com.caio.exceptions;

public class NoOneTestPasses extends RuntimeException {

    public NoOneTestPasses(){
        super("Nenhum teste está passando, assim não é possível de verificar nenhum mutante");
    }


    public NoOneTestPasses(String msg){
        super(msg);
    }
    
}
