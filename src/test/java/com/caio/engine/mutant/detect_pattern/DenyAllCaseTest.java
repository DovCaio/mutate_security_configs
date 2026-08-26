package com.caio.engine.mutant.detect_pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.caio.engine.mutant.mutants.MutantStrategy;
import com.caio.engine.mutant.mutants.PermitAllReplacementOperator;

class DenyAllCaseTest {

    @Test
    void shouldDetectDenyAll() {

        DenyAllCase pattern = new DenyAllCase("denyAll()");

        assertTrue(pattern.detect());
    }

    @Test
    void shouldNotDetectOtherExpressions() {

        DenyAllCase pattern = new DenyAllCase("permitAll()");

        assertFalse(pattern.detect());
    }

    @Test
    void shouldNotDetectDenyAllWithoutParentheses() {

        DenyAllCase pattern = new DenyAllCase("denyAll");

        assertFalse(pattern.detect());
    }

    @Test
    void shouldReturnPermitAllReplacementStrategy() {

        DenyAllCase pattern = new DenyAllCase("denyAll()");

        List<MutantStrategy> strategies = pattern.execute();

        assertEquals(1, strategies.size());

        assertTrue(
                strategies.get(0) instanceof PermitAllReplacementOperator);
    }

    @Test
    void shouldReturnEmptyListWhenDenyAllIsNotDetected() {

        DenyAllCase pattern = new DenyAllCase("hasRole('ADMIN')");

        List<MutantStrategy> strategies = pattern.execute();

        assertTrue(strategies.isEmpty());
    }

    @Test
    void shouldNotDuplicateStrategyWhenExecuteIsCalledMultipleTimes() {

        DenyAllCase pattern = new DenyAllCase("denyAll()");

        pattern.execute();
        pattern.execute();

        assertEquals(
                1,
                pattern.getMutantStrategies().size());
    }
}