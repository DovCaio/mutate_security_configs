package com.caio.engine.mutant.mutants;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

public class PermitAllReplacementOperatorTest {
    private final PermitAllReplacementOperator operator = new PermitAllReplacementOperator();

    @Test
    void shouldAlwaysReturnDenyAll() {

        assertEquals(
                List.of("permitAll()"),
                operator.make("hasAuthority('READ')"));

        assertEquals(
                List.of("permitAll()"),
                operator.make("hasAnyRole('ADMIN', 'USER')"));

        assertEquals(
                List.of("permitAll()"),
                operator.make("permitAll()"));

        assertEquals(
                List.of("permitAll()"),
                operator.make("hasPermission(#id, 'Document', 'Read')"));

        assertEquals(
                List.of("permitAll()"),
                operator.make("@.hasPermission('NormalUserCanPass')"));
    }
}
