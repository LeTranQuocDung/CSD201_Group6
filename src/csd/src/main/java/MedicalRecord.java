import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * MedicalRecord.java
 * Class dai dien cho Ho so benh an duoc tao sau khi kham.
 */
public class MedicalRecord {
    private String recordId;
    private String patientId;
    private String doctorId;
    private Date visitDate;
    private String diagnosis;
    private String prescription;
    private String notes;

    public MedicalRecord(String recordId, String patientId, String doctorId,
            String diagnosis, String prescription, String notes) {
        this.recordId = recordId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.visitDate = new Date(); // Thoi gian hien tai
        this.diagnosis = diagnosis;
        this.prescription = prescription;
        this.notes = notes;
    }

    // Getters and Setters
    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public Date getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(Date visitDate) {
        this.visitDate = visitDate;
    }

    public String getFormattedVisitDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        return sdf.format(visitDate);
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getPrescription() {
        return prescription;
    }

    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "Record " + recordId + " | Chuan doan: " + diagnosis;
    }
}
