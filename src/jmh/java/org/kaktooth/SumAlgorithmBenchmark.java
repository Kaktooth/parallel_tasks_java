package org.kaktooth;

import org.openjdk.jmh.annotations.*;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class SumAlgorithmBenchmark {

    @Param({"100", "1000", "10000", "100000", "1000000", "10000000", "100000000"})
    int arraySize;

    ParallelOperations parallelOperations;

    @Setup(Level.Invocation)
    public void init() {
        this.parallelOperations = new ParallelOperations(arraySize);
        System.out.println("array size: " + parallelOperations.array.length
                + " " + Arrays.toString(parallelOperations.array).substring(0, 15) + "...");
    }

    @Benchmark
    @Fork(1)
    public void singleThreadedLongArray() {
        parallelOperations.singleThreadedWaveAlgorithm();
    }

    @Benchmark
    @Fork(1)
    public void atomicArray() {
        parallelOperations.sum();
    }

    @TearDown
    public void shutdownExecutor() {
        parallelOperations.shutdown();
    }
}
