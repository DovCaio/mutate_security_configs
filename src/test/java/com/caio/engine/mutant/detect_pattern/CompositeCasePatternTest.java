package com.caio.engine.mutant.detect_pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.caio.engine.mutant.mutants.AuthorizationParameterReductionOperator;
import com.caio.engine.mutant.mutants.AuthorizationReplacementOperator;
import com.caio.engine.mutant.mutants.EmptyAuthorizationArgumentOperator;
import com.caio.engine.mutant.mutants.InvalidSecurityIdentifierReplacement;
import com.caio.engine.mutant.mutants.MutantStrategy;
import com.caio.engine.mutant.mutants.SecurityExpressionTypeReplacement;
import com.caio.engine.mutant.mutants.SecurityIdentifierTypeReplacement;

class CompositeCasePatternTest {

    private final List<String> sameSecurityIdentifier = List.of("'USER'", "'STAFF'");

    private final List<String> diffSecurityIdentifier = List.of("'READ'", "'WRITE'");

    @Test
    void shouldDetectHasAnyRole() {

        CompositeCasePattern pattern = new CompositeCasePattern(
                "hasAnyRole('ADMIN', 'STAFF')",
                sameSecurityIdentifier,
                diffSecurityIdentifier);

        assertTrue(pattern.detect());
    }

    @Test
    void shouldDetectHasAnyAuthority() {

        CompositeCasePattern pattern = new CompositeCasePattern(
                "hasAnyAuthority('ADMIN', 'STAFF')",
                sameSecurityIdentifier,
                diffSecurityIdentifier);

        assertTrue(pattern.detect());
    }

    @Test
    void shouldDetectNegatedHasAnyRole() {

        CompositeCasePattern pattern = new CompositeCasePattern(
                "!hasAnyRole('ADMIN', 'STAFF')",
                sameSecurityIdentifier,
                diffSecurityIdentifier);

        assertTrue(pattern.detect());
    }

    @Test
    void shouldDetectNegatedHasAnyAuthority() {

        CompositeCasePattern pattern = new CompositeCasePattern(
                "!hasAnyAuthority('ADMIN', 'STAFF')",
                sameSecurityIdentifier,
                diffSecurityIdentifier);

        assertTrue(pattern.detect());
    }

    @Test
    void shouldNotDetectSimpleRole() {

        CompositeCasePattern pattern = new CompositeCasePattern(
                "hasRole('ADMIN')",
                sameSecurityIdentifier,
                diffSecurityIdentifier);

        assertFalse(pattern.detect());
    }

    @Test
    void shouldNotDetectUnknownExpression() {

        CompositeCasePattern pattern = new CompositeCasePattern(
                "foo('ADMIN', 'STAFF')",
                sameSecurityIdentifier,
                diffSecurityIdentifier);

        assertFalse(pattern.detect());
    }

    @Test
    void shouldCaptureSecurityIdentifiers() {

        CompositeCasePattern pattern = new CompositeCasePattern(
                "hasAnyRole('ADMIN', 'STAFF')",
                sameSecurityIdentifier,
                diffSecurityIdentifier);

        assertTrue(pattern.detect());

        assertEquals(
                "'ADMIN', 'STAFF'",
                pattern.getGroup(2));
    }

    @Test
    void shouldCreateAllMutantStrategies() {

        CompositeCasePattern pattern = new CompositeCasePattern(
                "hasAnyRole('ADMIN', 'STAFF')",
                sameSecurityIdentifier,
                diffSecurityIdentifier);

        List<MutantStrategy> strategies = pattern.execute();

        assertEquals(6, strategies.size());
    }

    @Test
    void shouldCreateExpectedMutantStrategies() {

        CompositeCasePattern pattern = new CompositeCasePattern(
                "hasAnyRole('ADMIN', 'STAFF')",
                sameSecurityIdentifier,
                diffSecurityIdentifier);

        List<MutantStrategy> strategies = pattern.execute();

        assertTrue(
                strategies.get(0) instanceof SecurityExpressionTypeReplacement);

        assertTrue(
                strategies.get(1) instanceof AuthorizationReplacementOperator);

        assertTrue(
                strategies.get(2) instanceof SecurityIdentifierTypeReplacement);

        assertTrue(
                strategies.get(3) instanceof InvalidSecurityIdentifierReplacement);

        assertTrue(
                strategies.get(4) instanceof EmptyAuthorizationArgumentOperator);

        assertTrue(
                strategies.get(5) instanceof AuthorizationParameterReductionOperator);
    }

    @Test
    void shouldReturnEmptyListWhenPatternIsNotDetected() {

        CompositeCasePattern pattern = new CompositeCasePattern(
                "hasRole('ADMIN')",
                sameSecurityIdentifier,
                diffSecurityIdentifier);

        List<MutantStrategy> strategies = pattern.execute();

        assertTrue(strategies.isEmpty());
    }

    @Test
    void shouldNotDuplicateStrategiesWhenExecuteIsCalledMultipleTimes() {

        CompositeCasePattern pattern = new CompositeCasePattern(
                "hasAnyRole('ADMIN', 'STAFF')",
                sameSecurityIdentifier,
                diffSecurityIdentifier);

        pattern.execute();
        pattern.execute();

        assertEquals(
                6,
                pattern.getMutantStrategies().size());
    }
}