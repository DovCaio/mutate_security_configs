package com.caio.engine;

import java.util.ArrayList;
import java.util.List;

import com.caio.exceptions.NoOnePossibleMutant;
import com.caio.models.AnnotationMutationPoint;

import com.caio.engine.mutant.MutantMaker;

public class MutantGeneration {

    private List<AnnotationMutationPoint> amps;
    private List<AnnotationMutationPoint> mutants;

    public MutantGeneration(List<AnnotationMutationPoint> amps) {
        this.amps = amps;
        this.mutants = new ArrayList<>();
    }

    public void createMutants(List<String> roles, List<String> authorities) throws Exception {
        for (AnnotationMutationPoint amp : amps) {
            MutantMaker mutantGeneration  = new MutantMaker(amp.getOriginalValue(),roles, authorities);
            List<String> mutates = mutantGeneration.genAllMutants();
            if (!mutates.isEmpty()){

                List<AnnotationMutationPoint> aux = new ArrayList<>();

                mutates.stream().forEach(mutant -> {
                    try { //Talvez esse try/catch não seja mais necessário, nem o aux
                        if(!mutant.equals(""))
                            aux.add(createMutant(amp, mutant));
                    } catch (Exception e) {
                        System.out.println("Não foi possível de adicionar o mutant " + mutant);
                    }
                });

                if(!aux.isEmpty())
                    this.mutants.addAll(aux);
                }
        }
        if (this.mutants.isEmpty())
            throw new NoOnePossibleMutant();

    }
    
    private AnnotationMutationPoint createMutant(AnnotationMutationPoint amp, String novoValor) throws Exception {
        AnnotationMutationPoint mutant = new AnnotationMutationPoint(
            amp.getPackageName(),
            amp.getClassName(),
            amp.getMethodName(),
            amp.getOriginalValue(),
            novoValor,
            amp.getTargetType(),
            amp.getFilePath(),
            amp.getLineNumber()
        );

        return mutant;
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