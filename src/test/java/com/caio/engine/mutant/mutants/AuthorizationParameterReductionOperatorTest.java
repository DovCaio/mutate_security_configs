package com.caio.engine.mutant.mutants;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

public class AuthorizationParameterReductionOperatorTest {
    @Test
    void shouldRemoveEachAuthorizationParameter() {

        AuthorizationParameterReductionOperator operator = new AuthorizationParameterReductionOperator(
                "'ADMIN', 'STAFF', 'USER'");

        List<String> mutants = operator.make("hasAnyRole('ADMIN', 'STAFF', 'USER')");

        assertEquals(
                List.of(
                        "hasAnyRole('STAFF', 'USER')",
                        "hasAnyRole('ADMIN', 'USER')",
                        "hasAnyRole('ADMIN', 'STAFF')"),
                mutants);
    }

    @Test
    void shouldRemoveEachParameterFromTwoParameters() {

        AuthorizationParameterReductionOperator operator = new AuthorizationParameterReductionOperator(
                "'ADMIN', 'STAFF'");

        List<String> mutants = operator.make("hasAnyRole('ADMIN', 'STAFF')");

        assertEquals(
                List.of(
                        "hasAnyRole('STAFF')",
                        "hasAnyRole('ADMIN')"),
                mutants);
    }
}
