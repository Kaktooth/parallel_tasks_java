package org.kaktooth;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class ParallelOperations {
    private final int threadNumber;
    private Thread[] threads;
    private int[] values;

    public ParallelOperations(int[] values) {
        this.threadNumber = (int) (Math.log(values.length) / Math.log(2)) + 1;
        this.values = values;
    }

    public ParallelOperations(int threadNumber, int[] values) {
        this.threadNumber = threadNumber;
        this.values = values;
    }


    private void applyRunnable(Runnable runnable) {
        threads = new Thread[threadNumber];
        for (int i = 0; i < threadNumber; i++) {
            threads[i] = new Thread(runnable);
            threads[i].start();
        }
    }

    public void sum() {
        applyRunnable(this::waveAlgorithm);
    }

    private synchronized void waveAlgorithm() {

        if (values.length == 1) {
            try {
                Thread.currentThread().join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        int nextLength = (int) Math.ceil((double) values.length / 2);
        int[] newValues = new int[nextLength];
        int lastIndex;
        int sum;
        for (int i = 0; i < nextLength; i++) {
            int haveNoPair = values.length % 2;
            lastIndex = values.length - 1 - i;
            if (haveNoPair == 1 && i == lastIndex) {
                newValues[i] = values[i];
            } else {
                sum = values[i] + values[lastIndex];
                newValues[i] = sum;
            }
        }

        System.out.println(Arrays.toString(values) + "->" + Arrays.toString(newValues));
        values = newValues;

        if (values.length != 1) {
            Thread nextThread = new Thread(this::waveAlgorithm);
            nextThread.start();
        }
    }

    public void sum1() {
        applyRunnable(this::waveAlgorithm1);
    }

    private synchronized void waveAlgorithm1() {

        int nextLength = (values.length + 1) / 2;
        int[] newValues = new int[nextLength];
        int lastIndex;
        int sum;
        for (int i = 0; i < nextLength; i++) {
            int haveNoPair = values.length % 2;
            lastIndex = values.length - 1 - i;
            if (haveNoPair == 1 && i == lastIndex) {
                newValues[i] = values[i];
            } else {
                sum = values[i] + values[lastIndex];
                newValues[i] = sum;
            }
        }
        System.out.println(Arrays.toString(values) + "->" + Arrays.toString(newValues));
        values = newValues;
    }
}
