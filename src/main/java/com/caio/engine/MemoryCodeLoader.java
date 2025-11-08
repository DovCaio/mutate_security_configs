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

import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
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

        NonMutantClassLoader loader = new NonMutantClassLoader(this.allBytes, dependenciesClassLoader);
        factoreVerification(loader);
        Thread.currentThread().setContextClassLoader(loader);

        System.out.println("🧩 Total de classes de teste: " + this.testClasses.size());

        for (AnnotationMutationPoint c : this.testClasses) {
            String className = c.getTargetElement().name.replace('/', '.');
            System.out.println("\n-----------------------------------------");
            System.out.println("🧪 Executando: " + className);

            try {
                Class<?> testClass = loader.loadClass(className);

                LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                        .selectors(DiscoverySelectors.selectClass(testClass))
                        .build();

                Launcher launcher = LauncherFactory.create();
                SummaryGeneratingListener listener = new SummaryGeneratingListener();

                launcher.registerTestExecutionListeners(listener);
                launcher.execute(request);

                TestExecutionSummary summary = listener.getSummary();

                System.out.println("✅ Testes encontrados: " + summary.getTestsFoundCount());
                System.out.println("🏁 Testes executados: " + summary.getTestsSucceededCount());
                System.out.println("❌ Falhas: " + summary.getFailures().size());

                for (TestExecutionSummary.Failure failure : summary.getFailures()) {
                    System.out.println("   ➤ " + failure.getTestIdentifier().getDisplayName());
                    Throwable ex = failure.getException();
                    if (ex != null) {
                        System.out.println("     Causa: " + ex.getClass().getName() + " - " + ex.getMessage());

                        StringWriter sw = new StringWriter();
                        ex.printStackTrace(new PrintWriter(sw));
                        Arrays.stream(sw.toString().split("\n"))
                                .forEach(line -> System.out.println("       " + line));
                    }
                }

            } catch (Throwable t) {
                System.out.println("💥 Falha ao carregar/executar " + className + ": " + t.getMessage());
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
            throw new SpringbootContextNotFound(
                    "Não foi possível encontrar as factories para a inicialização do spring boo");
        }

    }

}
