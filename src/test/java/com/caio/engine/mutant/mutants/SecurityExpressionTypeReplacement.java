package com.caio.engine.mutant.mutants;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SecurityExpressionTypeReplacementTest {

    private final SecurityExpressionTypeReplacement operator = new SecurityExpressionTypeReplacement();

    @Test
    void shouldReplaceHasAuthorityWithHasRole() {

        List<String> mutants = operator.make("hasAuthority('ADMIN')");

        assertEquals(
                List.of("hasRole('ADMIN')"),
                mutants);
    }

    @Test
    void shouldReplaceHasRoleWithHasAuthority() {

        List<String> mutants = operator.make("hasRole('ADMIN')");

        assertEquals(
                List.of("hasAuthority('ADMIN')"),
                mutants);
    }

    @Test
    void shouldReplaceHasAnyAuthorityWithHasAnyRole() {

        List<String> mutants = operator.make("hasAnyAuthority('ADMIN', 'USER')");

        assertEquals(
                List.of("hasAnyRole('ADMIN', 'USER')"),
                mutants);
    }

    @Test
    void shouldReplaceHasAnyRoleWithHasAnyAuthority() {

        List<String> mutants = operator.make("hasAnyRole('ADMIN', 'USER')");

        assertEquals(
                List.of(
                        "hasAnyRole('ADMIN', 'USER')"
                                .replace("hasAnyRole", "hasAnyAuthority")),
                mutants);
    }
}