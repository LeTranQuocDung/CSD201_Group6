
// ================================================================
//  FILE: PatientNode.java
// ================================================================

public class PatientNode {

    private String patientID;       // ID duy nhất của bệnh nhân
    private int    priorityScore;   // 1 = nguy kịch nhất, 4 = nhẹ nhất
    private long   timestamp;       // Thời điểm nhập viện (ms)

    // Constructor
    public PatientNode(String patientID, int priorityScore) {
        // Cập nhật điều kiện kiểm tra từ 10 xuống 4
        if (priorityScore < 1 || priorityScore > 4)
            throw new IllegalArgumentException(
                "PriorityScore phải trong [1, 4]. Nhận: " + priorityScore);
                
        this.patientID     = patientID;
        this.priorityScore = priorityScore;
        this.timestamp     = System.currentTimeMillis();
    }

    public String getPatientID() {
        return this.patientID;
    }

    public void setPatientID(String patientID) {
        this.patientID = patientID;
    }

    public int getPriorityScore() {
        return this.priorityScore;
    }

    public void setPriorityScore(int priorityScore) {
        this.priorityScore = priorityScore;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    

    boolean hasHigherUrgencyThan(PatientNode other) {
        if (this.priorityScore != other.priorityScore)
            return this.priorityScore < other.priorityScore;
        return this.timestamp < other.timestamp;
    }

    @Override
    public String toString() {
        return String.format("Patient{ id='%s', priority=%d }",
                             this.patientID, this.priorityScore);
    }
}
