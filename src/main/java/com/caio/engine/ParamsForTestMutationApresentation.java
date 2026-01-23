package com.caio.engine;

public class ParamsForTestMutationApresentation {
    public String packageName; //Sempre obrigatório
    public String method; //Pode ser nulo se a mutação for em nível de classe
    public String operator;
    public String originalValue;
    public String mutatedValue;
    
    public ParamsForTestMutationApresentation(String packageName, String method, String operator, String originalValue,
            String mutatedValue) {
        this.packageName = packageName;
        this.method = method;
        this.operator = operator;
        this.originalValue = originalValue;
        this.mutatedValue = mutatedValue;
    }
    
    
}
