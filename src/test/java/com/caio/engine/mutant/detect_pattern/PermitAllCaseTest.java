package com.caio.engine.mutant.detect_pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.caio.engine.mutant.mutants.DenyAllReplacementOperator;
import com.caio.engine.mutant.mutants.MutantStrategy;

class PermitAllCaseTest {

    @Test
    void shouldDetectPermitAll() {

        PermitAllCase pattern = new PermitAllCase("permitAll()");

        assertTrue(pattern.detect());
    }

    @Test
    void shouldNotDetectOtherExpressions() {

        PermitAllCase pattern = new PermitAllCase("denyAll()");

        assertFalse(pattern.detect());
    }

    @Test
    void shouldNotDetectPermitAllWithoutParentheses() {

        PermitAllCase pattern = new PermitAllCase("permitAll");

        assertFalse(pattern.detect());
    }

    @Test
    void shouldReturnDenyAllReplacementStrategy() {

        PermitAllCase pattern = new PermitAllCase("permitAll()");

        List<MutantStrategy> strategies = pattern.execute();

        assertEquals(1, strategies.size());

        assertTrue(
                strategies.get(0) instanceof DenyAllReplacementOperator);
    }

    @Test
    void shouldReturnEmptyListWhenPermitAllIsNotDetected() {

        PermitAllCase pattern = new PermitAllCase("hasRole('ADMIN')");

        List<MutantStrategy> strategies = pattern.execute();

        assertTrue(strategies.isEmpty());
    }

    @Test
    void shouldNotDuplicateStrategyWhenExecuteIsCalledMultipleTimes() {

        PermitAllCase pattern = new PermitAllCase("permitAll()");

        pattern.execute();
        pattern.execute();

        assertEquals(
                1,
                pattern.getMutantStrategies().size());
    }
}