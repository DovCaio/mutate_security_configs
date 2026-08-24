package com.caio.engine.mutant.mutants;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

class AuthorizationReplacementOperatorTest {

        @Test
        void shouldReplaceAuthorization() {

                AuthorizationReplacementOperator operator = new AuthorizationReplacementOperator(
                                "'ADMIN'",
                                "'USER'");

                List<String> mutants = operator.make("hasRole('ADMIN')");

                assertEquals(
                                List.of("hasRole('USER')"),
                                mutants);
        }

        @Test
        void shouldAddSecurityIdentifiers() {

                AuthorizationReplacementOperator operator = new AuthorizationReplacementOperator(
                                "'ADMIN'",
                                List.of("USER", "MANAGER"));

                List<String> mutants = operator.make("hasAnyRole('ADMIN')");

                assertEquals(
                                List.of(
                                                "hasAnyRole('ADMIN', 'USER')",
                                                "hasAnyRole('ADMIN', 'MANAGER')"),
                                mutants);
        }

        @Test
        void shouldNotDuplicateExistingAuthorization() {

                AuthorizationReplacementOperator operator = new AuthorizationReplacementOperator(
                                "'ADMIN'",
                                List.of("ADMIN", "USER"));

                List<String> mutants = operator.make("hasAnyRole('ADMIN')");

                assertEquals(
                                List.of(
                                                "hasAnyRole('ADMIN', 'USER')"),
                                mutants);
        }
}