package triage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Đo thời gian insert và extractMin của một TriageStructure.
 *
 * Quy trình:
 *   1. Warm-up: chạy 100 lần để JIT compile xong
 *   2. Đo ITERATIONS lần, ghi nanoseconds từng lần
 *   3. Tính avg, max, và đếm số lần vượt ngưỡng 50ms
 */
public class BenchmarkRunner {

    private static final int WARM_UP   = 100;
    private static final int ITERATIONS = 1000;
    private static final double THRESHOLD_MS = 50.0;
    private static final Random RANDOM = new Random(42);

    /**
     * Chạy benchmark cho một cấu trúc với kích thước dữ liệu cho trước.
     */
    public BenchmarkResult run(TriageStructure structure, int dataSize) {

        // ---- 1. Warm-up ----
        for (int i = 0; i < WARM_UP; i++) {
            structure.insert(randomPatient(i));
        }
        while (structure.size() > 0) structure.extractMin();

        // ---- 2. Đo INSERT ----
        long[] insertNanos = new long[ITERATIONS];
        for (int i = 0; i < ITERATIONS; i++) {
            while (structure.size() < dataSize) {
                structure.insert(randomPatient(RANDOM.nextInt(10000)));
            }
            Patient p = randomPatient(i);
            long start = System.nanoTime();
            structure.insert(p);
            insertNanos[i] = System.nanoTime() - start;
            structure.extractMin();
        }

        // ---- 3. Đo EXTRACT-MIN ----
        long[] extractNanos = new long[ITERATIONS];
        for (int i = 0; i < ITERATIONS; i++) {
            while (structure.size() < dataSize) {
                structure.insert(randomPatient(RANDOM.nextInt(10000)));
            }
            long start = System.nanoTime();
            Patient extracted = structure.extractMin();
            extractNanos[i] = System.nanoTime() - start;
            if (extracted != null) structure.insert(extracted);
        }

        while (structure.size() > 0) structure.extractMin();

        return calcResult(structure.getName(), dataSize, insertNanos, extractNanos);
    }

    private BenchmarkResult calcResult(String name, int dataSize,
                                       long[] insertNanos, long[] extractNanos) {
        double avgIns = 0, maxIns = 0;
        double avgExt = 0, maxExt = 0;
        int vioIns = 0, vioExt = 0;

        for (long ns : insertNanos) {
            double ms = ns / 1_000_000.0;
            avgIns += ms;
            if (ms > maxIns) maxIns = ms;
            if (ms > THRESHOLD_MS) vioIns++;
        }
        for (long ns : extractNanos) {
            double ms = ns / 1_000_000.0;
            avgExt += ms;
            if (ms > maxExt) maxExt = ms;
            if (ms > THRESHOLD_MS) vioExt++;
        }

        avgIns /= insertNanos.length;
        avgExt /= extractNanos.length;

        return new BenchmarkResult(name, dataSize,
                avgIns, maxIns, avgExt, maxExt, vioIns, vioExt);
    }

    private Patient randomPatient(int id) {
        int priority = RANDOM.nextInt(10) + 1;
        return new Patient(id, "P" + id, priority);
    }

    /**
     * Mô phỏng tải 200 updates/phút với n=dataSize.
     *
     * FIX: Giữ size ổn định bằng cách xen kẽ cân bằng:
     *   - i%3==0: priority update (extract + đổi priority + re-insert) → size giữ nguyên
     *   - i%3==1: insert mới (size +1)
     *   - i%3==2: extract để cân bằng (size -1)
     * → Kích thước dao động ±1 quanh dataSize, không tăng mãi.
     */
    public List<Double> simulateLoad(TriageStructure structure, int dataSize) {
        List<Double> times = new ArrayList<Double>();

        // Nạp dữ liệu ban đầu
        for (int i = 0; i < dataSize; i++) {
            structure.insert(randomPatient(i));
        }

        // 200 operations, xen kẽ để giữ size ổn định
        for (int i = 0; i < 200; i++) {
            if (i % 3 == 0) {
                // Priority update: extract + change priority + re-insert (size giữ nguyên)
                long start = System.nanoTime();
                Patient p = structure.extractMin();
                if (p != null) {
                    p.setPriority(RANDOM.nextInt(10) + 1);
                    structure.insert(p);
                }
                times.add((System.nanoTime() - start) / 1_000_000.0);
            } else if (i % 3 == 1) {
                // Insert new patient (size +1)
                long start = System.nanoTime();
                structure.insert(randomPatient(dataSize + i));
                times.add((System.nanoTime() - start) / 1_000_000.0);
            } else {
                // Extract to balance the insert above (size -1)
                long start = System.nanoTime();
                structure.extractMin();
                times.add((System.nanoTime() - start) / 1_000_000.0);
            }
        }

        while (structure.size() > 0) structure.extractMin();
        return times;
    }
}
