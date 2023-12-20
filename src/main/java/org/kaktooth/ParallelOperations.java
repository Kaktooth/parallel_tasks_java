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
    private AtomicInteger i = new AtomicInteger(0);

    public ParallelOperations(int size) {
        this.array = getArray(size);
        this.values = new AtomicLongArray(array);

        final int cores = Runtime.getRuntime().availableProcessors();
        executor = Executors.newFixedThreadPool(cores);

        nextLength = new AtomicInteger((values.length() + 1) / 2);
        newValues = new AtomicLongArray(nextLength.get());
    }

    private void applyRunnable(Runnable runnable) {
        executor.execute(runnable);
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
            while (i.get() < nextLength.get()) {
                int haveNoPair = values.length() % 2;
                lastIndex.set(values.length() - 1 - i.get());
                if (haveNoPair == 1 && i.get() == lastIndex.get()) {
                    newValues.set(i.get(), values.get(i.get()));
                } else {
                    sum.set(values.get(i.get()) + values.get(lastIndex.get()));
                    newValues.set(i.get(), sum.get());
                }
                i.getAndIncrement();
            }
            i.set(0);
            values = newValues;

            if (values.length() == 1) {
                System.out.println(values);
            }
        }
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
