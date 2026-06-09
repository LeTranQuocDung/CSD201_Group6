///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//
///**
// *
// * @author PHUONGTHAO
// */
public class Patient {

    /* ── Hằng số mức ưu tiên ─────────────────────────────────
       Số CÀNG NHỎ → ưu tiên CÀNG CAO trong MinHeap            */
    public static final int PRIORITY_EMERGENCY = 1; // Emergency
    public static final int PRIORITY_URGENT   = 2; // Urgent
    public static final int PRIORITY_NORMAL   = 3; // Normal
    public static final int PRIORITY_MILD     = 4; // Mild

    /* ── Hằng số trạng thái ──────────────────────────────────*/
    public static final String STATUS_WAITING   = "waiting";
    public static final String STATUS_EXAMINING = "examining";
    public static final String STATUS_DONE      = "done";

    /* ── Thuộc tính bệnh nhân ────────────────────────────────*/
    private final String id;            // ID duy nhất: BN-XXXX
    private final String name;          // Họ và tên
    private final int    age;           // Tuổi
    private final String gender;        // Giới tính
    private final String phone;         // Số điện thoại
    private final String symptom;       // Triệu chứng
    private final String registeredAt;  // Thời điểm đăng ký (dùng tie-breaker trong Heap)

    // Các field thay đổi theo trạng thái
    private String status;      // waiting | examining | done
    private String calledAt;    // Thời điểm được gọi khám
    private String completedAt; // Thời điểm hoàn thành khám
    private String doctor;      // Tên bác sĩ phụ trách
    private String room;        // Phòng khám
    private int    priority;      // Mức ưu tiên 1-4

    /* ── Constructor ─────────────────────────────────────────*/
    public Patient(String id, String name, int age,
                   String gender, String phone,
                   String symptom, int priority) {

        this.id           = id;
        this.name         = name;
        this.age          = (age > 0 && age <= 150) ? age : 1;
        this.gender       = gender;
        this.phone        = phone;
        this.symptom      = symptom;
        this.priority     = (priority >= 1 && priority <= 4)
                            ? priority : PRIORITY_NORMAL;
        this.status       = STATUS_WAITING;
        this.registeredAt = java.time.LocalDateTime.now()
                            .format(java.time.format.DateTimeFormatter
                            .ofPattern("HH:mm:ss dd/MM/yyyy"));
        this.calledAt     = null;
        this.completedAt  = null;
        this.doctor       = null;
        this.room         = null;
    }

    /* ── Getters ─────────────────────────────────────────────*/
public String getId()           { return id; }
    public String getName()         { return name; }
    public int    getAge()          { return age; }
    public String getGender()       { return gender; }
    public String getPhone()        { return phone; }
    public String getSymptom()      { return symptom; }
    public int    getPriority()     { return priority; }
    public String getRegisteredAt() { return registeredAt; }
    public String getStatus()       { return status; }
    public String getCalledAt()     { return calledAt; }
    public String getCompletedAt()  { return completedAt; }
    public String getDoctor()       { return doctor; }
    public String getRoom()         { return room; }

    /* ── Setters (chỉ field có thể thay đổi) ─────────────────*/
    public void setStatus(String status)    { this.status      = status; }
    public void setCalledAt(String t)       { this.calledAt    = t; }
    public void setCompletedAt(String t)    { this.completedAt = t; }
    public void setDoctor(String doctor)    { this.doctor      = doctor; }
    public void setRoom(String room)        { this.room        = room; }
    public void setPriority(int priority)   { this.priority    = priority; }
    /* ── Nhãn mức ưu tiên ────────────────────────────────────*/
    public String getPriorityLabel() {
        switch (priority) {
            case PRIORITY_EMERGENCY: return "[!!!] Emergency";
            case PRIORITY_URGENT:    return "[!! ] Urgent";
            case PRIORITY_NORMAL:    return "[ ! ] Normal";
            case PRIORITY_MILD:      return "[   ] Mild";
            default:                 return "Unknown";
        }
    }

    /* ── Nhãn trạng thái ─────────────────────────────────────*/
    public String getStatusLabel() {
        switch (status) {
            case STATUS_WAITING:   return "Cho kham";
            case STATUS_EXAMINING: return "Dang kham";
            case STATUS_DONE:      return "Hoan thanh";
            default:               return status;
        }
    }

    /* ── Kiểm tra priority hợp lệ ───────────────────────────*/
    public static boolean isValidPriority(int p) {
        return p >= 1 && p <= 4;
    }

    /* ── So sánh cho MinHeap ─────────────────────────────────
       Trả true nếu this có ưu tiên CAO HƠN other.
       Ưu tiên cao hơn = priority nhỏ hơn.
       Nếu bằng nhau → ai đăng ký TRƯỚC được gọi trước (FIFO). */
    public boolean hasHigherPriorityThan(Patient other) {
        if (this.priority != other.priority) {
            return this.priority < other.priority;
        }
        // Tie-breaker: thời gian đăng ký sớm hơn thắng
return this.registeredAt.compareTo(other.registeredAt) < 0;
    }

    /* ── toString ────────────────────────────────────────────*/
    @Override
    public String toString() {
        return String.format(
            "%-12s | %-22s | %3d | %-18s | %-14s | %s",
            id, name, age, getPriorityLabel(), getStatusLabel(), registeredAt
        );
    }
}
