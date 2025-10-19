package com.caio.utli;

import com.caio.models.AnnotationMutationPoint;

import java.nio.file.Path;
import java.util.List;

public class Printers {

    public static void printPaths(List<Path> paths) {
        if (paths == null || paths.isEmpty()) {
            return;
        }

        System.out.print("Caminhos das classes encontradas:");
        for (Path path : paths) {
            System.out.println("    " + path.toAbsolutePath());
        }
    }

    public static void printMutationPoints(List<AnnotationMutationPoint> points) {
        System.out.println("\n");

        for (AnnotationMutationPoint amp : points) {
            System.out.println("---- AnnotationMutationPoint ----");
            System.out.println("Target Type: " + amp.getTargetType());
            System.out.println("Owner Class: " + amp.getOwnerClass());
            System.out.println("Annotation Desc: " + amp.getAnnotationDesc());
            System.out.println("Target Element: " + amp.getTargetElement());
            System.out.println("Values:");
            if (amp.getValues() != null) {
                for (int i = 0; i < amp.getValues().size(); i += 2) {
                    String key = (String) amp.getValues().get(i);
                    Object val = amp.getValues().get(i + 1);
                    System.out.println("  " + key + " = " + val);
                }
            }
            System.out.println("-------------------------------\n");
        }

        System.out.println("\n");

    }


}
