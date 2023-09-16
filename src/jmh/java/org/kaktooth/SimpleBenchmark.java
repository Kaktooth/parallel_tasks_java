package org.kaktooth;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class SimpleBenchmark {

    @Benchmark
    @Fork(1)
    public void first() {

        ParallelOperations parallelOperations = new ParallelOperations(6, OperationsConstants.BIG_ARRAY);
        parallelOperations.sum();
    }

    @Benchmark
    @Fork(1)
    public void second() {

        ParallelOperations parallelOperations = new ParallelOperations(OperationsConstants.BIG_ARRAY);
        parallelOperations.sum1();
    }
}
