package com.caio.engine;


import com.caio.models.AnnotationMutationPoint;



import java.util.List;

public class Engine {

    
    private MutantGeneration mutantGeneration;
    private MemoryCodeLoader memoryCodeLoader;


    public Engine(List<AnnotationMutationPoint> amps,List<AnnotationMutationPoint> mainClasses, List<AnnotationMutationPoint> testClasses) {
        
        this.mutantGeneration = new MutantGeneration(amps);
        this.memoryCodeLoader = new MemoryCodeLoader(mainClasses, testClasses);
    }

    public void start() throws Exception {
        this.mutantGeneration.createMutants();
        this.memoryCodeLoader.loadAllInMemory();
        this.memoryCodeLoader.loadMutantInMemory(this.mutantGeneration.getMutants());
    }

   

    

    public List<AnnotationMutationPoint> getMutants() {
        return this.mutantGeneration.getMutants();
    }

    public void setMutants(List<AnnotationMutationPoint> mutants) {
        this.mutantGeneration.setMutants(mutants);
    }

    

}
