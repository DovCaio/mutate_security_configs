package com.caio.engine.mutant.detect_pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.caio.engine.mutant.mutants.InvalidSecurityIdentifierReplacement;
import com.caio.engine.mutant.mutants.MutantStrategy;

class HasPermissionPatternTest {

    @Test
    void shouldDetectHasPermission() {

        HasPermissionPattern pattern = new HasPermissionPattern(
                "hasPermission('USER', 'READ', 'ADMIN')");

        assertTrue(pattern.detect());
    }

    @Test
    void shouldCapturePermissionParameters() {

        HasPermissionPattern pattern = new HasPermissionPattern(
                "hasPermission('USER', 'READ', 'ADMIN')");

        assertTrue(pattern.detect());

        assertEquals(
                "'USER', 'READ', 'ADMIN'",
                pattern.getGroup(1));
    }

    @Test
    void shouldNotDetectCustomHasPermission() {

        HasPermissionPattern pattern = new HasPermissionPattern(
                "@auth.hasPermission('USER', 'READ')");

        assertFalse(pattern.detect());
    }

    @Test
    void shouldNotDetectHasPermissionAfterDot() {

        HasPermissionPattern pattern = new HasPermissionPattern(
                "auth.hasPermission('USER', 'READ')");

        assertFalse(pattern.detect());
    }

    @Test
    void shouldDetectHasPermissionInsideExpression() {

        HasPermissionPattern pattern = new HasPermissionPattern(
                "hasRole('ADMIN') and hasPermission('USER', 'READ')");

        assertTrue(pattern.detect());
    }

    @Test
    void shouldReturnInvalidSecurityIdentifierReplacementStrategy() {

        HasPermissionPattern pattern = new HasPermissionPattern(
                "hasPermission('USER', 'READ', 'ADMIN')");

        List<MutantStrategy> strategies = pattern.execute();

        assertEquals(1, strategies.size());

        assertTrue(
                strategies.get(0) instanceof InvalidSecurityIdentifierReplacement);
    }

    @Test
    void shouldReturnEmptyListWhenPatternIsNotDetected() {

        HasPermissionPattern pattern = new HasPermissionPattern(
                "hasRole('ADMIN')");

        List<MutantStrategy> strategies = pattern.execute();

        assertTrue(strategies.isEmpty());
    }

    @Test
    void shouldNotDuplicateStrategyWhenExecuteIsCalledMultipleTimes() {

        HasPermissionPattern pattern = new HasPermissionPattern(
                "hasPermission('USER', 'READ')");

        pattern.execute();
        pattern.execute();

        assertEquals(
                1,
                pattern.getMutantStrategies().size());
    }

    @Test
    void shouldDetectHasPermissionWithSpaces() {

        HasPermissionPattern pattern = new HasPermissionPattern(
                "hasPermission( 'USER', 'READ' )");

        assertTrue(pattern.detect());
    }
}