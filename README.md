# Assignment 2: Data Structures

This Java 21 project implements an `int` dynamic array, singly linked list, and array-based min-heap without Java collection classes in the implementations. The benchmark saves operation counts and median times for four workloads.

## Build and test

```text
mvn test
```

IntelliJ IDEA can run `IntSequenceTest` and `MinHeapTest` directly. The tests compare results with Java collections, check invalid input and duplicates, and check the heap property after every operation.

## Run the benchmark

From the project root:

```text
mvn -q -DskipTests package
java -cp target/classes org.example.Main
```

The Java command regenerates `results/results.csv` using seed 42. Each case gets 10 warm-up runs followed by 5 measured runs; the saved time is the median. The first three workloads measure only the requested operations after filling the structure. W4 measures both insertion and extraction. The benchmark validates that extracted heap values are sorted.

To regenerate the charts, install Python 3 with Matplotlib and run:

```text
python results/plot_results.py
```

The charts are saved in `results/plots/`. `REPORT.md` contains the complexity analysis, loop invariant proofs, charts, and discussion.

## Counter rules

- `steps`: each read from an array cell or each move from one list node to the next.
- `moves`: each array element copy or shift and each list pointer update.
- `comparisons`: comparisons between stored values or between a stored value and a search value.

The counters are updated inside the data structure methods. The benchmark resets them after filling for W1-W3. Filling is included for W4. Timing and operation counts are separate columns in the CSV.
