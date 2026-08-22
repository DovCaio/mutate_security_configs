package com.caio.engine.mutant.mutants;

import java.util.regex.Matcher;

public class SecurityExpressionTypeReplacement implements MutantStrategy {

    @Override
    public String make(String value) {

        if (value.contains("hasAuthority")) {
            return value.replace("hasAuthority", "hasRole");
        }
        return value.replace("hasRole", "hasAuthority");

    }

}
