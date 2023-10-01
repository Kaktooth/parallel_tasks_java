package org.kaktooth;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class SimpleBenchmark {

    @Benchmark
    @Fork(1)
    public void twoThread() {

        ParallelOperations parallelOperations = new ParallelOperations(2, 1000);
        parallelOperations.sum();
    }

    @Benchmark
    @Fork(1)
    public void fourThread() {

        ParallelOperations parallelOperations = new ParallelOperations(4, 1000);
        parallelOperations.sum();
    }

    @Benchmark
    @Fork(1)
    public void sixThread() {

        ParallelOperations parallelOperations = new ParallelOperations(6, 1000);
        parallelOperations.sum();
    }

    @Benchmark
    @Fork(1)
    public void twoThreadQueue() {

        ParallelOperations parallelOperations = new ParallelOperations(2, 1000);
        parallelOperations.sum2();
    }

    @Benchmark
    @Fork(1)
    public void fourThreadQueue() {

        ParallelOperations parallelOperations = new ParallelOperations(4, 1000);
        parallelOperations.sum2();
    }

    @Benchmark
    @Fork(1)
    public void sixThreadQueue() {

        ParallelOperations parallelOperations = new ParallelOperations(6, 1000);
        parallelOperations.sum2();
    }
}
