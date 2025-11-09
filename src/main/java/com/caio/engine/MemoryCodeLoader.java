package com.caio.engine;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.caio.exceptions.NoOneClasseFinded;
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

        for (AnnotationMutationPoint c : this.mainClasses) {
            this.allBytes.put(c.getTargetElement().name.replace('/', '.'), c.getBytes());
        }

        for (AnnotationMutationPoint c : this.testClasses) {
            this.allBytes.put(c.getTargetElement().name.replace('/', '.'), c.getBytes());
        }

    }

    public void verifyTestsPassing() throws IOException{

        AllClassesClassLoader loader = new AllClassesClassLoader(this.allBytes, dependenciesClassLoader);
        factoreVerification(loader);
        runTest.executeTestForVerification(loader).toString();
        
    }

    public void loadMutantInMemory(List<AnnotationMutationPoint> mutants) throws ClassNotFoundException {

        if (this.allBytes.isEmpty()) throw new NoOneClasseFinded("Nenhuma classe encotrada para a aplicação dos mutantes.");

        Map<String, byte[]> applyMutantMap = this.allBytes;
        for (AnnotationMutationPoint mutation : mutants) {
            String className = mutation.getTargetElement().name.replace("/", "."); //Bem que esse AnnotationMutationPoint podia ter um getName que desse um retorno bonitinho já
            byte[] mutadedClasse = mutation.getBytes();


            byte[] originalClass = applyMutantMap.get(className);
            applyMutantMap.put(className, mutadedClasse);

            AllClassesClassLoader allClassesClassLoader = new AllClassesClassLoader(applyMutantMap, dependenciesClassLoader);
            runTest.executeTestForMutation(allClassesClassLoader);

            applyMutantMap.put(className, originalClass);

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
