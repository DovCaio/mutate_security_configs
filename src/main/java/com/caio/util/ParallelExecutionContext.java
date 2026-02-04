package com.caio.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import com.caio.models.AnnotationMutationPoint;

public class ParallelExecutionContext {
    
    private int totalWorkers;
    private final BlockingQueue<AnnotationMutationPoint> taskQueue;
    private final BlockingQueue<String> logQueue;
    private final AtomicInteger progress;

    public ParallelExecutionContext(BlockingQueue<AnnotationMutationPoint> taskQueue,
            BlockingQueue<String> logQueue, AtomicInteger progress) {
        this.taskQueue = taskQueue;
        this.logQueue = logQueue;
        this.progress = progress;
    }

    public int getTotalWorkers() {
        return totalWorkers;
    }

    public void setTotalWorkers(int totalWorkers) {
        this.totalWorkers = totalWorkers;
    }

    public BlockingQueue<AnnotationMutationPoint> getTaskQueue() {
        return taskQueue;
    }

    public BlockingQueue<String> getLogQueue() {
        return logQueue;
    }

    public AtomicInteger getProgress() {
        return progress;
    }

    

}
