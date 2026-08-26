package com.caio.engine.mutant.mutants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.caio.exceptions.InvalidClassInitiation;
import com.caio.exceptions.InvalidOperator;

public class InvalidSecurityIdentifierReplacement implements MutantStrategy {

    private String insideQuotes;
    private String beanName;

    public InvalidSecurityIdentifierReplacement(String insideQuotes) throws InvalidClassInitiation {
        if (insideQuotes == null)
            throw new InvalidClassInitiation("insideQuotes não deve ser nullo");
        this.insideQuotes = insideQuotes;
    }

    public InvalidSecurityIdentifierReplacement(String insideQuotes, String beanName) {
        if (insideQuotes == null)
            throw new InvalidClassInitiation("insideQuotes não deve ser null");
        if (beanName == null)
            throw new InvalidClassInitiation("beanName não deve ser null");
        this.insideQuotes = insideQuotes;
        this.beanName = beanName;
    }

    @Override
    public List<String> make(String value) {

        List<String> mutateOperators = new ArrayList<>();

        String[] expressions = Arrays.stream(insideQuotes.split(","))
                .map(String::trim)
                .toArray(String[]::new);

        if (value.contains("hasPermission") && value.contains("@")) {
            mutateCustomHasPermission(mutateOperators, value);
        } else if (value.contains("hasPermission")) {
            mutatenOnlyPermission(expressions, mutateOperators, value);
        } else {
            mutateEachParam(expressions, mutateOperators, value);
        }

        return mutateOperators;
    }

    private void mutatenOnlyPermission(String[] args, List<String> mutateOperators, String value) {
        if (args.length < 3)
            throw new InvalidOperator("Não é uma hasPermission válida.");
        if (args[2].contains("NO_"))
            mutateOperators.add(value.replace(args[2], args[2].replace("NO_", "")));
        else
            mutateOperators.add(value.replace(args[2], "'NO_" + args[2].replace("'", "") + "'"));
    }

    private void mutateEachParam(
            String[] expressions,
            List<String> mutateOperators,
            String value) {

        for (int i = 0; i < expressions.length; i++) {

            String[] mutatedExpressions = expressions.clone();

            String expression = expressions[i].trim();

            String content = expression;

            if (content.length() >= 2
                    && content.startsWith("'")
                    && content.endsWith("'")) {

                content = content.substring(1, content.length() - 1);
            }

            String mutatedContent;

            if (content.startsWith("NO_")) {
                mutatedContent = content.replaceFirst("NO_", "");
            } else {
                mutatedContent = "NO_" + content;
            }

            String mutatedExpression;

            if (expression.length() >= 2
                    && ((expression.startsWith("'") && expression.endsWith("'"))
                            || (expression.startsWith("\"") && expression.endsWith("\"")))) {

                mutatedExpression = expression.charAt(0)
                        + mutatedContent
                        + expression.charAt(expression.length() - 1);

            } else {
                mutatedExpression = mutatedContent;
            }

            mutatedExpressions[i] = mutatedExpression;

            String mutatedInsideQuotes = String.join(", ", mutatedExpressions);

            mutateOperators.add(
                    value.replace(
                            insideQuotes,
                            mutatedInsideQuotes));
        }
    }

    private void mutateCustomHasPermission(List<String> mutateOperators, String value) {

        String original = "@" + beanName + ".hasPermission(" + insideQuotes + ")";
        String mutatedParams = insideQuotes.replaceAll("'([^']+)'", "'MUTATED_$1'");

        mutateOperators.add(
                value.replace(
                        original,
                        "@" + beanName + ".hasPermission(" + mutatedParams + ")"));

    }

}
