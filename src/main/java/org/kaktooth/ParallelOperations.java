package org.kaktooth;

import java.util.Arrays;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;

public class ParallelOperations {
    long[] array;
    private AtomicLongArray values;
    private final ExecutorService executor;
    private AtomicInteger nextLength;
    private AtomicLongArray newValues;
    private AtomicInteger lastIndex = new AtomicInteger();
    private AtomicLong sum = new AtomicLong();
    private AtomicInteger nextLengthIteration = new AtomicInteger(0);
    private final Phaser phaser;
    private long[] secondValues;

    public ParallelOperations(int size) {
        this.array = getArray(size);
        this.values = new AtomicLongArray(array);
        this.secondValues = array;

        final int cores = Runtime.getRuntime().availableProcessors();
        phaser = new Phaser();
        executor = Executors.newFixedThreadPool(cores);

        nextLength = new AtomicInteger((values.length() + 1) / 2);
        newValues = new AtomicLongArray(nextLength.get());
    }

    private void applyRunnable(Runnable runnable) {
        executor.execute(runnable);
    }

    private long[] applyCallable(Callable<long[]> callable) {
        try {
            return executor.submit(callable).get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public void shutdown() {
        executor.shutdown();
    }

    public void sum() {
        applyRunnable(this::waveAlgorithm);
    }

    private synchronized void waveAlgorithm() {
        while (values.length() != 1) {
            nextLength.set((values.length() + 1) / 2);
            newValues = new AtomicLongArray(nextLength.get());
            while (nextLengthIteration.get() < nextLength.get()) {
                int haveNoPair = values.length() % 2;
                lastIndex.set(values.length() - 1 - nextLengthIteration.get());
                if (haveNoPair == 1 && nextLengthIteration.get() == lastIndex.get()) {
                    newValues.set(nextLengthIteration.get(), values.get(nextLengthIteration.get()));
                } else {
                    sum.set(values.get(nextLengthIteration.get()) + values.get(lastIndex.get()));
                    newValues.set(nextLengthIteration.get(), sum.get());
                }
                nextLengthIteration.getAndIncrement();
            }
            nextLengthIteration.set(0);
            values = newValues;

            if (values.length() == 1) {
                System.out.println(values);
            }
        }
    }

    public void sum2() {
        try {
            while (secondValues.length != 1) {
                secondValues = applyCallable(this::waveAlgorithm2);
            }
            System.out.println(Arrays.toString(secondValues));
        } finally {
            phaser.forceTermination();
        }
    }

    private long[] waveAlgorithm2() {
        phaser.register();
        int nextLength = (secondValues.length + 1) / 2;
        long[] newValues = new long[nextLength];

        for (int i = 0; i < nextLength; i++) {
            int lastIndex = secondValues.length - i - 1;
            newValues[i] += secondValues[i];
            if (i != lastIndex) {
                newValues[i] += secondValues[lastIndex];
            }
        }

        phaser.arriveAndAwaitAdvance();
        phaser.arriveAndDeregister();
        return newValues;
    }

    public void singleThreadedWaveAlgorithm() {
        int iterations = (int) (Math.log(array.length) / Math.log(2)) + 1;
        for (int it = 0; it < iterations; it++) {
            int nextLength = (array.length + 1) / 2;
            long[] newValues = new long[nextLength];
            int lastIndex;
            long sum;
            for (int i = 0; i < nextLength; i++) {
                int haveNoPair = array.length % 2;
                lastIndex = array.length - 1 - i;
                if (haveNoPair == 1 && i == lastIndex) {
                    newValues[i] = array[i];
                } else {
                    sum = array[i] + array[lastIndex];
                    newValues[i] = sum;
                }
            }
            array = newValues;
        }

        System.out.println(Arrays.toString(array));
    }

    private long[] getArray(int size) {
        long[] array = new long[size];

        for (int i = 0; i < size; i++) {
            array[i] = i + 1;
        }
        return array;
    }
}
