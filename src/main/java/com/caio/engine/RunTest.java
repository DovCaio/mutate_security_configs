package com.caio.engine;

import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

public class RunTest {

    public RunTest(){

    }

    public void runAllTests(ClassLoader loader) {

    Thread.currentThread().setContextClassLoader(loader);

    LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
            .selectors(DiscoverySelectors.selectPackage("pk.habsoft.demo.estore"))
            .build();

    Launcher launcher = LauncherFactory.create();
    SummaryGeneratingListener listener = new SummaryGeneratingListener();
    launcher.registerTestExecutionListeners(listener);

    launcher.execute(request);

    TestExecutionSummary summary = listener.getSummary();

    System.out.println("=== RESULTADOS DOS TESTES ===");
    System.out.println("Total tests: " + summary.getTestsFoundCount());
    System.out.println("Succeeded: " + summary.getTestsSucceededCount());
    System.out.println("Failed: " + summary.getTestsFailedCount());
    summary.getFailures().forEach(f ->
            System.out.println("❌ " + f.getTestIdentifier().getDisplayName() + " -> " + f.getException())
    );
    }
    
}
