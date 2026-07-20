package triage;

import java.util.Locale;

/**
 * Lưu kết quả đo của một lần chạy benchmark.
 */
public class BenchmarkResult {

    private final String structureName;
    private final int dataSize;
    private final double avgInsertMs;
    private final double maxInsertMs;
    private final double avgExtractMs;
    private final double maxExtractMs;
    private final int insertViolations;
    private final int extractViolations;

    public BenchmarkResult(String structureName, int dataSize,
                           double avgInsertMs, double maxInsertMs,
                           double avgExtractMs, double maxExtractMs,
                           int insertViolations, int extractViolations) {
        this.structureName   = structureName;
        this.dataSize        = dataSize;
        this.avgInsertMs     = avgInsertMs;
        this.maxInsertMs     = maxInsertMs;
        this.avgExtractMs    = avgExtractMs;
        this.maxExtractMs    = maxExtractMs;
        this.insertViolations  = insertViolations;
        this.extractViolations = extractViolations;
    }

    // ---- Getters ----
    public String getStructureName()   { return structureName; }
    public int    getDataSize()        { return dataSize; }
    public double getAvgInsertMs()     { return avgInsertMs; }
    public double getMaxInsertMs()     { return maxInsertMs; }
    public double getAvgExtractMs()    { return avgExtractMs; }
    public double getMaxExtractMs()    { return maxExtractMs; }
    public int    getInsertViolations()  { return insertViolations; }
    public int    getExtractViolations() { return extractViolations; }

    /** Trả về chuỗi JSON biểu diễn kết quả benchmark (dùng Locale.US để đảm bảo dấu chấm thập phân) */
    public String toJson() {
        return String.format(Locale.US,
            "{\"structure\":\"%s\",\"dataSize\":%d,"
            + "\"avgInsertMs\":%.6f,\"maxInsertMs\":%.6f,"
            + "\"avgExtractMs\":%.6f,\"maxExtractMs\":%.6f,"
            + "\"insertViolations\":%d,\"extractViolations\":%d}",
            structureName, dataSize,
            avgInsertMs, maxInsertMs,
            avgExtractMs, maxExtractMs,
            insertViolations, extractViolations);
    }

    @Override
    public String toString() {
        return String.format(Locale.US,
            "[%s | n=%d] Insert avg=%.4fms max=%.4fms vio=%d | "
          + "Extract avg=%.4fms max=%.4fms vio=%d",
            structureName, dataSize,
            avgInsertMs, maxInsertMs, insertViolations,
            avgExtractMs, maxExtractMs, extractViolations);
    }
}
