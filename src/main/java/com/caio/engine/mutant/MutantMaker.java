package com.caio.engine.mutant;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import com.caio.exceptions.NoOnePossibleMutant;

public class MutantMaker {
    private final String regexSimpleCases = "(?:hasAuthority|hasRole)\\(['\"]([^'\"]+)['\"]\\)";
    private final String regexCompostCases = "(?:hasAnyAuthority|hasAnyRole)\\(['\"]([^'\"]+)['\"]\\)";

    private final String regexPermitAll = "(?:permitAll())";
    private final String regexDenyAll = "(?:denyAll)";


    private String key;
    private String value;

    public MutantMaker(List<Object> values){

        for (int i = 0; i < values.size(); i += 2 ) {
            String key = (String) values.get(i);
            if (key.equals("value")){
                this.key = key;
                this.value = (String) values.get(i + 1);
            }

        }
    }

    public List<String> genAllMutants() throws Exception{
        List<String> result = new ArrayList<>();

        Pattern patternSimpleCase = Pattern.compile(regexSimpleCases);
        Pattern patternCompostCase = Pattern.compile(regexCompostCases);
        Pattern patternPermitAll = Pattern.compile(regexPermitAll);
        Pattern patternDenyAll = Pattern.compile(regexDenyAll);

        Matcher matcherSimpleCase = patternSimpleCase.matcher(this.value);
        Matcher matcherCompostCase = patternCompostCase.matcher(this.value);
        Matcher matcherPermitAllCase = patternPermitAll.matcher(this.value);
        Matcher matcherDenyCase = patternDenyAll.matcher(this.value);

        if (!matcherPermitAllCase.find() && !matcherDenyCase.find()){
            result.add("permitAll()");
            result.add("denyAll");
        }

        if (matcherSimpleCase.find()){
            result.add(mutateSimpleValue(matcherSimpleCase));
        }else if (matcherCompostCase.find()){
            System.out.println("TODO");
        
        } else if (matcherPermitAllCase.find()){
            System.out.print("TODO");
        }else if(matcherDenyCase.find()) {
            System.out.print("TODO");

        }
        else {
            result.add("");
        }

        return result;
    }




    private String mutateSimpleValue(Matcher matcher) {
        String mutateOperator = "";

        String insideQuotes = matcher.group(1);
        mutateOperator = value.replace(insideQuotes, "NO_" + insideQuotes);

        return mutateOperator;
    }

}
