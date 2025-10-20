package com.caio.engine;

import com.caio.models.AnnotationMutationPoint;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

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

    public void createMutants() {
        for (AnnotationMutationPoint amp : amps) {
            String mutate = mutateValue(amp.getValues());
            this.mutants.add(applyMutant(amp, mutate));
        }
    }

    public void start() {

    }

    private String mutateValue (List<Object> values) {
        String mutateOperator = "";
        Pattern pattern = Pattern.compile("'([^']*)'");
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

    private AnnotationMutationPoint applyMutant(AnnotationMutationPoint amp, String novoValor) {

        List<Object> values = amp.getValues();
        List<Object> valuesMutant = new ArrayList<Object>();
        if (values != null) {
            for (int i = 0; i < values.size(); i += 2) {
                String key = (String) values.get(i);
                String value = (String) values.get(i);
                if (key.equals("value")) {
                    value = novoValor; // altera o valor da annotation
                }
                valuesMutant.add(key);
                valuesMutant.add(value);
            }
        }

        return new AnnotationMutationPoint(
                amp.getTargetType(),
                amp.getOwnerClass(),
                amp.getAnnotationDesc(),
                amp.getTargetElement(),
                valuesMutant
        );
    }

    private static byte[] generateMutatedClassBytes(ClassNode classNode) {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        classNode.accept(cw);
        return cw.toByteArray();
    }

    public List<AnnotationMutationPoint> getMutants() {
        return mutants;
    }
}
