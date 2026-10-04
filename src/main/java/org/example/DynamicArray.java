package org.example;

public class DynamicArray implements IntSequence {
    private int[] data = new int[4];
    private int size;
    private final Metrics metrics = new Metrics();

    public void add(int value) {
        add(size, value);
    }

    public void add(int index, int value) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
        }
        if (size == data.length) {
            int[] bigger = new int[data.length * 2];
            for (int i = 0; i < size; i++) {
                bigger[i] = data[i];
                metrics.steps++;
                metrics.moves++;
            }
            data = bigger;
        }
        for (int i = size; i > index; i--) {
            data[i] = data[i - 1];
            metrics.steps++;
            metrics.moves++;
        }
        data[index] = value;
        size++;
    }

    public int remove(int index) {
        checkIndex(index);
        int value = data[index];
        metrics.steps++;
        for (int i = index; i < size - 1; i++) {
            data[i] = data[i + 1];
            metrics.steps++;
            metrics.moves++;
        }
        size--;
        return value;
    }

    public int get(int index) {
        checkIndex(index);
        metrics.steps++;
        return data[index];
    }

    public boolean contains(int value) {
        for (int i = 0; i < size; i++) {
            metrics.steps++;
            metrics.comparisons++;
            if (data[i] == value) {
                return true;
            }
        }
        return false;
    }

    public int size() {
        return size;
    }

    public Metrics metrics() {
        return metrics;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
    }
}
