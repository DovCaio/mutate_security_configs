package com.caio.engine.mutant.mutants;

import java.util.ArrayList;
import java.util.List;

public class SecurityExpressionTypeReplacement implements MutantStrategy {

    @Override
    public List<String> make(String value) {
        List<String> mutant = new ArrayList<>();

        if (value.contains("hasAuthority")) {
            mutant.add(value.replace("hasAuthority", "hasRole"));
        } else if (value.contains("hasRole")) {
            mutant.add(value.replace("hasRole", "hasAuthority"));
        } else if (value.contains("hasAnyAuthority")) {
            mutant.add(value.replace("hasAnyAuthority", "hasAnyRole"));
        }
        mutant.add(value.replace("hasAnyRole", "hasAnyAuthority"));
        return mutant;

    }

}
