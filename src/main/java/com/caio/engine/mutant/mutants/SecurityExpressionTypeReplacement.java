package com.caio.engine.mutant.mutants;

import java.util.ArrayList;
import java.util.List;

public class SecurityExpressionTypeReplacement implements MutantStrategy {

    @Override
    public List<String> make(String value) {

        List<String> mutants = new ArrayList<>();

        if (value.contains("hasAuthority")) {
            mutants.add(value.replace("hasAuthority", "hasRole"));
        } else if (value.contains("hasRole")) {
            mutants.add(value.replace("hasRole", "hasAuthority"));
        } else if (value.contains("hasAnyAuthority")) {
            mutants.add(value.replace("hasAnyAuthority", "hasAnyRole"));
        } else if (value.contains("hasAnyRole")) {
            mutants.add(value.replace("hasAnyRole", "hasAnyAuthority"));
        }

        return mutants;
    }

}
