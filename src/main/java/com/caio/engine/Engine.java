package com.caio.engine;

import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
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
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import java.util.List;

public class Engine {

    private List<AnnotationMutationPoint> amps;
    private List<AnnotationMutationPoint> mutants;


    public Engine(List<AnnotationMutationPoint> amps) {
        this.amps = amps;
        this.mutants = new ArrayList<>();
    }

    public void start() throws Exception {
        createMutants();
        loadInMemory();
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


    private void loadInMemory() throws ClassNotFoundException {
        for (AnnotationMutationPoint mutation: mutants){
            MutantClassLoader mutantClassLoader = new MutantClassLoader(mutation.getTargetElement().name.replace('/', '.'), mutation.getBytes());
            mutantClassLoader.loadClass(mutation.getTargetElement().name.replace('/', '.'));
            runTest(mutantClassLoader);

        }
    }

    private void runTest(MutantClassLoader mutantClassLoader) { //Vai precissar carergar as classes mutadas para que isso funcione
        // Define o classloader atual da thread
        Thread.currentThread().setContextClassLoader(mutantClassLoader);

        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(DiscoverySelectors.selectPackage("pk.habsoft.demo.estore"))
                .build();

        Launcher launcher = LauncherFactory.create();
        SummaryGeneratingListener listener = new SummaryGeneratingListener();
        launcher.registerTestExecutionListeners(listener);

        launcher.execute(request);

        TestExecutionSummary summary = listener.getSummary();

        System.out.println("Total tests: " + summary.getTestsFoundCount());
        System.out.println("Succeeded: " + summary.getTestsSucceededCount());
        System.out.println("Failed: " + summary.getTestsFailedCount());
        summary.getFailures().forEach(f ->
                System.out.println("Failed test: " + f.getTestIdentifier().getDisplayName() + " -> " + f.getException())
        );
    }

    private boolean runAllTests() throws IOException, InterruptedException {
        ProcessBuilder pb  = new ProcessBuilder("mvn", "test", "-q");
        pb.redirectErrorStream(true);
        Process process = pb.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        int exitCode = process.waitFor();

        return exitCode == 0; //Nesse caso, ele deveria garantir que a mesma quantidade de testes estava a passar, não necessariamente todos os testes vão estar passando né?
    }

    public List<AnnotationMutationPoint> getMutants() {
        return mutants;
    }
}
