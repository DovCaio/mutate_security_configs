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

    public MutantMaker(String value) {

        this.value = value;
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
            System.out.print("TODO");
        } else if (hasDenyAll) {
            System.out.print("TODO");

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

        return mutateOperators;
    }

    private List<String> mutateCompositeValue(Matcher matcher) {
        List<String> mutateOperators = new ArrayList<>();

        String fullExpression = matcher.group(0); // hasAnyRole("User", "Admin", "Guest")
        String insideQuotes = matcher.group(1); // "User", "Admin", "Guest"

        String[] values = insideQuotes.split(",");

        if (matcher.group(0).contains("hasAnyAuthority")) {
            mutateOperators.add("hasAnyRole(" + insideQuotes + ")");
        } else if (matcher.group(0).contains("hasAnyRole")) {
            mutateOperators.add("hasAnyAuthority(" + insideQuotes + ")");
        }

        for (int i = 0; i < values.length; i++) {

            String[] mutatedValues = values.clone();

            String value = values[i].trim();
            String mutatedValue = value.startsWith("NO_")
                    ? value.substring(3)
                    : "NO_" + value;

            mutatedValues[i] = mutatedValue;

            String mutatedInsideQuotes = String.join(", ", mutatedValues);
            String mutatedExpression = fullExpression.replace(insideQuotes, mutatedInsideQuotes);

            mutateOperators.add(mutatedExpression);
        }

        return mutateOperators;
    }

}
