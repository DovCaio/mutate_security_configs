/*package com.caio.engine;

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

    private List<String> classesTest; 
    private List<Class<?>> tests = new java.util.ArrayList<>();

    public MemoryCodeLoader(List<AnnotationMutationPoint> mainClasses, List<AnnotationMutationPoint> testClasses,
            List<URL> dependenciesJarURLs, RunTest runTest, List<String> classesTest) {
        this.mainClasses = mainClasses;
        this.testClasses = testClasses;
        this.runTest = runTest;
        this.dependenciesJarURLs = dependenciesJarURLs;
        dependenciesClassLoader = new URLClassLoader( // Muito importante
                dependenciesJarURLs.toArray(new URL[0]), ClassLoader.getSystemClassLoader());
        this.classesTest = classesTest; 

        for (AnnotationMutationPoint c : this.mainClasses) {
            String className = c.getTargetElement().name.replace('/', '.');
            this.allBytes.put(className, c.getBytes());
        }

        for (AnnotationMutationPoint c : this.testClasses) {
            String className = c.getTargetElement().name.replace('/', '.');
            this.allBytes.put(className, c.getBytes());
        }

    }

    private void findTests(ClassLoader loader) {
        classesTest.stream().forEach(classname -> {
            try {
                Class<?> test = loader.loadClass(classname.replace("/", "."));
                tests.add(test);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
    }

    public void verifyTestsPassing() throws IOException {
        AllClassesClassLoader loader = new AllClassesClassLoader(this.allBytes, dependenciesClassLoader);

        findTests(loader);

        factoreVerification(loader);
        runTest.executeTestForVerification(loader, tests);
    }

    public void loadMutantInMemory(List<AnnotationMutationPoint> mutants) throws ClassNotFoundException {

        if (this.allBytes.isEmpty())
            throw new NoOneClasseFinded("Nenhuma classe encontrada para a aplicação dos mutantes.");

        Map<String, byte[]> applyMutantMap = this.allBytes;
        for (AnnotationMutationPoint mutation : mutants) {
            String className = mutation.getTargetElement().name.replace("/", "."); // Bem que esse
                                                                                   // AnnotationMutationPoint podia ter
                                                                                   // um getName que desse um retorno
                                                                                   // bonitinho já
            byte[] mutadedClasse = mutation.getBytes();

            byte[] originalClass = applyMutantMap.get(className);
            applyMutantMap.put(className, mutadedClasse);

            AllClassesClassLoader allClassesClassLoader = new AllClassesClassLoader(applyMutantMap,
                    dependenciesClassLoader);

            ParamsForTestMutationApresentation params = new ParamsForTestMutationApresentation(
                    mutation.getOwnerClass(),
                    mutation.getMethod().name,
                    mutation.getAnnotationDesc(),
                    "Placeholder",
                    "Placeholder");

            runTest.executeTestForMutation(allClassesClassLoader, tests, params);

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
*/