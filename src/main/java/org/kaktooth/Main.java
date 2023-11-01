package org.kaktooth;

public class Main {
    public static void main(String[] args) {
        ParallelOperations parallelOperations = new ParallelOperations(100);
        parallelOperations.sum();
        parallelOperations.shutdown();
    }
}