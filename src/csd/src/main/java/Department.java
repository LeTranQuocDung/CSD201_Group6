import java.util.ArrayList;
import java.util.List;

/**
 * Department.java
 * Class dai dien cho Khoa trong Benh vien.
 */
public class Department {
    private String deptId;
    private String deptName;
    private int floor;
    private List<Doctor> doctors;

    public Department(String deptId, String deptName, int floor) {
        this.deptId = deptId;
        this.deptName = deptName;
        this.floor = floor;
        this.doctors = new ArrayList<>();
    }

    public void addDoctor(Doctor doctor) {
        this.doctors.add(doctor);
    }

    // Getters and Setters
    public String getDeptId() { return deptId; }
    public void setDeptId(String deptId) { this.deptId = deptId; }

    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }

    public int getFloor() { return floor; }
    public void setFloor(int floor) { this.floor = floor; }

    public List<Doctor> getDoctors() { return doctors; }

    @Override
    public String toString() {
        return deptName + " (Tang " + floor + ")";
    }
}
