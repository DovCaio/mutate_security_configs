package com.caio.exceptions;

public class NoOnePossibleMutant extends RuntimeException {


    public NoOnePossibleMutant(String msg){
        super(msg);
    }

    public NoOnePossibleMutant(){
        super("Nenhum mutante foi possível de ser feito.");
    }
    
}
