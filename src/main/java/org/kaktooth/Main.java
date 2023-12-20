package org.kaktooth;

public class Main {
    public static void main(String[] args) {
        ParallelOperations parallelOperations = new ParallelOperations(100000000);
        parallelOperations.sum();
        parallelOperations.shutdown();
    }
}