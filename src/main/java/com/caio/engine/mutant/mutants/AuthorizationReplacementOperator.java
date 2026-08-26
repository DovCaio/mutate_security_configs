package com.caio.engine.mutant.mutants;

import java.util.ArrayList;
import java.util.List;

public class AuthorizationReplacementOperator implements MutantStrategy {

    private String insideQuotes;
    private String roleOrAuthority;
    private List<String> rolesOrAuthorities;

    public AuthorizationReplacementOperator(String insideQuotes, String roleOrAuthority) {
        this.insideQuotes = insideQuotes;
        this.roleOrAuthority = roleOrAuthority;
    }

    public AuthorizationReplacementOperator(String insideQuotes, List<String> rolesOrAuthorities) {
        this.insideQuotes = insideQuotes;
        this.rolesOrAuthorities = rolesOrAuthorities;
    }

    @Override
    public List<String> make(String value) {
        List<String> mutateOperators = new ArrayList<>();

        if (roleOrAuthority != null) {
            if (insideQuotes.equals(roleOrAuthority)) {
                return mutateOperators;
            }
            mutateOperators.add(value.replace(insideQuotes, roleOrAuthority));
        } else if (rolesOrAuthorities != null) {
            for (String ra : rolesOrAuthorities) {
                if (!insideQuotes.contains(ra)) {
                    String mutatedInsideQuotes = insideQuotes + ", " + "'" + ra + "'";

                    String mutatedExpression = value.replace(insideQuotes, mutatedInsideQuotes);
                    mutateOperators.add(mutatedExpression);
                }
            }
        } else {
            throw new IllegalStateException("AuthorizationReplacementOperator was not properly initialized");
        }

        return mutateOperators;
    }

}
