package com.caio.engine.mutant.mutants;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class SecurityIdentifierTypeReplacement implements MutantStrategy { // faz exatamente a mesma coisa que o
                                                                           // AuthorizationReplacementOperator, fiz isso
                                                                           // para ficar mais de acordo com o artigo

    private String insideQuotes;
    private String roleOrAuthority;
    private List<String> rolesOrAuthorities;

    public SecurityIdentifierTypeReplacement(Matcher matcher, String roleOrAuthority) {
        this.insideQuotes = matcher.group(2);
        this.roleOrAuthority = roleOrAuthority;
    }

    public SecurityIdentifierTypeReplacement(Matcher matcher, List<String> rolesOrAuthorities) {
        this.insideQuotes = matcher.group(2);
        this.rolesOrAuthorities = rolesOrAuthorities;
    }

    @Override
    public List<String> make(String value) {
        List<String> mutateOperators = new ArrayList<>();

        if (roleOrAuthority != null) {
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
            throw new Error("Invalid initiation!");

        }

        return mutateOperators;
    }

}
