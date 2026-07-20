package triage;

import java.util.List;
import java.util.ArrayList;

/**
 * Stores the result of one simulation run (500 patients).
 */
public class MisorderingResult {

    private final String pqName;
    private final int    totalPatients;
    private final int    misorderCount;
    private final double misorderRate;      // percentage 0-100

    // Breakdown by severity of misordering
    private final int    urgentMisordered;       // urgent patient served late
    private final int    semiUrgentMisordered;   // semi-urgent served late
    private final List<String> misorderLog;      // detail of each misordering

    public MisorderingResult(String pqName, int totalPatients,
                             int misorderCount, int urgentMisordered,
                             int semiUrgentMisordered,
                             List<String> misorderLog) {
        this.pqName               = pqName;
        this.totalPatients        = totalPatients;
        this.misorderCount        = misorderCount;
        this.misorderRate         = (misorderCount * 100.0) / totalPatients;
        this.urgentMisordered     = urgentMisordered;
        this.semiUrgentMisordered = semiUrgentMisordered;
        this.misorderLog          = misorderLog;
    }

    public String getPqName()              { return pqName; }
    public int    getTotalPatients()       { return totalPatients; }
    public int    getMisorderCount()       { return misorderCount; }
    public double getMisorderRate()        { return misorderRate; }
    public int    getUrgentMisordered()    { return urgentMisordered; }
    public int    getSemiUrgentMisordered(){ return semiUrgentMisordered; }
    public List<String> getMisorderLog()   { return misorderLog; }

    @Override
    public String toString() {
        return String.format(
            "%s: %d/%d misordered (%.1f%%) | Urgent late: %d | Semi-Urgent late: %d",
            pqName, misorderCount, totalPatients, misorderRate,
            urgentMisordered, semiUrgentMisordered);
    }
}
