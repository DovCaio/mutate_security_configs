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
            System.out.println("Annotation Name: " + amp.getAnnotationName());
            System.out.println("Original Value: " + amp.getOriginalValue());
            System.out.println("Mutated Value: " + amp.getMutatedValue());
            System.out.println("Target Type: " + amp.getTargetType());
            System.out.println("File Path: " + amp.getFilePath());
            System.out.println("Line Number: " + amp.getLineNumber());
            System.out.println("-------------------------------\n");
        }

        System.out.println("\n");

    }

    public static void printSimpleListString(String title, List<String> list) {

        if (list == null || list.isEmpty()) {
            return;
        }
        System.out.println("\n" + title + ":");
        for (String item : list) {
            System.out.println(" - " + item);
        }
        System.out.println("\n");
    }

}
