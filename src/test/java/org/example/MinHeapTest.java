package org.example;

import org.junit.jupiter.api.Test;

import java.util.PriorityQueue;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class MinHeapTest {
    @Test
    void randomOperationsMatchPriorityQueueAndKeepHeapProperty() {
        MinHeap heap = new MinHeap();
        PriorityQueue<Integer> expected = new PriorityQueue<>();
        Random random = new Random(42);
        for (int i = 0; i < 3000; i++) {
            if (expected.isEmpty() || random.nextBoolean()) {
                int value = random.nextInt(1000) - 500;
                heap.insert(value);
                expected.add(value);
            } else {
                assertEquals(expected.remove().intValue(), heap.extractMin());
            }
            assertEquals(expected.size(), heap.size());
            if (!expected.isEmpty()) {
                assertEquals(expected.peek().intValue(), heap.peekMin());
            }
            assertHeapProperty(heap);
        }
        int previous = Integer.MIN_VALUE;
        while (heap.size() > 0) {
            int value = heap.extractMin();
            assertTrue(value >= previous);
            previous = value;
            assertHeapProperty(heap);
        }
    }

    @Test
    void emptyOneElementAndDuplicates() {
        MinHeap heap = new MinHeap();
        assertThrows(IllegalStateException.class, heap::peekMin);
        assertThrows(IllegalStateException.class, heap::extractMin);
        heap.insert(4);
        assertEquals(4, heap.peekMin());
        assertEquals(4, heap.extractMin());
        heap.insert(2);
        heap.insert(2);
        heap.insert(-1);
        assertEquals(-1, heap.extractMin());
        assertEquals(2, heap.extractMin());
        assertEquals(2, heap.extractMin());
        assertThrows(IllegalStateException.class, heap::extractMin);
    }

    private void assertHeapProperty(MinHeap heap) {
        for (int child = 1; child < heap.size(); child++) {
            assertTrue(heap.valueAt((child - 1) / 2) <= heap.valueAt(child));
        }
    }
}
