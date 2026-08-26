package com.caio.engine.mutant.detect_pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.caio.engine.mutant.mutants.LogicalNegationSecurityOperator;
import com.caio.engine.mutant.mutants.MutantStrategy;

class AllPredicatesCasePatternTest {

    @Test
    void shouldDetectHasRole() {

        AllPredicatesCasePattern pattern = new AllPredicatesCasePattern(
                "hasRole('ADMIN')");

        assertTrue(pattern.detect());
    }

    @Test
    void shouldDetectHasAuthority() {

        AllPredicatesCasePattern pattern = new AllPredicatesCasePattern(
                "hasAuthority('ADMIN')");

        assertTrue(pattern.detect());
    }

    @Test
    void shouldDetectHasAnyRole() {

        AllPredicatesCasePattern pattern = new AllPredicatesCasePattern(
                "hasAnyRole('ADMIN', 'USER')");

        assertTrue(pattern.detect());
    }

    @Test
    void shouldDetectHasAnyAuthority() {

        AllPredicatesCasePattern pattern = new AllPredicatesCasePattern(
                "hasAnyAuthority('ADMIN', 'USER')");

        assertTrue(pattern.detect());
    }

    @Test
    void shouldDetectHasPermission() {

        AllPredicatesCasePattern pattern = new AllPredicatesCasePattern(
                "hasPermission('USER', 'READ', 'DOCUMENT')");

        assertTrue(pattern.detect());
    }

    @Test
    void shouldDetectCustomHasPermission() {

        AllPredicatesCasePattern pattern = new AllPredicatesCasePattern(
                "@auth.hasPermission('USER', 'READ')");

        assertTrue(pattern.detect());
    }

    @Test
    void shouldDetectNegatedPredicate() {

        AllPredicatesCasePattern pattern = new AllPredicatesCasePattern(
                "!hasRole('ADMIN')");

        assertTrue(pattern.detect());
    }

    @Test
    void shouldNotDetectUnknownPredicate() {

        AllPredicatesCasePattern pattern = new AllPredicatesCasePattern(
                "foo('ADMIN')");

        assertFalse(pattern.detect());
    }

    @Test
    void shouldReturnLogicalNegationStrategy() {

        AllPredicatesCasePattern pattern = new AllPredicatesCasePattern(
                "hasRole('ADMIN')");

        List<MutantStrategy> strategies = pattern.execute();

        assertEquals(1, strategies.size());
        assertTrue(
                strategies.get(0) instanceof LogicalNegationSecurityOperator);
    }

    @Test
    void shouldReturnEmptyListWhenPatternIsNotDetected() {

        AllPredicatesCasePattern pattern = new AllPredicatesCasePattern(
                "foo('ADMIN')");

        List<MutantStrategy> strategies = pattern.execute();

        assertTrue(strategies.isEmpty());
    }
}
