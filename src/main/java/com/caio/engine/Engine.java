package com.caio.engine;

import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.engine.DiscoverySelector;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import com.caio.models.AnnotationMutationPoint;
import com.caio.utli.ClassNodeCloner;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import java.util.List;
import java.util.Map;

public class Engine {

    
    private RunTest runTest;
    private MutantGeneration mutantGeneration;

    public Engine(List<AnnotationMutationPoint> amps) {
        
        this.runTest = new RunTest();
        this.mutantGeneration = new MutantGeneration(amps);
    }

    public void start(List<AnnotationMutationPoint> allClasses) throws Exception {
        this.mutantGeneration.createMutants();
        loadAllInMemory(allClasses);
        loadMutantInMemory();
    }

   

    private void loadAllInMemory(List<AnnotationMutationPoint> allClasses) throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, InstantiationException, IllegalArgumentException, NoSuchMethodException, SecurityException {
        Map<String, byte[]> allBytes = new HashMap<>();
        for (AnnotationMutationPoint c : allClasses) {
            allBytes.put(c.getTargetElement().name.replace('/', '.'), c.getBytes());
        }

        NonMutantClassLoader loader = new NonMutantClassLoader(allBytes);

        List<Class<?>> testClasses = new ArrayList<>();

        for (String className : allBytes.keySet()) {
            Class<?> clazz = loader.loadClass(className);

            boolean hasTest = Arrays.stream(clazz.getDeclaredMethods())
                .anyMatch(m -> m.isAnnotationPresent(org.junit.jupiter.api.Test.class));

            if (hasTest) {
                testClasses.add(clazz);
                System.out.println("Classe de teste encontrada: " + className);
            }
        }

        System.out.println("Classes carregadas em memória:");
        allBytes.keySet().forEach(System.out::println);

        System.out.println("Com as classes normais");
        
        
        LauncherDiscoveryRequestBuilder builder = LauncherDiscoveryRequestBuilder.request();

        for (Class<?> testClass : testClasses) {
            builder.selectors(DiscoverySelectors.selectClass(testClass));
        }

        LauncherDiscoveryRequest request = builder.build();



        Thread.currentThread().setContextClassLoader(loader);

        Launcher launcher = LauncherFactory.create();
        SummaryGeneratingListener listener = new SummaryGeneratingListener();
        launcher.registerTestExecutionListeners(listener);

        launcher.execute(request);

        var summary = listener.getSummary();
        System.out.println("Total tests: " + summary.getTestsFoundCount());
        System.out.println("Succeeded: " + summary.getTestsSucceededCount());
        System.out.println("Failed: " + summary.getTestsFailedCount());
        summary.getFailures().forEach(f ->
            System.out.println("Failed test: " + f.getTestIdentifier().getDisplayName() + " -> " + f.getException())
        );

    }

    private void loadMutantInMemory() throws ClassNotFoundException {
        for (AnnotationMutationPoint mutation: this.mutantGeneration.getMutants()){
            MutantClassLoader mutantClassLoader = new MutantClassLoader(mutation.getTargetElement().name.replace('/', '.'), mutation.getBytes());
            mutantClassLoader.loadClass(mutation.getTargetElement().name.replace('/', '.'));
            runTest.runAllTests(mutantClassLoader);
        }
    }


    

  

  public List<AnnotationMutationPoint> getMutants() {
    return this.mutantGeneration.getMutants();
  }

  public void setMutants(List<AnnotationMutationPoint> mutants) {
    this.mutantGeneration.setMutants(mutants);
  }

    

}
