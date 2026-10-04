package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

public class Benchmark {
    private static volatile long sink;

    private static class Result {
        long time;
        long steps;
        long moves;
        long comparisons;

        Result(long time, Metrics metrics) {
            this.time = time;
            steps = metrics.steps;
            moves = metrics.moves;
            comparisons = metrics.comparisons;
        }
    }

    public static void main(String[] args) throws IOException {
        StringBuilder csv = new StringBuilder("workload,variant,structure,n,time_ms,steps,moves,comparisons\n");
        int[] sizes = {100, 1000, 10000, 100000};
        for (int n : sizes) {
            int[] values = new int[n];
            Random random = new Random(42);
            for (int i = 0; i < n; i++) {
                values[i] = random.nextInt(1_000_000);
            }
            int[] indexes = new int[10000];
            random = new Random(43);
            for (int i = 0; i < indexes.length; i++) {
                indexes[i] = random.nextInt(n);
            }
            int[] queries = new int[1000];
            for (int i = 0; i < queries.length; i++) {
                queries[i] = i % 2 == 0 ? values[random.nextInt(n)] : -i - 1;
            }
            for (String structure : new String[]{"DynamicArray", "MyLinkedList"}) {
                addCase(csv, "W1", "-", structure, n, values, indexes);
                addCase(csv, "W2", "-", structure, n, values, queries);
                addCase(csv, "W3", "head", structure, n, values, null);
                addCase(csv, "W3", "middle", structure, n, values, null);
            }
            addCase(csv, "W4", "-", "MinHeap", n, values, null);
            System.out.println("Finished n=" + n);
        }
        Files.createDirectories(Path.of("results"));
        Files.writeString(Path.of("results/results.csv"), csv.toString());
        System.out.println("Saved results/results.csv; checksum=" + sink);
    }

    private static void addCase(StringBuilder csv, String workload, String variant,
                                String structure, int n, int[] values, int[] input) {
        for (int i = 0; i < 10; i++) {
            run(workload, variant, structure, n, values, input);
        }
        long[] times = new long[5];
        Result result = null;
        for (int i = 0; i < 5; i++) {
            result = run(workload, variant, structure, n, values, input);
            times[i] = result.time;
        }
        for (int i = 1; i < times.length; i++) {
            long value = times[i];
            int j = i - 1;
            while (j >= 0 && times[j] > value) {
                times[j + 1] = times[j];
                j--;
            }
            times[j + 1] = value;
        }
        csv.append(workload).append(',').append(variant).append(',').append(structure).append(',')
                .append(n).append(',').append(times[2] / 1_000_000.0).append(',')
                .append(result.steps).append(',').append(result.moves).append(',')
                .append(result.comparisons).append('\n');
    }

    private static Result run(String workload, String variant, String structure,
                              int n, int[] values, int[] input) {
        if (workload.equals("W4")) {
            MinHeap heap = new MinHeap();
            long checksum = 0;
            long start = System.nanoTime();
            for (int value : values) {
                heap.insert(value);
            }
            int previous = Integer.MIN_VALUE;
            for (int i = 0; i < n; i++) {
                int value = heap.extractMin();
                if (value < previous) {
                    throw new IllegalStateException("Heap output is not sorted");
                }
                previous = value;
                checksum += value;
            }
            long time = System.nanoTime() - start;
            sink += checksum;
            return new Result(time, heap.metrics());
        }
        IntSequence sequence = structure.equals("DynamicArray")
                ? new DynamicArray() : new MyLinkedList();
        for (int value : values) {
            sequence.add(value);
        }
        sequence.metrics().reset();
        long checksum = 0;
        long start = System.nanoTime();
        if (workload.equals("W1")) {
            for (int index : input) {
                checksum += sequence.get(index);
            }
        } else if (workload.equals("W2")) {
            for (int value : input) {
                if (sequence.contains(value)) {
                    checksum++;
                }
            }
        } else {
            int index = variant.equals("head") ? 0 : n / 2;
            for (int i = 0; i < 1000; i++) {
                sequence.add(index, i);
            }
            for (int i = 0; i < 1000; i++) {
                checksum += sequence.remove(index);
            }
        }
        long time = System.nanoTime() - start;
        sink += checksum;
        return new Result(time, sequence.metrics());
    }
}
