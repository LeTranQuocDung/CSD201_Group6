package hospital.system.model;

public class MedicalRecordNode {
    public String recordId;
    public String date;
    public String diagnosis;
    
    public MedicalRecordNode next; 
    public MedicalRecordNode prev; 
    
    public MedicalRecordNode(String recordId, String date, String diagnosis) {
        this.recordId = recordId;
        this.date = date;
        this.diagnosis = diagnosis;
        this.next = null;
        this.prev = null;
    }
    
    public void printInfo() {
        System.out.printf("[Mã BA: %s | Ngày: %s | Bệnh: %s] ", recordId, date, diagnosis);
    }
}
