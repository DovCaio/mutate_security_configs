package com.caio.analize;

import java.nio.file.Files;
import java.nio.file.Path;

import com.caio.exceptions.DependenciePathIsNull;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.*;

public class Dependencies {

private static final Pattern DEPENDENCY_PATTERN = Pattern.compile(
    "<dependency>(?:(?!</dependency>).)*?" +        // tudo até fechar o dependency
    "<groupId>(.*?)</groupId>.*?" +
    "<artifactId>(.*?)</artifactId>.*?" +
    "<version>(.*?)</version>.*?" +
    "</dependency>",
    Pattern.DOTALL
);




    private Path dependenciesPath;

    private byte[] dependeciesJar;


    protected Dependencies(){
    }



    public void searchForDependencies(Path path) throws Exception{
        this.dependenciesPath = path;
        if (isMavenProject()) {
            mavenSearchForDependencies();
        }
        
    }

    private boolean isMavenProject(){
        if (dependenciesPath == null) throw new DependenciePathIsNull();
        return dependenciesPath.toAbsolutePath().endsWith("pom.xml");
    }

    private void mavenSearchForDependencies() throws Exception{
        String content = Files.readString(dependenciesPath);
        Matcher matcher = DEPENDENCY_PATTERN.matcher(content);

        List<Dependency> dependencies = new ArrayList<>();
        while (matcher.find()) {
            String groupId = matcher.group(1).trim();
            String artifactId = matcher.group(2).trim();
            String version = matcher.group(3).trim();
            dependencies.add(new Dependency(groupId, artifactId, version));
        }
        
        for (Dependency dependency : dependencies){
            System.out.println(dependency.toString());
        }
    }

    
    public class Dependency {
    public String groupId, artifactId, version;
    public Dependency(String g, String a, String v) {
        this.groupId = g;
        this.artifactId = a;
        this.version = v;
    }
    @Override
    public String toString() {
        return groupId + ":" + artifactId + ":" + version;
    }
    }
}
