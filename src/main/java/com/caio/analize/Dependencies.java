package com.caio.analize;

import java.net.URL;
import java.nio.file.Path;

import com.caio.exceptions.DependenciePathIsNull;

import java.util.*;
import java.util.stream.Collectors;

public class Dependencies {


    List<URL> jarUrls;


    protected Dependencies(){
    }

    public void extractJars(List<Path> paths) throws Exception{

        if (paths == null) throw new DependenciePathIsNull();
        
        List<URL> jarUrls = paths.stream()
                .map(path -> {
                    try {
                        return path.toUri().toURL();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
        this.jarUrls = jarUrls;
    }



    public List<URL> getJarUrls() {
        return jarUrls;
    }



    public void setJarUrls(List<URL> jarUrls) {
        this.jarUrls = jarUrls;
    }

    
}