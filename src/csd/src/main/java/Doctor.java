/**
 * Doctor.java
 * Class dai dien cho Bac si truc dieu phoi cap cuu.
 */
public class Doctor {
    private String doctorId;
    private String doctorName;
    private String room;
    private String deptId;
    private boolean available;

    public Doctor(String doctorId, String doctorName, String room, String deptId) {
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.room = room;
        this.deptId = deptId;
        this.available = true; // Mac dinh san sang lam viec
    }

    // Getters and Setters
    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }

    public String getDeptId() { return deptId; }
    public void setDeptId(String deptId) { this.deptId = deptId; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    @Override
    public String toString() {
        return doctorName + " (" + room + ")";
    }
}
