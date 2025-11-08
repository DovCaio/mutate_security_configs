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
        dependenciesClassLoader = new URLClassLoader( //Muito importante
                dependenciesJarURLs.toArray(new URL[0]), ClassLoader.getSystemClassLoader());

    }

    public void verifyTestsPassing() throws IOException{

        for (AnnotationMutationPoint c : this.mainClasses) {
            this.allBytes.put(c.getTargetElement().name.replace('/', '.'), c.getBytes());
        }

        for (AnnotationMutationPoint c : this.testClasses) {
            this.allBytes.put(c.getTargetElement().name.replace('/', '.'), c.getBytes());
        }

        AllClassesClassLoader loader = new AllClassesClassLoader(this.allBytes, dependenciesClassLoader);
        factoreVerification(loader);

        System.out.println(runTest.executeTestForVerification(loader).toString());
        
    }

    public void loadMutantInMemory(List<AnnotationMutationPoint> mutants) throws ClassNotFoundException {
        for (AnnotationMutationPoint mutation : mutants) {
            //MutantClassLoader mutantClassLoader = new MutantClassLoader(mutation.getBytes());
            //mutantClassLoader.loadClass(mutation.getTargetElement().name.replace('/', '.'));
            //runTest.executeTestForMutation(mutantClassLoader);
        }
    }

    private void factoreVerification(AllClassesClassLoader classLoader) throws IOException {

        Enumeration<URL> factories = classLoader.getResources("META-INF/spring.factories");
        if (!factories.hasMoreElements()) {
            throw new SpringbootContextNotFound(
                    "Não foi possível encontrar as factories para a inicialização do spring boot");
        }

    }

}
