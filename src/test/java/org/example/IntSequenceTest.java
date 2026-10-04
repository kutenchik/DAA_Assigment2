package org.example;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class IntSequenceTest {
    @Test
    void randomOperationsMatchArrayList() {
        for (IntSequence sequence : new IntSequence[]{new DynamicArray(), new MyLinkedList()}) {
            ArrayList<Integer> expected = new ArrayList<>();
            Random random = new Random(42);
            for (int i = 0; i < 2000; i++) {
                int action = random.nextInt(4);
                if (action == 0 || expected.isEmpty()) {
                    int index = random.nextInt(expected.size() + 1);
                    int value = random.nextInt(101) - 50;
                    sequence.add(index, value);
                    expected.add(index, value);
                } else if (action == 1) {
                    int index = random.nextInt(expected.size());
                    assertEquals(expected.remove(index).intValue(), sequence.remove(index));
                } else if (action == 2) {
                    int index = random.nextInt(expected.size());
                    assertEquals(expected.get(index).intValue(), sequence.get(index));
                } else {
                    int value = random.nextInt(101) - 50;
                    assertEquals(expected.contains(value), sequence.contains(value));
                }
                assertEquals(expected.size(), sequence.size());
                for (int j = 0; j < expected.size(); j++) {
                    assertEquals(expected.get(j).intValue(), sequence.get(j));
                }
            }
        }
    }

    @Test
    void edgeCasesAndInvalidIndexes() {
        for (IntSequence sequence : new IntSequence[]{new DynamicArray(), new MyLinkedList()}) {
            assertFalse(sequence.contains(7));
            assertThrows(IndexOutOfBoundsException.class, () -> sequence.get(0));
            assertThrows(IndexOutOfBoundsException.class, () -> sequence.remove(0));
            assertThrows(IndexOutOfBoundsException.class, () -> sequence.add(-1, 7));
            assertThrows(IndexOutOfBoundsException.class, () -> sequence.add(1, 7));
            sequence.add(7);
            assertEquals(7, sequence.get(0));
            sequence.add(0, 7);
            sequence.add(sequence.size(), 9);
            assertEquals(7, sequence.get(0));
            assertEquals(9, sequence.get(sequence.size() - 1));
            assertTrue(sequence.contains(7));
            assertEquals(7, sequence.remove(0));
            assertEquals(9, sequence.remove(sequence.size() - 1));
            assertEquals(7, sequence.remove(0));
            assertEquals(0, sequence.size());
            assertThrows(IndexOutOfBoundsException.class, () -> sequence.get(-1));
            assertThrows(IndexOutOfBoundsException.class, () -> sequence.remove(0));
        }
    }

    @Test
    void countersTrackDefinedOperations() {
        DynamicArray array = new DynamicArray();
        array.add(1);
        array.add(2);
        array.metrics().reset();
        assertEquals(2, array.get(1));
        assertEquals(1, array.metrics().steps);
        array.add(0, 3);
        assertEquals(2, array.metrics().moves);

        MyLinkedList list = new MyLinkedList();
        list.add(1);
        list.add(2);
        list.metrics().reset();
        assertEquals(2, list.get(1));
        assertEquals(1, list.metrics().steps);
        list.add(0, 3);
        assertTrue(list.metrics().moves > 0);
    }
}
