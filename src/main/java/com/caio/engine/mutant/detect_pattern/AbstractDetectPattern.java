package com.caio.engine.mutant.detect_pattern;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.caio.engine.mutant.mutants.MutantStrategy;

public class AbstractDetectPattern {

    private String target;
    private Pattern pattern;
    private Matcher matcher;
    private List<MutantStrategy> mutantStrategies;

    public AbstractDetectPattern(String regex, String target) {
        this.target = target;
        this.pattern = Pattern.compile(regex);
        this.matcher = pattern.matcher(this.target);
        this.mutantStrategies = new ArrayList<>();
    }

    public boolean detect() {
        return this.matcher.find();
    }

    public void addMutantStrategy(MutantStrategy mutantStrategy) {
        mutantStrategies.add(mutantStrategy);
    }

    public List<MutantStrategy> getMutantStrategies() {
        return mutantStrategies;
    }

    public Matcher getMatcher() {
        return matcher;
    }

    public String getGroup(int group) {
        return matcher.group(group);
    }
}
