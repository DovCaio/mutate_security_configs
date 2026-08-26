package com.caio.engine.mutant.detect_pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.caio.engine.mutant.mutants.LogicalSecurityOperatorReplacement;
import com.caio.engine.mutant.mutants.MutantStrategy;

class LogicalCaseTest {

    @Test
    void shouldDetectAndOperator() {

        LogicalCase pattern = new LogicalCase(
                "hasRole('ADMIN') and hasRole('USER')");

        assertTrue(pattern.detect());
    }

    @Test
    void shouldDetectOrOperator() {

        LogicalCase pattern = new LogicalCase(
                "hasRole('ADMIN') or hasRole('USER')");

        assertTrue(pattern.detect());
    }

    @Test
    void shouldNotDetectLogicalOperatorInsideAnotherWord() {

        LogicalCase pattern = new LogicalCase(
                "android");

        assertFalse(pattern.detect());
    }

    @Test
    void shouldCaptureAndOperator() {

        LogicalCase pattern = new LogicalCase(
                "hasRole('ADMIN') and hasRole('USER')");

        assertTrue(pattern.detect());

        assertEquals(
                "and",
                pattern.getGroup(1));
    }

    @Test
    void shouldCaptureOrOperator() {

        LogicalCase pattern = new LogicalCase(
                "hasRole('ADMIN') or hasRole('USER')");

        assertTrue(pattern.detect());

        assertEquals(
                "or",
                pattern.getGroup(1));
    }

    @Test
    void shouldReturnLogicalOperatorReplacementStrategy() {

        LogicalCase pattern = new LogicalCase(
                "hasRole('ADMIN') and hasRole('USER')");

        List<MutantStrategy> strategies = pattern.execute();

        assertEquals(1, strategies.size());

        assertTrue(
                strategies.get(0) instanceof LogicalSecurityOperatorReplacement);
    }

    @Test
    void shouldReturnEmptyListWhenLogicalOperatorIsNotDetected() {

        LogicalCase pattern = new LogicalCase(
                "hasRole('ADMIN')");

        List<MutantStrategy> strategies = pattern.execute();

        assertTrue(strategies.isEmpty());
    }

    @Test
    void shouldNotDuplicateStrategyWhenExecuteIsCalledMultipleTimes() {

        LogicalCase pattern = new LogicalCase(
                "hasRole('ADMIN') and hasRole('USER')");

        pattern.execute();
        pattern.execute();

        assertEquals(
                1,
                pattern.getMutantStrategies().size());
    }

    @Test
    void shouldDetectMultipleLogicalOperators() {

        LogicalCase pattern = new LogicalCase(
                "hasRole('ADMIN') and hasRole('USER') or hasRole('READ')");

        assertTrue(pattern.detect());
    }
}