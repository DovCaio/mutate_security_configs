package com.caio.engine.mutant.mutants;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class LogicalNegationSecurityOperator implements MutantStrategy {

    private Matcher matcher;

    public LogicalNegationSecurityOperator(Matcher matcher) {
        this.matcher = matcher;
    }

    @Override
    public List<String> make(String value) {
        List<String> mutants = new ArrayList<>();

        while (matcher.find()) {
            String expr = matcher.group(0);

            if (!expr.startsWith("!")) {

                String mutant = value.substring(0, matcher.start()) +
                        "!" + expr +
                        value.substring(matcher.end());

                mutants.add(mutant);
            } else {
                String mutant = value.substring(0, matcher.start()) +
                        expr.substring(1) +
                        value.substring(matcher.end());

                mutants.add(mutant);
            }
        }

        return mutants;
    }
}
