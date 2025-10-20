package com.caio.engine;

import com.caio.models.AnnotationMutationPoint;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import java.util.List;

public class Engine {

    private List<AnnotationMutationPoint> amps;

    public Engine(List<AnnotationMutationPoint> amps) {
        this.amps = amps;
    }

    public void start() {
        for (AnnotationMutationPoint amp : amps) {
            String mutate = mutateValue(amp.getValues());
            //applyMutation(amp, mutate);
        }
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
                    System.out.print(mutateOperator);
                }
            }
        }

        return  mutateOperator;
    }

    private void applyMutation(AnnotationMutationPoint amp, String novoValor) {
        List<Object> values = amp.getValues();
        if (values != null) {
            for (int i = 0; i < values.size(); i += 2) {
                String key = (String) values.get(i);
                if (key.equals("value")) {
                    values.set(i + 1, novoValor); // altera o valor da annotation
                }
            }
        }
    }

    private static byte[] generateMutatedClassBytes(ClassNode classNode) {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        classNode.accept(cw);
        return cw.toByteArray();
    }

}
