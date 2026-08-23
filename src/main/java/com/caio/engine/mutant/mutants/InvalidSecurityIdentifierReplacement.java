package com.caio.engine.mutant.mutants;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class InvalidSecurityIdentifierReplacement implements MutantStrategy {

    private String insideQuotes;

    public InvalidSecurityIdentifierReplacement(Matcher matcher) {
        this.insideQuotes = matcher.group(2);
    }

    @Override
    public List<String> make(String value) {

        List<String> mutateOperators = new ArrayList<>();

        String[] expressions = insideQuotes.split(",");

        for (int i = 0; i < expressions.length; i++) {
            String[] mutatedExpressions = expressions.clone();

            String expression = expressions[i].trim();
            String mutatedexpression = expression.startsWith("NO_")
                    ? "" + expression.substring(4)
                    : expression.substring(0, 1) + "NO_" + expression.substring(1);

            mutatedExpressions[i] = mutatedexpression;

            String mutatedInsideQuotes = String.join(", ", mutatedExpressions);
            String mutatedExpression = expression.replace(insideQuotes, mutatedInsideQuotes);

            mutateOperators.add(mutatedExpression);
        }

        return mutateOperators;
    }

}
