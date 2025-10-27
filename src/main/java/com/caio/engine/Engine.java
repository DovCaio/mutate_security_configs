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

    private List<AnnotationMutationPoint> amps;
    private List<AnnotationMutationPoint> mutants;


    public Engine(List<AnnotationMutationPoint> amps) {
        this.amps = amps;
        this.mutants = new ArrayList<>();
    }

    public void start(List<AnnotationMutationPoint> allClasses) throws Exception {
        createMutants();
        loadAllInMemory(allClasses);
        loadMutantInMemory();
    }

    public void createMutants() throws Exception {
        for (AnnotationMutationPoint amp : amps) {
            String mutate = mutateValue(amp.getValues());
            this.mutants.add(createMutant(amp, mutate));
        }
    }



    private String mutateValue (List<Object> values) {
        String mutateOperator = "";
        Pattern pattern = Pattern.compile("'([^']*)'"); //Dessa forma ele vai mutar todos já, porém talvez fosse interessante a possibilidade de ser as aspas duas dentro das simples
        for (int i = 0; i < values.size(); i += 2) {
            String key = (String) values.get(i);

            if (key.equals("value")) {
                String value = (String) values.get(i + 1);
                Matcher matcher = pattern.matcher(value);
                if (matcher.find()) {
                    String insideQuotes = matcher.group(1);
                    mutateOperator = value.replace(insideQuotes, "NO_" + insideQuotes);
                }
            }
        }

        return  mutateOperator;
    }


    private AnnotationMutationPoint createMutant(AnnotationMutationPoint amp, String novoValor) throws Exception {
        //Dá para dar uma boa melhorada nesse código
        List<Object> values = amp.getValues();
        List<Object> valuesMutant = new ArrayList<>(); //deve dar para reaproveitar esse bloco de alguma forma
        if (values != null) {
            for (int i = 0; i < values.size(); i += 2) {
                String key = (String) values.get(i);
                String value = (String) values.get(i);
                if (key.equals("value")) {
                    value = novoValor;
                }
                valuesMutant.add(key);
                valuesMutant.add(value);
            }
        }

        ClassNode classNode = ClassNodeCloner.cloneClassNode(amp.getTargetElement()); //gambiarra, nem sei como copiar objetos em java

        AnnotationMutationPoint mutante = new AnnotationMutationPoint(
                amp.getTargetType(),
                amp.getOwnerClass(),
                amp.getAnnotationDesc(),
                classNode,
                valuesMutant
        );

        byte[] mutateBytes = new byte[0];


        if (mutante.getTargetType() == AnnotationMutationPoint.TargetType.METHOD) { //Dá para criar métodos auxiliares, tem muito aninhamento
            for (MethodNode m : mutante.getTargetElement().methods) {
                if (mutante.getMethod() != null && m.name.equals(mutante.getMethod().name) && m.visibleAnnotations != null) {
                    for (AnnotationNode ann : m.visibleAnnotations) {
                        if (ann.desc.equals(mutante.getAnnotationDesc())) {
                            for (int i = 0; i < ann.values.size(); i++) {
                                Object v = ann.values.get(i);
                                if (v instanceof String s && s.equals("value")) {
                                    ann.values.set(i + 1, novoValor);
                                }
                            }
                        }
                    }
                }
            }

            mutateBytes = generateMutatedClassBytes(mutante.getTargetElement());


        }else {
            // Mutação da annotation da classe
        }

        mutante.setBytes(mutateBytes);

        return mutante;
    }
    

    private  byte[] generateMutatedClassBytes(ClassNode classNode) {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        classNode.accept(cw);
        return cw.toByteArray();
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
        
        
        

    }

    private void loadMutantInMemory() throws ClassNotFoundException {
        for (AnnotationMutationPoint mutation: mutants){
            MutantClassLoader mutantClassLoader = new MutantClassLoader(mutation.getTargetElement().name.replace('/', '.'), mutation.getBytes());
            mutantClassLoader.loadClass(mutation.getTargetElement().name.replace('/', '.'));
            runAllTests(mutantClassLoader);
        }
    }


    

  private void runAllTests(ClassLoader loader) {

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

  public List<AnnotationMutationPoint> getMutants() {
    return mutants;
  }

  public void setMutants(List<AnnotationMutationPoint> mutants) {
    this.mutants = mutants;
  }

    

}
