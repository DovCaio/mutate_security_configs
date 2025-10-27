package com.caio.engine;

import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;

import com.caio.models.AnnotationMutationPoint;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


import java.util.List;
import java.util.Map;

public class Engine {

    
    private MutantGeneration mutantGeneration;
    private MemoryCodeLoader memoryCodeLoader;


    public Engine(List<AnnotationMutationPoint> amps,List<AnnotationMutationPoint> mainClasses, List<AnnotationMutationPoint> testClasses) {
        
        this.mutantGeneration = new MutantGeneration(amps);
        this.memoryCodeLoader = new MemoryCodeLoader(mainClasses, testClasses);
    }

    public void start() throws Exception {
        this.mutantGeneration.createMutants();
        this.memoryCodeLoader.loadAllInMemory();
        this.memoryCodeLoader.loadMutantInMemory(this.mutantGeneration.getMutants());
    }

   

    

    public List<AnnotationMutationPoint> getMutants() {
        return this.mutantGeneration.getMutants();
    }

    public void setMutants(List<AnnotationMutationPoint> mutants) {
        this.mutantGeneration.setMutants(mutants);
    }

    

}
