package org.example;

public class MinHeap {
    private int[] data = new int[4];
    private int size;
    private final Metrics metrics = new Metrics();

    public void insert(int value) {
        if (size == data.length) {
            int[] bigger = new int[data.length * 2];
            for (int i = 0; i < size; i++) {
                bigger[i] = data[i];
                metrics.steps++;
                metrics.moves++;
            }
            data = bigger;
        }
        int index = size;
        while (index > 0) {
            int parentIndex = (index - 1) / 2;
            int parent = data[parentIndex];
            metrics.steps++;
            metrics.comparisons++;
            if (parent <= value) {
                break;
            }
            data[index] = parent;
            metrics.moves++;
            index = parentIndex;
        }
        data[index] = value;
        if (index != size) {
            metrics.moves++;
        }
        size++;
    }

    public int peekMin() {
        if (size == 0) {
            throw new IllegalStateException();
        }
        metrics.steps++;
        return data[0];
    }

    public int extractMin() {
        if (size == 0) {
            throw new IllegalStateException();
        }
        int result = data[0];
        metrics.steps++;
        size--;
        if (size == 0) {
            return result;
        }
        int value = data[size];
        metrics.steps++;
        int index = 0;
        while (index * 2 + 1 < size) {
            int childIndex = index * 2 + 1;
            int child = data[childIndex];
            metrics.steps++;
            if (childIndex + 1 < size) {
                int right = data[childIndex + 1];
                metrics.steps++;
                metrics.comparisons++;
                if (right < child) {
                    childIndex++;
                    child = right;
                }
            }
            metrics.comparisons++;
            if (value <= child) {
                break;
            }
            data[index] = child;
            metrics.moves++;
            index = childIndex;
        }
        data[index] = value;
        metrics.moves++;
        return result;
    }

    public int size() {
        return size;
    }

    public Metrics metrics() {
        return metrics;
    }

    int valueAt(int index) {
        return data[index];
    }
}
