package com.caio.engine.mutant.mutants;

import java.util.regex.Matcher;

public class LogicalNegationSecurityOperator implements MutantStrategy {

    private String insideQuotes;

    public LogicalNegationSecurityOperator(Matcher matcher) {
        this.insideQuotes = matcher.group(2);
    }

    @Override
    public String make(String value) {
        return value.replace(insideQuotes, "NO_" + insideQuotes);
    }

}
