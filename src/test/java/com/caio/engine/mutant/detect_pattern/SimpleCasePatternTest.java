package com.caio.engine.mutant.detect_pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.caio.engine.mutant.mutants.AuthorizationReplacementOperator;
import com.caio.engine.mutant.mutants.EmptyAuthorizationArgumentOperator;
import com.caio.engine.mutant.mutants.InvalidSecurityIdentifierReplacement;
import com.caio.engine.mutant.mutants.MutantStrategy;
import com.caio.engine.mutant.mutants.SecurityExpressionTypeReplacement;
import com.caio.engine.mutant.mutants.SecurityIdentifierTypeReplacement;

class SimpleCasePatternTest {

    private final List<String> sameSecurityIdentifier = List.of("USER", "STAFF");

    private final List<String> diffSecurityIdentifier = List.of("READ", "WRITE");

    @Test
    void shouldDetectHasRole() {

        SimpleCasePattern pattern = new SimpleCasePattern(
                "hasRole('ADMIN')",
                sameSecurityIdentifier,
                diffSecurityIdentifier);

        assertTrue(pattern.detect());
    }

    @Test
    void shouldDetectHasAuthority() {

        SimpleCasePattern pattern = new SimpleCasePattern(
                "hasAuthority('ADMIN')",
                sameSecurityIdentifier,
                diffSecurityIdentifier);

        assertTrue(pattern.detect());
    }

    @Test
    void shouldDetectNegatedHasRole() {

        SimpleCasePattern pattern = new SimpleCasePattern(
                "!hasRole('ADMIN')",
                sameSecurityIdentifier,
                diffSecurityIdentifier);

        assertTrue(pattern.detect());
    }

    @Test
    void shouldDetectNegatedHasAuthority() {

        SimpleCasePattern pattern = new SimpleCasePattern(
                "!hasAuthority('ADMIN')",
                sameSecurityIdentifier,
                diffSecurityIdentifier);

        assertTrue(pattern.detect());
    }

    @Test
    void shouldNotDetectCompositeRole() {

        SimpleCasePattern pattern = new SimpleCasePattern(
                "hasAnyRole('ADMIN', 'USER')",
                sameSecurityIdentifier,
                diffSecurityIdentifier);

        assertFalse(pattern.detect());
    }

    @Test
    void shouldNotDetectCompositeAuthority() {

        SimpleCasePattern pattern = new SimpleCasePattern(
                "hasAnyAuthority('ADMIN', 'USER')",
                sameSecurityIdentifier,
                diffSecurityIdentifier);

        assertFalse(pattern.detect());
    }

    @Test
    void shouldNotDetectUnknownExpression() {

        SimpleCasePattern pattern = new SimpleCasePattern(
                "foo('ADMIN')",
                sameSecurityIdentifier,
                diffSecurityIdentifier);

        assertFalse(pattern.detect());
    }

    @Test
    void shouldCaptureSecurityIdentifier() {

        SimpleCasePattern pattern = new SimpleCasePattern(
                "hasRole('ADMIN')",
                sameSecurityIdentifier,
                diffSecurityIdentifier);

        assertTrue(pattern.detect());

        assertEquals(
                "ADMIN",
                pattern.getGroup(2));
    }

    @Test
    void shouldCreateExpectedNumberOfStrategies() {

        SimpleCasePattern pattern = new SimpleCasePattern(
                "hasRole('ADMIN')",
                sameSecurityIdentifier,
                diffSecurityIdentifier);

        List<MutantStrategy> strategies = pattern.execute();

        assertEquals(7, strategies.size());
    }

    @Test
    void shouldCreateExpectedMutantStrategies() {

        SimpleCasePattern pattern = new SimpleCasePattern(
                "hasRole('ADMIN')",
                sameSecurityIdentifier,
                diffSecurityIdentifier);

        List<MutantStrategy> strategies = pattern.execute();

        assertTrue(
                strategies.get(0) instanceof InvalidSecurityIdentifierReplacement);

        assertTrue(
                strategies.get(1) instanceof SecurityExpressionTypeReplacement);

        assertTrue(
                strategies.get(2) instanceof AuthorizationReplacementOperator);

        assertTrue(
                strategies.get(3) instanceof AuthorizationReplacementOperator);

        assertTrue(
                strategies.get(4) instanceof SecurityIdentifierTypeReplacement);

        assertTrue(
                strategies.get(5) instanceof SecurityIdentifierTypeReplacement);

        assertTrue(
                strategies.get(6) instanceof EmptyAuthorizationArgumentOperator);
    }

    @Test
    void shouldReturnEmptyListWhenPatternIsNotDetected() {

        SimpleCasePattern pattern = new SimpleCasePattern(
                "hasAnyRole('ADMIN', 'USER')",
                sameSecurityIdentifier,
                diffSecurityIdentifier);

        List<MutantStrategy> strategies = pattern.execute();

        assertTrue(strategies.isEmpty());
    }

    @Test
    void shouldNotDuplicateStrategiesWhenExecuteIsCalledMultipleTimes() {

        SimpleCasePattern pattern = new SimpleCasePattern(
                "hasRole('ADMIN')",
                sameSecurityIdentifier,
                diffSecurityIdentifier);

        pattern.execute();
        pattern.execute();

        assertEquals(
                7,
                pattern.getMutantStrategies().size());
    }

    @Test
    void shouldWorkWithEmptyIdentifierLists() {

        SimpleCasePattern pattern = new SimpleCasePattern(
                "hasRole('ADMIN')",
                List.of(),
                List.of());

        List<MutantStrategy> strategies = pattern.execute();

        assertEquals(3, strategies.size());
    }
}