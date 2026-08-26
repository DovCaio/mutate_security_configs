package com.caio.engine.mutant.detect_pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.caio.engine.mutant.mutants.MutantStrategy;

class AbstractDetectPatternTest {

    @Test
    void shouldDetectPattern() {

        AbstractDetectPattern pattern = new AbstractDetectPattern(
                "hasRole",
                "hasRole('ADMIN')");

        assertTrue(pattern.detect());
    }

    @Test
    void shouldNotDetectPattern() {

        AbstractDetectPattern pattern = new AbstractDetectPattern(
                "hasRole",
                "hasAuthority('ADMIN')");

        assertFalse(pattern.detect());
    }

    @Test
    void shouldResetMatcherBeforeDetecting() {

        AbstractDetectPattern pattern = new AbstractDetectPattern(
                "hasRole",
                "hasRole('ADMIN')");

        assertTrue(pattern.detect());
        assertTrue(pattern.detect());
        assertTrue(pattern.detect());
    }

    @Test
    void shouldReturnMatcher() {

        AbstractDetectPattern pattern = new AbstractDetectPattern(
                "(hasRole)\\('([^']+)'\\)",
                "hasRole('ADMIN')");

        pattern.detect();

        assertEquals(
                "hasRole('ADMIN')",
                pattern.getMatcher().group(0));
    }

    @Test
    void shouldReturnRequestedGroup() {

        AbstractDetectPattern pattern = new AbstractDetectPattern(
                "(hasRole)\\('([^']+)'\\)",
                "hasRole('ADMIN')");

        pattern.detect();

        assertEquals(
                "hasRole",
                pattern.getGroup(1));

        assertEquals(
                "ADMIN",
                pattern.getGroup(2));
    }

    @Test
    void shouldAddMutantStrategy() {

        AbstractDetectPattern pattern = new AbstractDetectPattern(
                "hasRole",
                "hasRole('ADMIN')");

        MutantStrategy strategy = value -> List.of("mutant");

        pattern.addMutantStrategy(strategy);

        assertEquals(
                1,
                pattern.getMutantStrategies().size());

        assertEquals(
                strategy,
                pattern.getMutantStrategies().get(0));
    }

    @Test
    void shouldReturnAllMutantStrategies() {

        AbstractDetectPattern pattern = new AbstractDetectPattern(
                "hasRole",
                "hasRole('ADMIN')");

        MutantStrategy first = value -> List.of("mutant1");

        MutantStrategy second = value -> List.of("mutant2");

        pattern.addMutantStrategy(first);
        pattern.addMutantStrategy(second);

        assertEquals(
                List.of(first, second),
                pattern.getMutantStrategies());
    }

    @Test
    void shouldThrowExceptionWhenRequestedGroupDoesNotExist() {

        AbstractDetectPattern pattern = new AbstractDetectPattern(
                "(hasRole)",
                "hasRole('ADMIN')");

        pattern.detect();

        assertThrows(
                IndexOutOfBoundsException.class,
                () -> pattern.getGroup(2));
    }
}