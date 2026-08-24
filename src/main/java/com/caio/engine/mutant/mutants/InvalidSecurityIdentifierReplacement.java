package com.caio.engine.mutant.mutants;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class InvalidSecurityIdentifierReplacement implements MutantStrategy {

    private String insideQuotes;
    private Matcher matcher;

    public InvalidSecurityIdentifierReplacement(Matcher matcher) {
        this.matcher = matcher;
        this.insideQuotes = matcher.group(1);
        if (matcher.groupCount() >= 2) {
            this.insideQuotes = matcher.group(2);
        }
    }

    @Override
    public List<String> make(String value) {

        List<String> mutateOperators = new ArrayList<>();

        String[] expressions = insideQuotes.split(",");
        if (value.contains("hasPermission") && value.contains("@")) {
            mutateCustomHasPermission(mutateOperators, value);
        } else if (value.contains("hasPermission")) {
            mutatenOnlyPermission(expressions, mutateOperators, value);
        } else {
            mutateEachParam(expressions, mutateOperators);
        }

        return mutateOperators;
    }

    private void mutatenOnlyPermission(String[] args, List<String> mutateOperators, String value) {
        if (args.length < 3)
            throw new Error("Não é uma hasPermission válida.");

        mutateOperators.add(value.replace(args[2], "NO_" + args[2]));
    }

    private void mutateEachParam(String[] expressions, List<String> mutateOperators) {
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
    }

    private void mutateCustomHasPermission(List<String> mutateOperators, String value) {

        while (matcher.find()) {

            String beanName = matcher.group(1);
            String params = insideQuotes;

            String mutatedParams = params.replaceAll("'([^']+)'", "'MUTATED_$1'");
            mutateOperators.add(
                    value.substring(0, matcher.start()) +
                            "@" + beanName + ".hasPermission(" + mutatedParams + ")" +
                            value.substring(matcher.end()));

            mutateOperators.add(
                    value.substring(0, matcher.start()) +
                            "@" + beanName + ".hasPermition(" + params + ")" +
                            value.substring(matcher.end()));
        }

    }

}
