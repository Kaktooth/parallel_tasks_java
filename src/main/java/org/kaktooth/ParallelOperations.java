package org.kaktooth;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class ParallelOperations {
    private final int threadNumber;
    private AtomicIntegerArray values;
    private ExecutorService ex;

    public ParallelOperations(int[] values) {
        this.threadNumber = (int) (Math.log(values.length) / Math.log(2)) + 1;
        this.values = new AtomicIntegerArray(values);
        ex = Executors.newFixedThreadPool(threadNumber);
    }

    public ParallelOperations(int threadNumber, int[] values) {
        this.threadNumber = threadNumber;
        this.values = new AtomicIntegerArray(values);
        ex = Executors.newFixedThreadPool(threadNumber);
    }


    private void applyRunnable(Runnable runnable) {
        ex.execute(runnable);
    }

    public void sum() {
        applyRunnable(this::waveAlgorithm);
        ex.shutdown();
    }

    private void waveAlgorithm() {

        int nextLength = (values.length() + 1) / 2;
        var newValues = new AtomicIntegerArray(nextLength);
        int lastIndex;
        int sum;
        for (int i = 0; i < nextLength; i++) {
            int haveNoPair = values.length() % 2;
            lastIndex = values.length() - 1 - i;
            if (haveNoPair == 1 && i == lastIndex) {
                newValues.set(i, values.get(i));
            } else {
                sum = values.get(i) + values.get(lastIndex);
                newValues.set(i, sum);
            }
        }

        values = newValues;
        System.out.println(values);

        if (values.length() != 1) {
            waveAlgorithm();
        }
    }
}
