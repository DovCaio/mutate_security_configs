package com.caio.engine;


import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import com.caio.exceptions.SpringbootContextNotFound;
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

        for ( URL u : dependenciesJarURLs) {
            System.out.println(u.toString());
        }

       NonMutantClassLoader loader = new NonMutantClassLoader(this.allBytes, dependenciesClassLoader);

        factoreVerification(loader);
        Thread.currentThread().setContextClassLoader(loader);
        JUnitCore junit = new JUnitCore();

        for (AnnotationMutationPoint c : this.testClasses) {
            String className = c.getTargetElement().name.replace('/', '.');
            System.out.println("\n-----------------------------------------");
            System.out.println("🧪 Executando: " + className);

            try {
                Class<?> testClass = loader.loadClass(className);
                Result result = junit.run(testClass);

                for (Failure f : result.getFailures()) {
                    System.out.println("❌ " + f.getTestHeader());
                    Throwable ex = f.getException();
                    if (ex != null) {
                        System.out.println("   ➤ Causa: " + ex.getClass().getName() + " - " + ex.getMessage());

                        StringWriter sw = new StringWriter();
                        ex.printStackTrace(new PrintWriter(sw));
                        String trace = sw.toString();
                        Arrays.stream(trace.split("\n"))
                            .limit(15)
                            .forEach(line -> System.out.println("     " + line));
                    }
                }
                System.out.println("✅ Executados: " + result.getRunCount());

                // Opcional: listar métodos de teste
                Arrays.stream(testClass.getDeclaredMethods()).forEach(m -> {
                    if (m.isAnnotationPresent(org.junit.Test.class)) {
                        System.out.println("Method: " + m.getName() + ", Annotations: " + Arrays.toString(m.getAnnotations()));
                    }
                });

            } catch (Throwable t) {
                System.out.println("💥 Falha ao carregar classe " + className + ": " + t.getMessage());
                t.printStackTrace(System.out);
            }
        }

    }

    public void loadMutantInMemory(List<AnnotationMutationPoint> mutants) throws ClassNotFoundException {
        for (AnnotationMutationPoint mutation : mutants) {
            MutantClassLoader mutantClassLoader = new MutantClassLoader(
                    mutation.getTargetElement().name.replace('/', '.'), mutation.getBytes());
            mutantClassLoader.loadClass(mutation.getTargetElement().name.replace('/', '.'));
            runTest.runAllTests(mutantClassLoader);
        }
    }

    private void factoreVerification(NonMutantClassLoader classLoader) throws IOException {

        Enumeration<URL> factories = classLoader.getResources("META-INF/spring.factories");
        if (!factories.hasMoreElements()) {
            throw new SpringbootContextNotFound("Não foi possível encontrar as factories para a inicialização do spring boo");            
        }

    }

}
