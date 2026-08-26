package com.caio.engine.mutant.detect_pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.caio.engine.mutant.mutants.InvalidSecurityIdentifierReplacement;
import com.caio.engine.mutant.mutants.MutantStrategy;

class HasPermissionCustomPatternTest {

    @Test
    void shouldDetectCustomHasPermission() {

        HasPermissionCustomPattern pattern = new HasPermissionCustomPattern(
                "@auth.hasPermission('USER', 'READ')");

        assertTrue(pattern.detect());
    }

    @Test
    void shouldDetectCustomHasPermissionWithSpaces() {

        HasPermissionCustomPattern pattern = new HasPermissionCustomPattern(
                "@auth.hasPermission( 'USER', 'READ' )");

        assertTrue(pattern.detect());
    }

    @Test
    void shouldNotDetectRegularHasPermission() {

        HasPermissionCustomPattern pattern = new HasPermissionCustomPattern(
                "hasPermission('USER', 'READ')");

        assertFalse(pattern.detect());
    }

    @Test
    void shouldNotDetectUnknownMethod() {

        HasPermissionCustomPattern pattern = new HasPermissionCustomPattern(
                "@auth.hasRole('ADMIN')");

        assertFalse(pattern.detect());
    }

    @Test
    void shouldCaptureBeanName() {

        HasPermissionCustomPattern pattern = new HasPermissionCustomPattern(
                "@auth.hasPermission('USER', 'READ')");

        assertTrue(pattern.detect());

        assertEquals(
                "auth",
                pattern.getGroup(1));
    }

    @Test
    void shouldCapturePermissionParameters() {

        HasPermissionCustomPattern pattern = new HasPermissionCustomPattern(
                "@auth.hasPermission('USER', 'READ')");

        assertTrue(pattern.detect());

        assertEquals(
                "'USER', 'READ'",
                pattern.getGroup(2));
    }

    @Test
    void shouldReturnInvalidSecurityIdentifierReplacementStrategy() {

        HasPermissionCustomPattern pattern = new HasPermissionCustomPattern(
                "@auth.hasPermission('USER', 'READ')");

        List<MutantStrategy> strategies = pattern.execute();

        assertEquals(1, strategies.size());

        assertTrue(
                strategies.get(0) instanceof InvalidSecurityIdentifierReplacement);
    }

    @Test
    void shouldReturnEmptyListWhenPatternIsNotDetected() {

        HasPermissionCustomPattern pattern = new HasPermissionCustomPattern(
                "hasRole('ADMIN')");

        List<MutantStrategy> strategies = pattern.execute();

        assertTrue(strategies.isEmpty());
    }

    @Test
    void shouldNotDuplicateStrategyWhenExecuteIsCalledMultipleTimes() {

        HasPermissionCustomPattern pattern = new HasPermissionCustomPattern(
                "@auth.hasPermission('USER', 'READ')");

        pattern.execute();
        pattern.execute();

        assertEquals(
                1,
                pattern.getMutantStrategies().size());
    }
}
