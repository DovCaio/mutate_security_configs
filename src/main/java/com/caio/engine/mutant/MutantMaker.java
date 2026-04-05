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

    private final String regexHasPermission = "(?<!\\.)hasPermission\\(([^)]*)\\)";
    private final String regexHasPermissionCustom = "@(\\w+)\\.hasPermission\\s*\\(\\s*([^)]*)\\)";

    private String value;
    private List<String> rolesAndAuthorities;

    public MutantMaker(String value, List<String> roles, List<String> authorities) {

        this.value = value;

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
        Pattern patternHasPermission = Pattern.compile(regexHasPermission);
        Pattern patternHasPermissionCustom = Pattern.compile(regexHasPermissionCustom);

        Matcher matcherSimpleCase = patternSimpleCase.matcher(this.value);
        Matcher matcherCompostCase = patternCompostCase.matcher(this.value);
        Matcher matcherPermitAllCase = patternPermitAll.matcher(this.value);
        Matcher matcherDenyCase = patternDenyAll.matcher(this.value);
        Matcher matcherHasPermissionCase = patternHasPermission.matcher(this.value);
        Matcher matcherHasPermissionCustomCase = patternHasPermissionCustom.matcher(this.value);

        boolean hasSimple = matcherSimpleCase.find();
        boolean hasCompost = matcherCompostCase.find();
        boolean hasPermitAll = matcherPermitAllCase.find();
        boolean hasDenyAll = matcherDenyCase.find();
        boolean hasHasPermission = matcherHasPermissionCase.find();
        boolean hasHasPermissionCustom = matcherHasPermissionCustomCase.find();

        if (!hasDenyAll && !hasPermitAll) { 
            result.add("permitAll()");  
            result.add("denyAll");
        }



        if (hasSimple) {
            result.add(addNegation(matcherSimpleCase.group(0)));
            result.addAll(mutateSimpleValue(matcherSimpleCase));
            result.add(removeInsideParentheses(matcherSimpleCase));
        } else if (hasCompost) {
            result.add(addNegation(matcherCompostCase.group(0)));
            result.addAll(mutateCompositeValue(matcherCompostCase));
            result.add(removeInsideParentheses(matcherCompostCase));
            result.addAll(alterIntoQuantitiesOfParamsAndWhichParams(matcherCompostCase));

        } else if (hasPermitAll) {
            result.addAll(mutePermitAll(matcherPermitAllCase));
        } else if (hasDenyAll) {
            result.addAll(muteDenyAll(matcherDenyCase));

        } else if (hasHasPermissionCustom) {
            result.add(addNegation(matcherHasPermissionCustomCase.group(0)));
            matcherHasPermissionCustomCase.reset();
            result.addAll(muteHasPermissionCustom(matcherHasPermissionCustomCase));
        } else if (hasHasPermission) {
            result.add(addNegation(matcherHasPermissionCase.group(0)));

            matcherHasPermissionCase.reset();
            result.addAll(muteHasPermission(matcherHasPermissionCase));
        } else {
            result.add("");
        }

        return result.stream().distinct().toList();
    }

    private String addNegation(String str) {
        if (str.startsWith("!")) {
            return "";
        }
        return "!" + str;
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
                    : value.substring(0, 1) + "NO_" + value.substring(1);

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

    private String removeInsideParentheses(Matcher matcher) {
        String str = matcher.group(0);
        String mutant = str.replaceAll("\\(([^)]*)\\)", "(\'\')");
        return mutant;
    }

    private List<String> alterIntoQuantitiesOfParamsAndWhichParams(Matcher matcher) {

        List<String> ra = matcher.group(1) != null ? List.of(matcher.group(1).split(",")) : new ArrayList<>();

        List<String> result = new ArrayList<>();

        for (int i = 0; i < ra.size(); i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < ra.size(); j++) {
                if (i != j) {
                    if (sb.length() > 0) {
                        sb.append(", ");
                    }
                    sb.append(ra.get(j));
                }
            }
            String mutatedInsideQuotes = sb.toString();
            String fullExpression = matcher.group(0);
            String mutatedExpression = fullExpression.replace(matcher.group(1), mutatedInsideQuotes);
            result.add(mutatedExpression);
        }

        return result;
    }



    private List<String> muteHasPermission(Matcher matcher) {

        List<String> mutants = new ArrayList<>();

        while (matcher.find()) { // 🔥 ESSENCIAL

            String args = matcher.group(1);


            String[] parts = args.split("\\s*,\\s*");


            for (int i = 0; i < parts.length; i++) {

                if (parts[i].startsWith("'") && parts[i].endsWith("'")) {

                    String original = parts[i];
                    String innerValue = original.substring(1, original.length() - 1);

                    parts[i] = "'MUTATED_" + innerValue + "'";

                    String newArgs = String.join(", ", parts);

                    mutants.add(
                            value.substring(0, matcher.start()) +
                                    "hasPermission(" + newArgs + ")" +
                                    value.substring(matcher.end()));

                    parts[i] = original;
                }
            }
        }

        return mutants;
    }

    private List<String> muteHasPermissionCustom(Matcher matcher) {

        List<String> mutants = new ArrayList<>();

        while (matcher.find()) {

            String beanName = matcher.group(1);
            String params = matcher.group(2);


            String mutatedParams = params.replaceAll("'([^']+)'", "'MUTATED_$1'");
            mutants.add(
                    value.substring(0, matcher.start()) +
                            "@" + beanName + ".hasPermission(" + mutatedParams + ")" +
                            value.substring(matcher.end()));

            mutants.add(
                    value.substring(0, matcher.start()) +
                            "@" + beanName + ".hasPermition(" + params + ")" +
                            value.substring(matcher.end()));
        }

        return mutants;
    }

}
