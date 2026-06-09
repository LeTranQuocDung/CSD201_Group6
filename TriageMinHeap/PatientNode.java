
// ================================================================
//  FILE: PatientNode.java
// ================================================================

public class PatientNode {
    
    private Patient data;

    private String patientID;       // ID duy nhất của bệnh nhân
    private int    priorityScore;   // 1 = nguy kịch nhất, 4 = nhẹ nhất

    // Constructor
    public PatientNode(Patient data) {
        this.data = data;
        this.patientID = data.getId();
        this.priorityScore = data.getPriority();
    }

    public String getPatientID() {
        return this.patientID;
    }

    public int getPriorityScore() {
        return this.priorityScore;
    }
    
    public void setPriorityScore(int priority){
        this.data.setPriority(priority);
    }

    

    boolean hasHigherUrgencyThan(PatientNode other) {
        return this.data.hasHigherPriorityThan(other.data);
    }

    @Override
    public String toString() {
        return String.format("Patient{ id='%s', priority=%d }",
                             this.patientID, this.priorityScore);
    }
}
