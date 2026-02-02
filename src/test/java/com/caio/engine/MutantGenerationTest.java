package com.caio.engine;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import com.caio.engine.mutant.MutantMaker;
import com.caio.exceptions.NoOnePossibleMutant;
import com.caio.models.AnnotationMutationPoint;
import com.caio.models.AnnotationMutationPoint.TargetType;

class MutantGenerationTest {

    private AnnotationMutationPoint buildAmp(String original) {
        return new AnnotationMutationPoint(
                "pkg",
                "Clazz",
                "method",
                original,
                null,
                TargetType.METHOD,
                Path.of("file.java"),
                10);
    }

    private List<String> roles = List.of("USER", "ADMIN");
    private List<String> authorities = List.of("READ", "WRITE");

    // ===============================
    // ✅ happy path — mutants gerados
    // ===============================
    @Test
    void shouldGenerateMutantsAndCopyAllFieldsCorrectly() throws Exception {

        AnnotationMutationPoint amp = buildAmp("hasRole('USER')");

        try (MockedConstruction<MutantMaker> mocked = Mockito.mockConstruction(MutantMaker.class,
                (mock, context) -> when(mock.genAllMutants())
                        .thenReturn(List.of("permitAll()", "denyAll")))) {

            MutantGeneration generation = new MutantGeneration(List.of(amp));

            generation.createMutants(roles, authorities);

            List<AnnotationMutationPoint> mutants = generation.getMutants();

            assertEquals(2, mutants.size());

            AnnotationMutationPoint m1 = mutants.get(0);

            assertEquals("pkg", m1.getPackageName());
            assertEquals("Clazz", m1.getClassName());
            assertEquals("method", m1.getMethodName());
            assertEquals("hasRole('USER')", m1.getOriginalValue());
            assertEquals(TargetType.METHOD, m1.getTargetType());
            assertEquals(Path.of("file.java"), m1.getFilePath());
            assertEquals(10, m1.getLineNumber());

            assertTrue(
                    m1.getMutatedValue().equals("permitAll()") ||
                            m1.getMutatedValue().equals("denyAll"));

            verify(mocked.constructed().get(0)).genAllMutants();
        }
    }

    // ===============================
    // 🚫 ignora mutants vazios
    // ===============================
    @Test
    void shouldIgnoreEmptyMutantStrings() throws Exception {

        AnnotationMutationPoint amp = buildAmp("x");

        try (MockedConstruction<MutantMaker> mocked = Mockito.mockConstruction(MutantMaker.class,
                (mock, context) -> when(mock.genAllMutants())
                        .thenReturn(List.of("", "valid")))) {

            MutantGeneration generation = new MutantGeneration(List.of(amp));

            generation.createMutants(roles, authorities);

            List<AnnotationMutationPoint> mutants = generation.getMutants();

            assertEquals(1, mutants.size());
            assertEquals("valid", mutants.get(0).getMutatedValue());
        }
    }

    // ===============================
    // 🚫 lista vazia → exceção
    // ===============================
    @Test
    void shouldThrowWhenNoMutantsGenerated() {

        AnnotationMutationPoint amp = buildAmp("x");

        try (MockedConstruction<MutantMaker> mocked = Mockito.mockConstruction(MutantMaker.class,
                (mock, context) -> when(mock.genAllMutants())
                        .thenReturn(List.of()))) {

            MutantGeneration generation = new MutantGeneration(List.of(amp));

            assertThrows(NoOnePossibleMutant.class,
                    () -> generation.createMutants(roles, authorities));
        }
    }

    // ===============================
    // 🔁 múltiplos AMPs
    // ===============================
    @Test
    void shouldAggregateMutantsFromMultipleAmps() throws Exception {

        AnnotationMutationPoint amp1 = buildAmp("a");
        AnnotationMutationPoint amp2 = buildAmp("b");

        try (MockedConstruction<MutantMaker> mocked = Mockito.mockConstruction(MutantMaker.class,
                (mock, context) -> when(mock.genAllMutants())
                        .thenReturn(List.of("m1", "m2")))) {

            MutantGeneration generation = new MutantGeneration(List.of(amp1, amp2));

            generation.createMutants(roles, authorities);

            assertEquals(4, generation.getMutants().size());
        }
    }

    @Test
    void shouldContinueIfCreateMutantFailsForOneEntry() throws Exception {

        AnnotationMutationPoint badAmp = new AnnotationMutationPoint(
                "pkg",
                "Clazz",
                "method",
                "orig",
                null,
                AnnotationMutationPoint.TargetType.METHOD,
                Path.of("file"),
                10) {
            @Override
            public String getPackageName() {
                throw new RuntimeException();
            }
        };
        try (MockedConstruction<MutantMaker> mocked = Mockito.mockConstruction(MutantMaker.class,
                (mock, context) -> when(mock.genAllMutants())
                        .thenReturn(List.of("ok")))) {

            MutantGeneration generation = new MutantGeneration(List.of(badAmp));

            assertThrows(NoOnePossibleMutant.class,
                    () -> generation.createMutants(roles, authorities));
        }
    }

    @Test
    void shouldSetAndGetFields() {

        AnnotationMutationPoint amp = buildAmp("x");
        MutantGeneration gen = new MutantGeneration(List.of(amp));

        assertEquals(1, gen.getAmps().size());

        gen.setAmps(List.of());
        assertTrue(gen.getAmps().isEmpty());

        gen.setMutants(List.of());
        assertTrue(gen.getMutants().isEmpty());
    }

}
