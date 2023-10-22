package org.kaktooth;

public class Main {
    public static void main(String[] args) {
        ParallelOperations parallelOperations = new ParallelOperations(1000000);
        parallelOperations.sum2();
        parallelOperations.shutdown();
    }
}