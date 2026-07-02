import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Patient.java
 * Class đại diện cho một Bệnh nhân trong hệ thống Triage bệnh viện thông minh.
 */
public class Patient {
    
    // Các trạng thái khám
    public static final String STATUS_WAITING = "waiting";
    public static final String STATUS_EXAMINING = "examining";
    public static final String STATUS_DONE = "done";

    // Các mức độ ưu tiên
    public static final int PRIORITY_EMERGENCY = 1; // Nguy kịch (Emergency)
    public static final int PRIORITY_URGENT    = 2; // Cấp cứu (Urgent)
    public static final int PRIORITY_NORMAL    = 3; // Thường (Normal)
    public static final int PRIORITY_MILD      = 4; // Nhẹ (Mild)

    private String id;           // Mã bệnh nhân (VD: BN-0001)
    private String name;         // Họ và tên
    private int    age;          // Tuổi
    private String gender;       // Giới tính
    private String phone;        // Số điện thoại
    private String symptom;      // Triệu chứng
    private int    priority;     // Mức ưu tiên (1 -> 4)
    private String status;       // Trạng thái (waiting / examining / done)
    private String doctor;       // Bác sĩ đảm nhận
    private String room;         // Phòng khám
    
    // Mốc thời gian
    private String registeredAt; // Đăng ký lúc
    private String calledAt;     // Gọi khám lúc
    private String completedAt;  // Hoàn thành lúc

    // Lịch sử bệnh án (Sơ đồ lớp liên kết writes/has)
    private List<MedicalRecord> medicalHistory;

    public Patient(String id, String name, int age, String gender, String phone, String symptom, int priority) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.phone = phone;
        this.symptom = symptom;
        this.priority = priority;
        this.status = STATUS_WAITING;
        this.doctor = "";
        this.room = "";
        this.registeredAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy"));
        this.calledAt = "";
        this.completedAt = "";
        this.medicalHistory = new ArrayList<>();
    }

    // Thêm bệnh án mới
    public void addMedicalRecord(MedicalRecord record) {
        this.medicalHistory.add(record);
    }

    // Lấy danh sách bệnh án
    public List<MedicalRecord> getMedicalHistory() {
        return this.medicalHistory;
    }

    // ── Getters and Setters ───────────────────────────────────────────
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getSymptom() { return symptom; }
    public void setSymptom(String symptom) { this.symptom = symptom; }

    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDoctor() { return doctor; }
    public void setDoctor(String doctor) { this.doctor = doctor; }

    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }

    public String getRegisteredAt() { return registeredAt; }
    public void setRegisteredAt(String registeredAt) { this.registeredAt = registeredAt; }

    public String getCalledAt() { return calledAt; }
    public void setCalledAt(String calledAt) { this.calledAt = calledAt; }

    public String getCompletedAt() { return completedAt; }
    public void setCompletedAt(String completedAt) { this.completedAt = completedAt; }

    // ── Label Helpers for View ────────────────────────────────────────

    public String getPriorityLabel() {
        switch (this.priority) {
            case PRIORITY_EMERGENCY: return "1 - Nguy kich (Emergency)";
            case PRIORITY_URGENT:    return "2 - Cap cuu (Urgent)";
            case PRIORITY_NORMAL:    return "3 - Thuong (Normal)";
            case PRIORITY_MILD:      return "4 - Nhe (Mild)";
            default:                 return String.valueOf(this.priority);
        }
    }

    public String getStatusLabel() {
        switch (this.status) {
            case STATUS_WAITING:   return "Cho kham";
            case STATUS_EXAMINING: return "Dang kham";
            case STATUS_DONE:      return "Da kham xong";
            default:               return this.status;
        }
    }

    public boolean hasHigherPriorityThan(Patient other) {
        if (this.priority != other.priority) {
            return this.priority < other.priority;
        }
        return this.id.compareTo(other.id) < 0;
    }

    @Override
    public String toString() {
        return "Patient{id='" + id + "', name='" + name + "', priority=" + priority + ", status='" + status + "'}";
    }
}
