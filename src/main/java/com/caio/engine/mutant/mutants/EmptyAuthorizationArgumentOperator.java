package com.caio.engine.mutant.mutants;

public class EmptyAuthorizationArgumentOperator implements MutantStrategy {

    @Override
    public String make(String value) {
        return value.replaceAll("\\(([^)]*)\\)", "(\'\')");
    }

}
