package com.caio.engine.mutant.mutants;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SecurityIdentifierTypeReplacementTest {

    @Test
    void shouldReplaceSecurityIdentifierType() {

        SecurityIdentifierTypeReplacement operator = new SecurityIdentifierTypeReplacement(
                "'ADMIN'",
                "'USER'");

        List<String> mutants = operator.make("hasRole('ADMIN')");

        assertEquals(
                List.of("hasRole('USER')"),
                mutants);
    }

    @Test
    void shouldAddSecurityIdentifiers() {

        SecurityIdentifierTypeReplacement operator = new SecurityIdentifierTypeReplacement(
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
    void shouldNotAddExistingSecurityIdentifier() {

        SecurityIdentifierTypeReplacement operator = new SecurityIdentifierTypeReplacement(
                "'ADMIN'",
                List.of("ADMIN", "USER"));

        List<String> mutants = operator.make("hasAnyRole('ADMIN')");

        assertEquals(
                List.of(
                        "hasAnyRole('ADMIN', 'USER')"),
                mutants);
    }

    @Test
    void shouldReplaceSecurityIdentifierInCompleteExpression() {

        SecurityIdentifierTypeReplacement operator = new SecurityIdentifierTypeReplacement(
                "'ADMIN'",
                "'USER'");

        List<String> mutants = operator.make(
                "hasRole('ADMIN') and hasAuthority('ADMIN')");

        assertEquals(
                List.of(
                        "hasRole('USER') and hasAuthority('USER')"),
                mutants);
    }

    @Test
    void shouldThrowExceptionWhenOperatorIsNotInitialized() {

        SecurityIdentifierTypeReplacement operator = new SecurityIdentifierTypeReplacement(
                "'ADMIN'",
                (String) null);

        assertThrows(
                Error.class,
                () -> operator.make("hasRole('ADMIN')"));
    }
}
