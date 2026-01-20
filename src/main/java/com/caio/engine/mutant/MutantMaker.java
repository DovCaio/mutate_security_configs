package com.caio.engine.mutant;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MutantMaker {
    private final String regexSimpleCases = "(?:hasAuthority|hasRole)\\(['\"]([^'\"]+)['\"]\\)";
    private final String regexCompostCases = "(?:hasAnyAuthority|hasAnyRole)\\(['\"]([^'\"]+)['\"]\\)";

    private final String regexPermitAll = "(?:permitAll())";
    private final String regexDenyAll = "(?:denyAll)";


    private String value;

    public MutantMaker(String value){

        this.value = value;
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

        boolean hasSimple = matcherSimpleCase.find();
        boolean hasCompost = matcherCompostCase.find();
        boolean hasPermitAll = matcherPermitAllCase.find();
        boolean hasDenyAll = matcherDenyCase.find();

        if (!hasDenyAll && !hasPermitAll){
            result.add("permitAll()");
            result.add("denyAll");
        }

        if (hasSimple){
            result.add(mutateSimpleValue(matcherSimpleCase));
        }else if (hasCompost){
            System.out.println("TODO");
        
        } else if (hasPermitAll){
            System.out.print("TODO");
        }else if(hasDenyAll) {
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
