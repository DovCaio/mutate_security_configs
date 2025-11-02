package com.caio.engine;

import static org.junit.jupiter.api.DynamicTest.stream;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;


import com.caio.models.AnnotationMutationPoint;

public class MemoryCodeLoader {

    private List<AnnotationMutationPoint> mainClasses;
    private List<AnnotationMutationPoint> testClasses;
    private RunTest runTest;
    private List<URL> dependenciesJarURLs;
    private URLClassLoader dependenciesClassLoader;
    private Map<String, byte[]> allBytes = new HashMap<>();

    public MemoryCodeLoader(List<AnnotationMutationPoint> mainClasses, List<AnnotationMutationPoint> testClasses,
            List<URL> dependenciesJarURLs, RunTest runTest) {
        this.mainClasses = mainClasses;
        this.testClasses = testClasses;
        this.runTest = runTest;
        this.dependenciesJarURLs = dependenciesJarURLs;
        dependenciesClassLoader = new URLClassLoader(
                dependenciesJarURLs.toArray(new URL[0]), ClassLoader.getSystemClassLoader());

    }

    public void loadAllInMemory() throws ClassNotFoundException, IllegalAccessException, InvocationTargetException,
            InstantiationException, IllegalArgumentException, NoSuchMethodException, SecurityException,
            URISyntaxException, IOException {

        for (AnnotationMutationPoint c : this.mainClasses) {
            this.allBytes.put(c.getTargetElement().name.replace('/', '.'), c.getBytes());
        }

        for (AnnotationMutationPoint c : this.testClasses) {
            this.allBytes.put(c.getTargetElement().name.replace('/', '.'), c.getBytes());
        }

       NonMutantClassLoader loader = new NonMutantClassLoader(this.allBytes, dependenciesClassLoader);

        

        // Troca antes da descoberta
        Thread.currentThread().setContextClassLoader(loader);

        LauncherDiscoveryRequestBuilder builder = LauncherDiscoveryRequestBuilder.request();

        for (AnnotationMutationPoint c : this.testClasses) {
            Class<?> testClass = loader.loadClass(c.getTargetElement().name.replace('/', '.'));

            System.out.println("Executando: " + testClass.getName());
            JUnitCore junit = new JUnitCore();
            Result result = junit.run(testClass);

            for (Failure f : result.getFailures()) {
                System.out.println("❌ " + f);
            }
            System.out.println("✅ Executados: " + result.getRunCount());

            Arrays.stream(testClass.getDeclaredMethods()).forEach(m -> {
            if (m.isAnnotationPresent(org.junit.Test.class)) {
                System.out.println("Method: " + m.getName() + ", Annotations: " + Arrays.toString(m.getAnnotations()));
            }
        });
        }



        
    LauncherDiscoveryRequest request = builder.build();
    Launcher launcher = LauncherFactory.create();
    SummaryGeneratingListener listener = new SummaryGeneratingListener();
    launcher.registerTestExecutionListeners(listener);

    launcher.execute(request);

    var summary = listener.getSummary();
    System.out.println("=== RESULTADOS DOS TESTES ===");
    System.out.println("Total tests: " + summary.getTestsFoundCount());
    System.out.println("Succeeded: " + summary.getTestsSucceededCount());
    System.out.println("Failed: " + summary.getTestsFailedCount());
    summary.getFailures().forEach(f ->
        System.out.println("Failed test: " + f.getTestIdentifier().getDisplayName() + " -> " + f.getException())
    );

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
        for (URL jarUrl : dependenciesJarURLs) {
            Path jarPath = Paths.get(jarUrl.toURI());
            try (JarFile jarFile = new JarFile(jarPath.toFile())) {
                jarFile.stream()
                        .filter(e -> e.getName().endsWith(".class"))
                        .forEach(entry -> {
                            try (InputStream in = jarFile.getInputStream(entry)) {
                                byte[] bytes = in.readAllBytes();
                                String className = entry.getName()
                                        .replace('/', '.')
                                        .replace(".class", "");
                                allBytes.put(className, bytes);
                            } catch (IOException ex) {
                                System.err.println("Erro lendo classe: " + entry.getName());
                            }
                        });
            } catch (Exception e) {
                System.err.println("Erro lendo JAR: " + jarUrl);
                e.printStackTrace();
            }
        }
    }

}
