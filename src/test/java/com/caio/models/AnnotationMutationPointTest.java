package com.caio.models;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class AnnotationMutationPointTest {

    @Test
    void shouldCreateCorrectly() {

        AnnotationMutationPoint amp = new AnnotationMutationPoint(
                "pkg",
                "Clazz",
                "method",
                "orig",
                "mut",
                AnnotationMutationPoint.TargetType.METHOD,
                Path.of("A.java"),
                12
        );

        assertEquals("pkg", amp.getPackageName());
        assertEquals("Clazz", amp.getClassName());
        assertEquals("method", amp.getMethodName());
        assertEquals("orig", amp.getOriginalValue());
        assertEquals("mut", amp.getMutatedValue());
        assertEquals(12, amp.getLineNumber());
    }

    @Test
    void shouldSettersWork() {

        AnnotationMutationPoint amp = new AnnotationMutationPoint(
                "p","c","m","o","x",
                AnnotationMutationPoint.TargetType.CLASS,
                Path.of("B.java"),1);

        amp.setMutatedValue("NEW");
        amp.setOriginalValue("OLD");
        amp.setTargetType(AnnotationMutationPoint.TargetType.METHOD);
        amp.setLineNumber(99);
        amp.setFilePath(Path.of("C.java"));
        amp.setAnnotationName(AnnotationMutationPoint.AnnotationType.PRE);

        assertEquals("NEW", amp.getMutatedValue());
        assertEquals("OLD", amp.getOriginalValue());
        assertEquals(99, amp.getLineNumber());
        assertEquals("C.java", amp.getFilePath().toString());
        assertEquals(AnnotationMutationPoint.AnnotationType.PRE, amp.getAnnotationName());
    }
}
