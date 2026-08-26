package com.caio.engine.mutant.mutants;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

class LogicalNegationSecurityOperatorTest {

    private static final String REGEX = "(!?)(hasRole\\([^)]*\\)|hasAuthority\\([^)]*\\))";

    @Test
    void shouldAddNegationToSecurityExpression() {

        String value = "hasRole('ADMIN')";

        Matcher matcher = Pattern
                .compile(REGEX)
                .matcher(value);

        LogicalNegationSecurityOperator operator = new LogicalNegationSecurityOperator(matcher);

        List<String> mutants = operator.make(value);

        assertEquals(
                List.of("!hasRole('ADMIN')"),
                mutants);
    }

    @Test
    void shouldRemoveNegationFromSecurityExpression() {

        String value = "!hasRole('ADMIN')";

        Matcher matcher = Pattern
                .compile(REGEX)
                .matcher(value);

        LogicalNegationSecurityOperator operator = new LogicalNegationSecurityOperator(matcher);

        List<String> mutants = operator.make(value);

        assertEquals(
                List.of("hasRole('ADMIN')"),
                mutants);
    }

    @Test
    void shouldMutateMultipleSecurityExpressions() {

        String value = "hasRole('ADMIN') and hasAuthority('READ')";

        Matcher matcher = Pattern
                .compile(REGEX)
                .matcher(value);

        LogicalNegationSecurityOperator operator = new LogicalNegationSecurityOperator(matcher);

        List<String> mutants = operator.make(value);

        assertEquals(
                List.of(
                        "!hasRole('ADMIN') and hasAuthority('READ')",
                        "hasRole('ADMIN') and !hasAuthority('READ')"),
                mutants);
    }

    @Test
    void shouldMutateMultipleNegatedSecurityExpressions() {

        String value = "!hasRole('ADMIN') and !hasAuthority('READ')";

        Matcher matcher = Pattern
                .compile(REGEX)
                .matcher(value);

        LogicalNegationSecurityOperator operator = new LogicalNegationSecurityOperator(matcher);

        List<String> mutants = operator.make(value);

        assertEquals(
                List.of(
                        "hasRole('ADMIN') and !hasAuthority('READ')",
                        "!hasRole('ADMIN') and hasAuthority('READ')"),
                mutants);
    }
}