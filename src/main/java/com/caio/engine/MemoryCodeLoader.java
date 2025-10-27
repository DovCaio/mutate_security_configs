package com.caio.engine;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;

import com.caio.models.AnnotationMutationPoint;

public class MemoryCodeLoader {

    private List<AnnotationMutationPoint> mainClasses;
    private List<AnnotationMutationPoint> testClasses;
    private RunTest runTest;


    public MemoryCodeLoader(List<AnnotationMutationPoint> mainClasses, List<AnnotationMutationPoint> testClasses) {
        this.mainClasses = mainClasses;
        this.testClasses = testClasses;
        this.runTest = new RunTest();
        
    }
    
    public void loadAllInMemory() throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, InstantiationException, IllegalArgumentException, NoSuchMethodException, SecurityException {
        Map<String, byte[]> allBytes = new HashMap<>();
        for (AnnotationMutationPoint c : this.mainClasses) {
            allBytes.put(c.getTargetElement().name.replace('/', '.'), c.getBytes());
        }

        NonMutantClassLoader loader = new NonMutantClassLoader(allBytes);


        System.out.println("Classes carregadas em memória:");
        allBytes.keySet().forEach(System.out::println);

        System.out.println("Com as classes normais");
        
        
        LauncherDiscoveryRequestBuilder builder = LauncherDiscoveryRequestBuilder.request();

        //for (Class<?> testClass : testClasses) {
        //    builder.selectors(DiscoverySelectors.selectClass(testClass));
        //}

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

    public void loadMutantInMemory(List<AnnotationMutationPoint> mutants) throws ClassNotFoundException {
        for (AnnotationMutationPoint mutation: mutants){
            MutantClassLoader mutantClassLoader = new MutantClassLoader(mutation.getTargetElement().name.replace('/', '.'), mutation.getBytes());
            mutantClassLoader.loadClass(mutation.getTargetElement().name.replace('/', '.'));
            runTest.runAllTests(mutantClassLoader);
        }
    }
  

}
