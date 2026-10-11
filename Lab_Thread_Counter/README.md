# Lab_Thread_Counter — Static vs Non-Static Variables in Multithreading

**Problem:** SEPM-001 · **Language:** Java
**Name:** Al-Shahariar · **ID:** IT-24057 · **Dept.:** ICT, MBSTU

## 1. Directory structure

```
Lab_Thread_Counter/
├── Shahariar_Thread.java        # Source code (single class)
├── run_all.sh               # Runs every test case (Git Bash / Linux)
├── run_all.bat              # Same, for Windows CMD / PowerShell
├── output.txt               # Full console output of run_all.sh
├── handwritten_code/        # Scanned / photographed handwritten code
│   └── page1.jpg ...
├── screenshots/             # Execution screenshots (TC1 … TC7)
│   └── tc1.png ...
└── README.md
```

## 2. Program design

- `Shahariar_Thread` is a single class that implements `Runnable`. Each thread gets its **own** `Shahariar_Thread` object (this object plays the role of `CounterTask`).
- **Static counter** (`safeStatic` / `unsafeStatic`): belongs to the class, so all threads update the same variable.
- **Non-static counter** (`instanceCount`): belongs to each object, so every thread updates only its own copy. No sharing → no race.
- **Experiment A (`true`)**: static counter is an `AtomicLong` → `incrementAndGet()` is atomic.
- **Experiment B (`false`)**: static counter is a plain `long` → `unsafeStatic++` is not atomic (read → add → write), so updates can be lost.
- `main()` starts N threads, calls `join()` on all of them, then calculates:

| Symbol | Meaning | Formula |
|---|---|---|
| E | Expected count | N × K |
| S | Final static counter | — |
| I | Sum of all instance counters | Σ instanceCount |
| D | Absolute difference | \|S − I\| |
| P | Percentage difference | \|S − I\| / I × 100 (0% if S = I = 0, undefined if only I = 0) |

## 3. How to run

```bash
javac Shahariar_Thread.java
java Shahariar_Thread 10 100000 true     # thread-safe experiment
java Shahariar_Thread 10 100000 false    # unsynchronized experiment

bash run_all.sh | tee output.txt     # all test cases at once
```

## 4. Test inputs

| Test | Threads N | Increments K | Expected N × K |
|---|---|---|---|
| TC1 | 1 | 1,000 | 1,000 |
| TC2 | 2 | 10,000 | 20,000 |
| TC3 | 5 | 10,000 | 50,000 |
| TC4 | 10 | 50,000 | 500,000 |
| TC5 | 20 | 50,000 | 1,000,000 |
| TC6 | 50 | 50,000 | 2,500,000 |
| TC7 | 100 | 50,000 | 5,000,000 |

Each test case: Experiment A run once, Experiment B run 5 times.

**Machine used:** Acer laptop — AMD Ryzen 5 7530U (6 cores, 12 threads, 2.00 GHz), 8 GB RAM, Windows 11 Home (26H2, build 26300.9457), Java SE 25 LTS (HotSpot 64-bit)

## 5. Output

Screenshots of each test case are in [`screenshots/`](screenshots/). Full text output is in [`output.txt`](output.txt).

## 6. Result analysis

### 6.1 Experiment A — Thread-safe (AtomicLong)

| Threads | Expected | Static count (safe) | Non-static total | Abs. difference | Difference (%) |
|---|---|---|---|---|---|
| 1 | 1,000 | 1,000 | 1,000 | 0 | 0% |
| 2 | 20,000 | 20,000 | 20,000 | 0 | 0% |
| 5 | 50,000 | 50,000 | 50,000 | 0 | 0% |
| 10 | 500,000 | 500,000 | 500,000 | 0 | 0% |
| 20 | 1,000,000 | 1,000,000 | 1,000,000 | 0 | 0% |
| 50 | 2,500,000 | 2,500,000 | 2,500,000 | 0 | 0% |
| 100 | 5,000,000 | 5,000,000 | 5,000,000 | 0 | 0% |

### 6.2 Experiment B — Unsynchronized (plain long), 5 runs each

| Threads | Expected | Run 1 S | Run 2 S | Run 3 S | Run 4 S | Run 5 S | Non-static total | Avg. abs. difference | Avg. difference (%) |
|---|---|---|---|---|---|---|---|---|---|
| 1 | 1,000 | 1,000 | 1,000 | 1,000 | 1,000 | 1,000 | 1,000 | 0 | 0.0000% |
| 2 | 20,000 | 16,971 | 16,756 | 18,646 | 17,183 | 14,686 | 20,000 | 3,151.6 | 15.7580% |
| 5 | 50,000 | 20,907 | 19,194 | 15,313 | 18,152 | 20,682 | 50,000 | 31,150.4 | 62.3008% |
| 10 | 500,000 | 71,833 | 81,617 | 68,011 | 68,515 | 72,578 | 500,000 | 427,489.2 | 85.4978% |
| 20 | 1,000,000 | 153,777 | 168,596 | 141,009 | 90,331 | 159,176 | 1,000,000 | 857,422.2 | 85.7422% |
| 50 | 2,500,000 | 705,230 | 918,087 | 466,348 | 515,668 | 950,834 | 2,500,000 | 1,788,766.6 | 71.5507% |
| 100 | 5,000,000 | 2,855,537 | 3,032,145 | 1,791,964 | 1,910,899 | 3,328,159 | 5,000,000 | 2,416,259.2 | 48.3252% |

**Range of P across the 5 runs**

| Threads | Min P | Max P | Spread |
|---|---|---|---|
| 1 | 0% | 0% | 0 |
| 2 | 6.7700% | 26.5700% | 19.80 |
| 5 | 58.1860% | 69.3740% | 11.19 |
| 10 | 83.6766% | 86.3978% | 2.72 |
| 20 | 83.1404% | 90.9669% | 7.83 |
| 50 | 61.9666% | 81.3461% | 19.38 |
| 100 | 33.4368% | 64.1607% | 30.72 |

> Avg. difference (%) = (P₁ + P₂ + P₃ + P₄ + P₅) / 5

## 7. Observation

- **Experiment A:** In every test case S = I = E, so D = 0 and P = 0%. `AtomicLong` never lost an update.
- **Experiment B, 1 thread:** S = E every time. With only one thread there is nobody to interfere, so no race.
- **Experiment B, 2+ threads:** S < E in every run. The non-static total I was always exactly E, because each thread had its own counter.
- **Variation between runs:** the same N and K gave a different S every time. For N = 2, P ranged from 6.77% to 26.57%; for N = 100, from 33.44% to 64.16%. The spread was smallest at N = 10 (2.72 points) and largest at N = 100 (30.72 points).
- **Trend with thread count:** average P rose sharply from 0% (N = 1) to 15.76% (N = 2), 62.30% (N = 5) and about 85.5% (N = 10 and N = 20). After that it **fell**: 71.55% at N = 50 and 48.33% at N = 100. So the loss is **not monotonic** — it peaked around 10–20 threads.
- **Why it fell at high N:** the CPU (Ryzen 5 7530U) can run only 12 threads at the same time. With 50 or 100 threads there are far more threads than hardware threads. Thread creation and start-up take time, so early threads may finish much of their work before later ones start, and the OS time-slices them, so fewer threads truly overlap at the same moment. Fewer real overlaps → fewer lost updates in percentage terms.

## 8. Analysis questions

**1. Difference between static and non-static variable?**
A static variable belongs to the class; only one copy exists and all objects share it. A non-static (instance) variable belongs to an object; every object has its own copy.

**2. Why do all threads share the same static counter?**
Because there is only one copy of a static variable for the whole class. Every thread's object points to that same memory location.

**3. Why does each thread have its own non-static counter?**
Each thread is given a separate `Shahariar_Thread` object. `instanceCount` lives inside that object, so each thread increments only its own copy.

**4. Why is `join()` required before calculating the final counts?**
`join()` makes `main` wait until each thread finishes. Without it, `main` could read the counters while threads are still running and get incomplete values. `join()` also guarantees the thread's writes are visible to `main`.

**5. Why can the unsynchronized static count be lower than expected?**
`count++` is three steps: read, add 1, write. Two threads can read the same value, both add 1, and both write the same result. One increment is lost. This is a race condition.

**6. Does increasing threads always increase the percentage difference?**
No. More threads give more chances for a race, so loss usually appears once N ≥ 2. But the value is not monotonic. It depends on how many threads truly run at the same time (CPU cores), OS scheduling, and JIT optimization. When there are more threads than cores, many threads wait instead of running together, so the loss can even go down. In my runs, average P was 85.50% at N = 10 and 85.74% at N = 20, but it dropped to 71.55% at N = 50 and 48.33% at N = 100.

**7. Why might two runs with the same thread count give different results?**
Thread scheduling is decided by the OS and is different on every run. The exact moments when threads overlap change, so the number of lost updates also changes. Background load and JIT timing add more randomness.

**8. What changes when `AtomicLong` is used instead of `long`?**
`incrementAndGet()` does read-add-write as one atomic step (using CPU compare-and-swap). No update is lost, so S always equals E. It is a bit slower than a plain `long` but correct.

**9. How to make all threads share one instance counter as well?**
Create only one object and pass the same object to all threads, e.g. `Shahariar_Thread shared = new Shahariar_Thread(k, true);` and `new Thread(shared)` for every thread. Then the instance counter is also shared, so it must be made thread-safe too — use `AtomicLong` for it, or put the increment inside a `synchronized` block/method.

## 9. Conclusion

Static vs non-static only decides **who owns** a variable, not whether it is **thread-safe**. A static variable is shared by all threads, so concurrent updates need `AtomicLong` or `synchronized`. Instance counters were always correct because no other thread touched them.

The unsynchronized experiment showed lost updates whenever more than one thread ran, and the results changed from run to run. The percentage difference did not grow strictly with thread count; it depended on scheduling, CPU cores and runtime conditions. The thread-safe experiment was the control case: S = I in every test and the difference stayed at 0%.
