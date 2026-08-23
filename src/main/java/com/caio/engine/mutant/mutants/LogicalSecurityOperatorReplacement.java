package com.caio.engine.mutant.mutants;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class LogicalSecurityOperatorReplacement implements MutantStrategy {

    private Matcher matcher;

    public LogicalSecurityOperatorReplacement(Matcher matcher) {
        this.matcher = matcher;
    }

    @Override
    public List<String> make(String value) {
        List<String> mutateOperators = new ArrayList<>();

        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();

            String operator = matcher.group(1);
            String mutatedOperator = operator.equals("and") ? "or" : "and";

            String mutant = value.substring(0, start) +
                    mutatedOperator +
                    value.substring(end);

            mutateOperators.add(mutant);
        }

        return mutateOperators;
    }

}
