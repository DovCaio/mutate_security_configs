package com.caio.engine.mutant.mutants;

import java.util.ArrayList;
import java.util.List;

public class AuthorizationParameterReductionOperator implements MutantStrategy {

    private String insideQuotes;

    public AuthorizationParameterReductionOperator(String insideQuotes) {
        this.insideQuotes = insideQuotes;
    }

    @Override
    public List<String> make(String value) {
        List<String> ra = insideQuotes != null
                ? List.of(insideQuotes.split(","))
                        .stream()
                        .map(String::trim)
                        .toList()
                : new ArrayList<>();

        List<String> result = new ArrayList<>();

        for (int i = 0; i < ra.size(); i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < ra.size(); j++) {
                if (i != j) {
                    if (sb.length() > 0) {
                        sb.append(", ");
                    }
                    sb.append(ra.get(j));
                }
            }
            String mutatedInsideQuotes = sb.toString();
            String fullExpression = value;
            String mutatedExpression = fullExpression.replace(insideQuotes, mutatedInsideQuotes);
            result.add(mutatedExpression);
        }

        return result;
    }

}
