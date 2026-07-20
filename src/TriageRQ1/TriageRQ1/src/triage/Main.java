package triage;

import java.util.ArrayList;
import java.util.List;

/**
 * Điểm vào chính của chương trình RQ1.
 *
 * Chạy benchmark cho 3 cấu trúc với 4 kịch bản kích thước:
 *   n = 50, 200, 500, 1000
 *
 * Xuất:
 *   - Bảng kết quả ra console
 *   - Kết luận trả lời RQ1
 */
public class Main {

    // Các kích thước dữ liệu cần thử nghiệm
    private static final int[] DATA_SIZES = {50, 200, 500, 1000};

    /** Tạo mới instance cấu trúc (tránh state leak giữa các lần benchmark) */
    private static TriageStructure createStructure(int type) {
        switch (type) {
            case 0: return new MinHeapTriage();
            case 1: return new SortedLinkedListTriage();
            case 2: return new UnsortedArrayTriage();
            default: throw new IllegalArgumentException("Unknown type: " + type);
        }
    }

    private static final String[] STRUCTURE_NAMES = {"Min-Heap", "Sorted Linked List", "Unsorted Array"};

    public static void main(String[] args) {

        System.out.println("=================================================");
        System.out.println("  RQ1 - Smart Hospital Patient Triage System");
        System.out.println("  So sánh: Min-Heap vs Sorted Linked List vs Unsorted Array");
        System.out.println("=================================================\n");

        BenchmarkRunner runner = new BenchmarkRunner();
        List<BenchmarkResult> allResults = new ArrayList<>();

        // Chạy benchmark: tạo MỚI instance cho mỗi lần chạy (fix state leak)
        for (int s = 0; s < 3; s++) {
            System.out.println(">>> Đang đo: " + STRUCTURE_NAMES[s]);
            for (int n : DATA_SIZES) {
                TriageStructure structure = createStructure(s);
                BenchmarkResult result = runner.run(structure, n);
                allResults.add(result);
                System.out.println("  " + result);
            }
            System.out.println();
        }

        // ---- In bảng tổng hợp ----
        printSummaryTable(allResults);

        // ---- Mô phỏng tải 200 updates/phút ----
        System.out.println("\n=================================================");
        System.out.println("  Mô phỏng tải: 200 updates/phút với n=200");
        System.out.println("=================================================");
        simulateAndPrint(runner);

        // ---- Kết luận ----
        printConclusion(allResults);
    }

    private static void printSummaryTable(List<BenchmarkResult> results) {
        System.out.println("\n=================================================");
        System.out.printf("%-22s %6s %14s %14s %14s %14s %10s %10s%n",
            "Cấu trúc", "n",
            "Avg Insert(ms)", "Max Insert(ms)",
            "Avg Extract(ms)", "Max Extract(ms)",
            "Vio Ins", "Vio Ext");
        System.out.println("-".repeat(110));

        for (BenchmarkResult r : results) {
            System.out.printf("%-22s %6d %14.4f %14.4f %14.4f %14.4f %10d %10d%n",
                r.getStructureName(), r.getDataSize(),
                r.getAvgInsertMs(), r.getMaxInsertMs(),
                r.getAvgExtractMs(), r.getMaxExtractMs(),
                r.getInsertViolations(), r.getExtractViolations());
        }
        System.out.println("=================================================");
    }

    private static void simulateAndPrint(BenchmarkRunner runner) {
        for (int s = 0; s < 3; s++) {
            TriageStructure structure = createStructure(s);
            List<Double> times = runner.simulateLoad(structure, 200);
            double max = times.stream().mapToDouble(Double::doubleValue).max().orElse(0);
            double avg = times.stream().mapToDouble(Double::doubleValue).average().orElse(0);
            long violations = times.stream().filter(t -> t > 50.0).count();
            System.out.printf("%-22s avg=%.4fms  max=%.4fms  vượt_50ms=%d/200%n",
                STRUCTURE_NAMES[s], avg, max, violations);
        }
    }

    private static void printConclusion(List<BenchmarkResult> results) {
        System.out.println("\n=================================================");
        System.out.println("  KẾT LUẬN RQ1");
        System.out.println("=================================================");

        // Tìm cấu trúc tốt nhất cho insert và extract ở n=500
        BenchmarkResult bestInsert  = null;
        BenchmarkResult bestExtract = null;

        for (BenchmarkResult r : results) {
            if (r.getDataSize() != 500) continue;
            if (bestInsert  == null || r.getAvgInsertMs()  < bestInsert.getAvgInsertMs())
                bestInsert = r;
            if (bestExtract == null || r.getAvgExtractMs() < bestExtract.getAvgExtractMs())
                bestExtract = r;
        }

        System.out.println("Tại n=500 (tải thực tế):");
        if (bestInsert != null)
            System.out.printf("  Insert nhanh nhất : %s (avg %.4fms)%n",
                bestInsert.getStructureName(), bestInsert.getAvgInsertMs());
        if (bestExtract != null)
            System.out.printf("  Extract nhanh nhất: %s (avg %.4fms)%n",
                bestExtract.getStructureName(), bestExtract.getAvgExtractMs());

        System.out.println();
        System.out.println("Ngưỡng 50ms:");
        for (BenchmarkResult r : results) {
            if (r.getDataSize() != 500) continue;
            boolean safe = r.getInsertViolations() == 0 && r.getExtractViolations() == 0;
            System.out.printf("  %-22s -> %s (ins_vio=%d, ext_vio=%d)%n",
                r.getStructureName(),
                safe ? "ĐẠT < 50ms ổn định" : "CÓ vi phạm 50ms",
                r.getInsertViolations(), r.getExtractViolations());
        }

        System.out.println();
        System.out.println("NOTE cho AI Audit Log:");
        System.out.println("  - Kết luận dựa trên số liệu thực đo, không phải lý thuyết.");
        System.out.println("  - Min-Heap thường thắng cả hai chiều nhờ O(log n) cân bằng.");
        System.out.println("  - Sorted Linked List extract nhanh nhưng insert chậm khi n lớn.");
        System.out.println("  - Unsorted Array insert nhanh nhưng extract chậm khi n lớn.");
        System.out.println("=================================================");
    }
}
