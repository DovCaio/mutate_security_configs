package com.caio.engine.mutant.mutants;

import java.util.ArrayList;
import java.util.List;

public class EmptyAuthorizationArgumentOperator implements MutantStrategy {

    @Override
    public List<String> make(String value) {
        return new ArrayList<>(List.of(value.replaceAll("\\(([^)]*)\\)", "(\'\')")));
    }

}
