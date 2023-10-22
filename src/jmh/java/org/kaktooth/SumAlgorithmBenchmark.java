package org.kaktooth;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class SumAlgorithmBenchmark {
    ParallelOperations parallelOperations = new ParallelOperations(10000000);

    @Benchmark
    @Fork(1)
    public void singleThreadedLongArray() {
        parallelOperations.waveAlgorithm3();
    }

    @Benchmark
    @Fork(1)
    public void atomicArray() {
        parallelOperations.sum();
    }

    @Benchmark
    @Fork(1)
    public void queue() {
        parallelOperations.sum2();
    }

    @TearDown
    public void shutdownExecutor() {
        parallelOperations.shutdown();
    }
}
