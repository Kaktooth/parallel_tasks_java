package org.kaktooth;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.profile.WinPerfAsmProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.*;

import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class SumAlgorithmBenchmark {
    ParallelOperations parallelOperations = new ParallelOperations(100000000);

    public static void main(String[] args) throws RunnerException, CommandLineOptionException {
        Options opt = new OptionsBuilder()
                .include(SumAlgorithmBenchmark.class.getSimpleName())
                .parent(new CommandLineOptions(args))
                .addProfiler(WinPerfAsmProfiler.class)
//                .addProfiler(GCProfiler.class)
                .verbosity(VerboseMode.EXTRA)
                .result("build/results/jmh/profiler.csv")
                .output("build/results/jmh/profiler.log")
                .build();

        new Runner(opt).run();
    }

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
