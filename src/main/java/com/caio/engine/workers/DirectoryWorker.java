package com.caio.engine.workers;

import java.util.Queue;

import com.caio.engine.CodeLoader;
import com.caio.models.AnnotationMutationPoint;

public class DirectoryWorker implements Runnable {

    private CodeLoader codeLoader;
    private final Queue<AnnotationMutationPoint> mutationQueue;

    public DirectoryWorker(CodeLoader codeLoader, Queue<AnnotationMutationPoint> mutationQueue) {
        this.codeLoader = codeLoader;
        this.mutationQueue = mutationQueue;
    }

    @Override
    public void run() {
        AnnotationMutationPoint mutation;

        while ((mutation = mutationQueue.poll()) != null) {
            codeLoader.executeOne(mutation);

        }
    }

}
