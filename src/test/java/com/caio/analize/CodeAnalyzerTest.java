package com.caio.analize;

import org.junit.jupiter.api.Test;

import com.caio.models.AnnotationMutationPoint;

import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.ArrayList;

public class CodeAnalyzerTest {
    
    private CodeAnalyzer codeAnalyzer;
    
    @BeforeEach
    void setUp() {
        codeAnalyzer = new CodeAnalyzer();
    }
    
    @Test
    void testCodeAnalyzerInitialization() {
        assertNotNull(new CodeAnalyzer());
    }
    
    @Test
    void testAnalyzeWithNullPath() {
        assertThrows(IllegalArgumentException.class, () -> codeAnalyzer.analyze(null));     
    }
    
    @Test
    void testGetControllers() {
        assertNotNull(codeAnalyzer.getControllers());
        assertTrue(codeAnalyzer.getControllers().isEmpty());
    }
    
    @Test
    void testGetMutationsPoints() {
        assertNotNull(codeAnalyzer.getMutationsPoints());
        assertTrue(codeAnalyzer.getMutationsPoints().isEmpty());
    }
    
    @Test
    void testSetMutationsPoints() {
        List<AnnotationMutationPoint> points = new ArrayList<>();
        codeAnalyzer.setMutationsPoints(points);
        assertEquals(points, codeAnalyzer.getMutationsPoints());
    }
    
    @Test
    void testGetMainClasses() {
        assertNotNull(codeAnalyzer.getmainClasses());
        assertTrue(codeAnalyzer.getmainClasses().isEmpty());
    }
}