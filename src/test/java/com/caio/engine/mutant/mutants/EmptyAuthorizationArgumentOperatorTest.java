package com.caio.engine.mutant.mutants;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

public class EmptyAuthorizationArgumentOperatorTest {
    private final EmptyAuthorizationArgumentOperator operator = new EmptyAuthorizationArgumentOperator();

    @Test
    void shouldReplaceAuthorizationArgumentWithEmptyArgument() {

        List<String> mutants = operator.make("hasRole('ADMIN')");

        assertEquals(
                List.of("hasRole('')"),
                mutants);
    }

    @Test
    void shouldReplaceAuthorityArgumentWithEmptyArgument() {

        List<String> mutants = operator.make("hasAuthority('READ')");

        assertEquals(
                List.of("hasAuthority('')"),
                mutants);
    }

    @Test
    void shouldReplaceMultipleArgumentsWithEmptyArgument() {

        List<String> mutants = operator.make(
                "hasAnyRole('ADMIN', 'USER')");

        assertEquals(
                List.of("hasAnyRole('')"),
                mutants);
    }

    @Test
    void shouldPreserveNegation() {

        List<String> mutants = operator.make(
                "!hasRole('ADMIN')");

        assertEquals(
                List.of("!hasRole('')"),
                mutants);
    }
}
