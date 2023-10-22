package org.kaktooth;

import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class ParallelOperations {
    private long[] array;
    private AtomicLongArray values;
    private final AtomicReference<ConcurrentLinkedDeque<Long>> queue;
    private final ExecutorService executor;

    public ParallelOperations(int size) {
        this.array = getArray(size);
        this.values = new AtomicLongArray(array);
        this.queue = new AtomicReference<>(Arrays.stream(array).boxed().collect(Collectors.toCollection(ConcurrentLinkedDeque::new)));
        executor = Executors.newCachedThreadPool();
    }

    private void applyRunnable(Runnable runnable) {
        executor.execute(runnable);
    }

    public void shutdown() {
        executor.shutdown();
    }

    public void sum() {
//      System.out.println(values);
        applyRunnable(this::waveAlgorithm);
    }

    private void waveAlgorithm() {

        var nextLength = new AtomicInteger((values.length() + 1) / 2);
        var newValues = new AtomicLongArray(nextLength.get());
        var lastIndex = new AtomicInteger();
        var sum = new AtomicLong();
        var i = new AtomicInteger(0);
        if (values.length() != 1) {
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
            synchronized (this) {
                values = newValues;
            }
//      System.out.println(values);
            waveAlgorithm();
        }
    }

    public void sum2() {
//      System.out.println(queue);
        applyRunnable(this::waveAlgorithm2);
    }

    private void waveAlgorithm2() {

        var nextLength = new AtomicInteger((queue.get().size() + 1) / 2);
        var newQueue = new ConcurrentLinkedDeque<Long>();
        var sum = new AtomicLong();
        var i = new AtomicInteger();
        for (; i.get() < nextLength.get(); i.getAndIncrement()) {
            if (queue.get().size() == 1) {
                newQueue.add(queue.get().pollFirst());
            } else {
                sum.set(queue.get().pollFirst() + queue.get().pollLast());
                newQueue.add(sum.get());
            }
        }

        queue.set(newQueue);


//        System.out.println(queue);

        if (queue.get().size() != 1) {
            waveAlgorithm2();
        }
    }

    public void waveAlgorithm3() {
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
//            System.out.println(Arrays.toString(array));
        }
    }

    private long[] getArray(int size) {
        long[] array = new long[size];

        for (int i = 0; i < size; i++) {
            array[i] = i + 1;
        }
        return array;
    }
}
