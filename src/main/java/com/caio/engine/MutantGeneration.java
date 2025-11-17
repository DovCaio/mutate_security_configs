package com.caio.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import com.caio.exceptions.NoOnePossibleMutant;
import com.caio.exceptions.TypeOfAnnotationPointMutationNonDetected;
import com.caio.models.AnnotationMutationPoint;
import com.caio.utli.ClassNodeCloner;

public class MutantGeneration {

    private List<AnnotationMutationPoint> amps;
    private List<AnnotationMutationPoint> mutants;
    private final String regex = "(?:hasAuthority|hasRole)\\(['\"]([^'\"]+)['\"]\\)";

    public MutantGeneration(List<AnnotationMutationPoint> amps) {
        this.amps = amps;
        this.mutants = new ArrayList<>();
    }

    public void createMutants() throws Exception {
        for (AnnotationMutationPoint amp : amps) {
            String mutate = mutateValue(amp.getValues());
            if (!mutate.equals(""))
                this.mutants.add(createMutant(amp, mutate));
        }
        if (this.mutants.isEmpty())
            throw new NoOnePossibleMutant();
    }

    private String mutateValue(List<Object> values) {
        String mutateOperator = "";
        Pattern pattern = Pattern.compile(regex);
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
        return mutateOperator;
    }

    private AnnotationMutationPoint createMutant(AnnotationMutationPoint amp, String novoValor) throws Exception {
        List<Object> mutatedValues = mutateAnnotationValues(amp.getValues(), novoValor); // Seria muito bom se o
                                                                                         // amp.getValues() retornase um
                                                                                         // map, ao invés de uma list
                                                                                         // que de certa forma
                                                                                         // representa um. Dá forma que
                                                                                         // tá complicou muito o
                                                                                         // entendimento
        ClassNode clonedClassNode = cloneClassNode(amp.getTargetElement());

        AnnotationMutationPoint mutant = AnnotationMutationPoint.forMethod(
                amp.getOwnerClass(),
                amp.getAnnotationDesc(),
                clonedClassNode,
                mutatedValues,
                null,
                amp.getMethod());

        byte[] mutatedBytes = switch (mutant.getTargetType()) {
            case METHOD -> mutateMethodAnnotation(mutant, novoValor);
            case CLASS -> mutateClassAnnotation(mutant, novoValor);
            default -> throw new TypeOfAnnotationPointMutationNonDetected();
        };

        mutant.setBytes(mutatedBytes);
        return mutant;
    }

    private List<Object> mutateAnnotationValues(List<Object> originalValues, String novoValor) {
        if (originalValues == null)
            return new ArrayList<>();

        List<Object> mutated = new ArrayList<>();
        for (int i = 0; i < originalValues.size(); i += 2) {
            String key = (String) originalValues.get(i);
            String value = (String) originalValues.get(i + 1);

            if ("value".equals(key)) {
                value = novoValor;
            }

            mutated.add(key);
            mutated.add(value);
        }
        return mutated;
    }

    private ClassNode cloneClassNode(ClassNode original) throws Exception {
        return ClassNodeCloner.cloneClassNode(original);
    }

    private byte[] mutateMethodAnnotation(AnnotationMutationPoint mutant, String novoValor) throws Exception {
        for (MethodNode method : mutant.getTargetElement().methods) {
            if (shouldMutateMethod(mutant, method)) { // Dá para recuperar nome do metodo e da classe, para especificar
                                                      // no relatório
                mutateAnnotationValue(method.visibleAnnotations, mutant.getAnnotationDesc(), novoValor);
            }
        }
        return generateMutatedClassBytes(mutant.getTargetElement());
    }

    private boolean shouldMutateMethod(AnnotationMutationPoint mutant, MethodNode method) {
        return mutant.getMethod() != null
                && method.name.equals(mutant.getMethod().name)
                && method.visibleAnnotations != null;
    }

    private void mutateAnnotationValue(List<AnnotationNode> annotations, String targetDesc, String novoValor) {
        for (AnnotationNode annotation : annotations) {
            if (annotation != null && annotation.desc.equals(targetDesc) && annotation.values != null) {

                List<Object> values = annotation.values;

                for (int i = 0; i < values.size() - 1; i += 2) {
                    String key = (String) values.get(i);
                    Object oldValue = values.get(i + 1);

                    // Apenas substitui se for String (PreAuthorize, etc)
                    if (oldValue instanceof String) {
                        values.set(i + 1, novoValor);
                    }

                    // Se fosse array, enum, class, etc você trataria aqui
                }
            }
        }
    }

    private byte[] mutateClassAnnotation(AnnotationMutationPoint mutant, String novoValor) throws Exception {
        // (TODO) Lógica futura para mutação de annotation de classe
        return new byte[0];
    }

    private byte[] generateMutatedClassBytes(ClassNode classNode) {
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
