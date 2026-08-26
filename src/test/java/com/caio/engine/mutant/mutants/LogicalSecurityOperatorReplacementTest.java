package com.caio.engine.mutant.mutants;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

class LogicalSecurityOperatorReplacementTest {

    private static final String REGEX = "\\b(and|or)\\b";

    @Test
    void shouldReplaceAndWithOr() {

        String value = "hasRole('ADMIN') and hasAuthority('READ')";

        Matcher matcher = Pattern
                .compile(REGEX)
                .matcher(value);

        LogicalSecurityOperatorReplacement operator = new LogicalSecurityOperatorReplacement(matcher);

        List<String> mutants = operator.make(value);

        assertEquals(
                List.of(
                        "hasRole('ADMIN') or hasAuthority('READ')"),
                mutants);
    }

    @Test
    void shouldReplaceOrWithAnd() {

        String value = "hasRole('ADMIN') or hasAuthority('READ')";

        Matcher matcher = Pattern
                .compile(REGEX)
                .matcher(value);

        LogicalSecurityOperatorReplacement operator = new LogicalSecurityOperatorReplacement(matcher);

        List<String> mutants = operator.make(value);

        assertEquals(
                List.of(
                        "hasRole('ADMIN') and hasAuthority('READ')"),
                mutants);
    }

    @Test
    void shouldMutateEachLogicalOperator() {

        String value = "hasRole('ADMIN') and hasAuthority('READ') or hasRole('USER')";

        Matcher matcher = Pattern
                .compile(REGEX)
                .matcher(value);

        LogicalSecurityOperatorReplacement operator = new LogicalSecurityOperatorReplacement(matcher);

        List<String> mutants = operator.make(value);

        assertEquals(
                List.of(
                        "hasRole('ADMIN') or hasAuthority('READ') or hasRole('USER')",
                        "hasRole('ADMIN') and hasAuthority('READ') and hasRole('USER')"),
                mutants);
    }
}
