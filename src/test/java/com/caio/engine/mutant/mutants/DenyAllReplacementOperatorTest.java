package com.caio.engine.mutant.mutants;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

public class DenyAllReplacementOperatorTest {
    private final DenyAllReplacementOperator operator = new DenyAllReplacementOperator();

    @Test
    void shouldAlwaysReturnDenyAll() {

        assertEquals(
                List.of("denyAll"),
                operator.make("hasAuthority('READ')"));

        assertEquals(
                List.of("denyAll"),
                operator.make("hasAnyRole('ADMIN', 'USER')"));

        assertEquals(
                List.of("denyAll"),
                operator.make("permitAll()"));

        assertEquals(
                List.of("denyAll"),
                operator.make("hasPermission(#id, 'Document', 'Read')"));

        assertEquals(
                List.of("denyAll"),
                operator.make("@.hasPermission('NormalUserCanPass')"));
    }
}
