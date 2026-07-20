package triage;

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
    private final int insertViolations;   // số lần vượt 50ms
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

    /** Tiêu đề CSV */
    public static String csvHeader() {
        return "Structure,DataSize,AvgInsert(ms),MaxInsert(ms),"
             + "AvgExtract(ms),MaxExtract(ms),InsertVio>50ms,ExtractVio>50ms";
    }

    /** Một dòng CSV */
    public String toCsvRow() {
        return String.format("%s,%d,%.4f,%.4f,%.4f,%.4f,%d,%d",
                structureName, dataSize,
                avgInsertMs, maxInsertMs,
                avgExtractMs, maxExtractMs,
                insertViolations, extractViolations);
    }

    @Override
    public String toString() {
        return String.format(
            "[%s | n=%d] Insert avg=%.4fms max=%.4fms vio=%d | "
          + "Extract avg=%.4fms max=%.4fms vio=%d",
            structureName, dataSize,
            avgInsertMs, maxInsertMs, insertViolations,
            avgExtractMs, maxExtractMs, extractViolations);
    }
}
