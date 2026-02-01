    package com.caio.engine.mutant;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MutantMaker {
    private final String regexSimpleCases = "(?:hasAuthority|hasRole)\\(['\"]([^'\"]+)['\"]\\)";
    private final String regexCompositeCases = "(?:hasAnyRole|hasAnyAuthority)\\s*\\(\\s*([^)]*)\\s*\\)";

    private final String regexPermitAll = "(?:permitAll())";
    private final String regexDenyAll = "(?:denyAll)";

    private String value;
    private List<String> roles;
    private List<String> authorities;
    private List<String> rolesAndAuthorities;

    public MutantMaker(String value, List<String> roles, List<String> authorities) {

        this.value = value;
        this.roles = roles;
        this.authorities = authorities;


        this.rolesAndAuthorities = new ArrayList<>();
        rolesAndAuthorities.addAll(roles);
        rolesAndAuthorities.addAll(authorities);


    }

    public List<String> genAllMutants() throws Exception {
        List<String> result = new ArrayList<>();

        Pattern patternSimpleCase = Pattern.compile(regexSimpleCases);
        Pattern patternCompostCase = Pattern.compile(regexCompositeCases);
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

        if (!hasDenyAll && !hasPermitAll) {
            result.add("permitAll()");
            result.add("denyAll");
        }

        if (hasSimple) {
            result.addAll(mutateSimpleValue(matcherSimpleCase));
        } else if (hasCompost) {
            result.addAll(mutateCompositeValue(matcherCompostCase));
        } else if (hasPermitAll) {
            result.addAll(mutePermitAll(matcherPermitAllCase));
        } else if (hasDenyAll) {
            result.addAll(muteDenyAll(matcherDenyCase));

        } else {
            result.add("");
        }

        return result;
    }

    private List<String> mutateSimpleValue(Matcher matcher) {
        List<String> mutateOperators = new ArrayList<>();

        String insideQuotes = matcher.group(1);
        mutateOperators.add(value.replace(insideQuotes, "NO_" + insideQuotes));

        if (value.contains("hasAuthority")) {
            mutateOperators.add(value.replace("hasAuthority", "hasRole"));
        } else if (value.contains("hasRole")) {
            mutateOperators.add(value.replace("hasRole", "hasAuthority"));
        }


        for (String ra : rolesAndAuthorities) {
            if (!insideQuotes.equals(ra)) {
                mutateOperators.add(value.replace(insideQuotes, ra));
            }
        }

        return mutateOperators;
    }

    private List<String> mutateCompositeValue(Matcher matcher) {
        List<String> mutateOperators = new ArrayList<>();

        String fullExpression = matcher.group(0); // hasAnyRole("User", "Admin", "Guest")
        String insideQuotes = matcher.group(1); // "User", "Admin", "Guest"

        mutateOperators.addAll(swapAuthenticationMethod(fullExpression));
        mutateOperators.addAll(addNewRoleOrAuthority(fullExpression, insideQuotes));
        mutateOperators.addAll(mutateEachValue(fullExpression, insideQuotes));

        return mutateOperators;
    }

    private List<String> swapAuthenticationMethod(String fullExpression) {
        List<String> mutateOperators = new ArrayList<>();
        
        if (fullExpression.contains("hasAnyAuthority")) {
            mutateOperators.add(fullExpression.replace("hasAnyAuthority", "hasAnyRole"));
        } else if (fullExpression.contains("hasAnyRole")) {
            mutateOperators.add(fullExpression.replace("hasAnyRole", "hasAnyAuthority"));
        }
        
        return mutateOperators;
    }

    private List<String> addNewRoleOrAuthority(String fullExpression, String insideQuotes) {
        List<String> mutateOperators = new ArrayList<>();
        
        for (String ra : rolesAndAuthorities) {
            if (!insideQuotes.contains(ra)) {
                String mutatedInsideQuotes = insideQuotes + ", " + ra;
                String mutatedExpression = fullExpression.replace(insideQuotes, mutatedInsideQuotes);
                mutateOperators.add(mutatedExpression);
            }
        }
        
        return mutateOperators;
    }

    private List<String> mutateEachValue(String fullExpression, String insideQuotes) {
        List<String> mutateOperators = new ArrayList<>();
        
        String[] values = insideQuotes.split(",");
        
        for (int i = 0; i < values.length; i++) {
            String[] mutatedValues = values.clone();
            
            String value = values[i].trim();
            String mutatedValue = value.startsWith("NO_")
                    ? "" + value.substring(4)
                    : "NO_" + value.substring(1);
            
            mutatedValues[i] = mutatedValue;
            
            String mutatedInsideQuotes = String.join(", ", mutatedValues);
            String mutatedExpression = fullExpression.replace(insideQuotes, mutatedInsideQuotes);
            
            mutateOperators.add(mutatedExpression);
        }
        
        return mutateOperators;
    }

    private List<String> mutePermitAll(Matcher matcher) {

        List<String> mutateOperators = new ArrayList<>();

        mutateOperators.add("denyAll");

        return mutateOperators;

    }

    private List<String> muteDenyAll(Matcher matcher) {

        List<String> mutateOperators = new ArrayList<>();

        mutateOperators.add("permitAll()");

        return mutateOperators;

    }

}
