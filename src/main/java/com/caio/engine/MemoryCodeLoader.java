package com.caio.engine;

import static org.junit.jupiter.api.DynamicTest.stream;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;

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
    private List<URL> dependenciesJarURLs;
    private URLClassLoader dependenciesClassLoader;
    private Map<String, byte[]> allBytes = new HashMap<>();

    public MemoryCodeLoader(List<AnnotationMutationPoint> mainClasses, List<AnnotationMutationPoint> testClasses,
            List<URL> dependenciesJarURLs) {
        this.mainClasses = mainClasses;
        this.testClasses = testClasses;
        this.runTest = new RunTest();
        this.dependenciesJarURLs = dependenciesJarURLs;
        dependenciesClassLoader = new URLClassLoader(
                dependenciesJarURLs.toArray(new URL[0]), ClassLoader.getSystemClassLoader());

    }

    public void loadAllInMemory() throws ClassNotFoundException, IllegalAccessException, InvocationTargetException,
            InstantiationException, IllegalArgumentException, NoSuchMethodException, SecurityException,
            URISyntaxException, IOException {

        loadDependencieInMemory(); //Isso está aparentando não fazer nada

        for (AnnotationMutationPoint c : this.mainClasses) {
            this.allBytes.put(c.getTargetElement().name.replace('/', '.'), c.getBytes());
        }

        for (AnnotationMutationPoint c : this.testClasses) {
            this.allBytes.put(c.getTargetElement().name.replace('/', '.'), c.getBytes());
        }


        this.allBytes.forEach((k, v) -> System.out.println(k)); 

        NonMutantClassLoader loader = new NonMutantClassLoader(this.allBytes);


        LauncherDiscoveryRequestBuilder builder = LauncherDiscoveryRequestBuilder.request();

        for (AnnotationMutationPoint c : this.testClasses) {
            Class<?> testClass = loader.findClass(c.getTargetElement().name.replace('/', '.'));

            builder.selectors(DiscoverySelectors.selectClass(testClass));

        }


       
        LauncherDiscoveryRequest request = builder.build();

        // NÃO troque o classloader da thread
        // Thread.currentThread().setContextClassLoader(loader);

        Launcher launcher = LauncherFactory.create();
        SummaryGeneratingListener listener = new SummaryGeneratingListener();

        launcher.registerTestExecutionListeners(listener);
        launcher.execute(request);

        var summary = listener.getSummary();
        System.out.println("Total tests: " + summary.getTestsFoundCount());
        System.out.println("Succeeded: " + summary.getTestsSucceededCount());
        System.out.println("Failed: " + summary.getTestsFailedCount());
        summary.getFailures().forEach(f -> System.out
                .println("Failed test: " + f.getTestIdentifier().getDisplayName() + " -> " + f.getException()));

    }

    public void loadMutantInMemory(List<AnnotationMutationPoint> mutants) throws ClassNotFoundException {
        for (AnnotationMutationPoint mutation : mutants) {
            MutantClassLoader mutantClassLoader = new MutantClassLoader(
                    mutation.getTargetElement().name.replace('/', '.'), mutation.getBytes());
            mutantClassLoader.loadClass(mutation.getTargetElement().name.replace('/', '.'));
            runTest.runAllTests(mutantClassLoader);
        }
    }

    private void loadDependencieInMemory() throws URISyntaxException, IOException {
        URLClassLoader dependenciesClassLoader = new URLClassLoader(
            dependenciesJarURLs.toArray(new URL[0]),
            Thread.currentThread().getContextClassLoader()
        );

        Thread.currentThread().setContextClassLoader(dependenciesClassLoader);
    }

}
