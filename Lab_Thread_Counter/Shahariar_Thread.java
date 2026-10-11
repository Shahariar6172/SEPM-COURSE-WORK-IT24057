import java.util.concurrent.atomic.AtomicLong;

// Lab: Comparing Static and Non-Static Variables in Multithreading
// Usage: java Shahariar_Thread <N threads> <K increments> <true|false>
//   true  -> Experiment A: static AtomicLong (thread-safe)
//   false -> Experiment B: static long (unsynchronized)
public class Shahariar_Thread implements Runnable {

    // Static counters: one copy shared by ALL objects (all threads)
    static AtomicLong safeStatic = new AtomicLong(0);
    static long unsafeStatic = 0;

    // Non-static counter: each object (each thread) has its own copy
    long instanceCount = 0;

    int k;
    boolean safeMode;

    Shahariar_Thread(int k, boolean safeMode) {
        this.k = k;
        this.safeMode = safeMode;
    }

    // Each thread runs this method (works as the CounterTask)
    public void run() {
        for (int i = 0; i < k; i++) {
            if (safeMode) {
                safeStatic.incrementAndGet(); // atomic read-modify-write
            } else {
                unsafeStatic++;               // read, add 1, write (not atomic)
            }
            instanceCount++;                  // private to this object
        }
    }

    public static void main(String[] args) throws InterruptedException {
        if (args.length != 3) {
            System.out.println("Usage: java Shahariar_Thread <N> <K> <true|false>");
            return;
        }
        int n = Integer.parseInt(args[0]);
        int k = Integer.parseInt(args[1]);
        boolean safeMode = Boolean.parseBoolean(args[2]);

        Shahariar_Thread[] tasks = new Shahariar_Thread[n];
        Thread[] threads = new Thread[n];

        // Create one object per thread, then start all threads
        for (int i = 0; i < n; i++) {
            tasks[i] = new Shahariar_Thread(k, safeMode);
            threads[i] = new Thread(tasks[i]);
        }
        for (int i = 0; i < n; i++) {
            threads[i].start();
        }

        // Wait until every thread has finished
        for (int i = 0; i < n; i++) {
            threads[i].join();
        }

        long e = (long) n * k;                                // expected
        long s = safeMode ? safeStatic.get() : unsafeStatic;  // static total
        long sumI = 0;                                        // non-static total
        for (int i = 0; i < n; i++) {
            sumI += tasks[i].instanceCount;
        }
        long d = Math.abs(s - sumI);                          // absolute difference

        String p;
        if (sumI == 0) {
            p = (s == 0) ? "0.0000%" : "undefined";
        } else {
            p = String.format("%.4f%%", (d * 100.0) / sumI);
        }

        System.out.println("Mode               : " + (safeMode ? "true (AtomicLong, thread-safe)" : "false (long, unsynchronized)"));
        System.out.println("Threads (N)        : " + n);
        System.out.println("Increments (K)     : " + k);
        System.out.println("Expected (E = N*K) : " + e);
        System.out.println("Static count (S)   : " + s);
        System.out.println("Non-static (I)     : " + sumI);
        System.out.println("Abs difference (D) : " + d);
        System.out.println("Difference (P)     : " + p);
    }
}
