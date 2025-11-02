package com.caio.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import com.caio.models.AnnotationMutationPoint;
import com.caio.utli.ClassNodeCloner;

public class MutantGeneration {

    private List<AnnotationMutationPoint> amps;
    private List<AnnotationMutationPoint> mutants;
    private final String regex = "\"'([^']*)'\"";


    public MutantGeneration(List<AnnotationMutationPoint> amps){
        this.amps = amps;
        this.mutants = new ArrayList<>();
    }

     public void createMutants() throws Exception {
        for (AnnotationMutationPoint amp : amps) {
            String mutate = mutateValue(amp.getValues());
            this.mutants.add(createMutant(amp, mutate));
        }
    }



    private String mutateValue (List<Object> values) {
        String mutateOperator = "";
        Pattern pattern = Pattern.compile(regex); //Dessa forma ele vai mutar todos já, porém talvez fosse interessante a possibilidade de ser as aspas duas dentro das simples
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

    public List<AnnotationMutationPoint> getAmps() {
        return amps;
    }

    public void setAmps(List<AnnotationMutationPoint> amps) {
        this.amps = amps;
    }

    public List<AnnotationMutationPoint> getMutants() {
        return mutants;
    }

    public void setMutants(List<AnnotationMutationPoint> mutants) {
        this.mutants = mutants;
    }


    
    
}
