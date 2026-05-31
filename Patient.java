/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author PHUONGTHAO
 */
public class Patient {

    /* ── Hằng số mức ưu tiên ─────────────────────────────────
       Số CÀNG NHỎ → ưu tiên CÀNG CAO trong MinHeap            */
    public static final int PRIORITY_NGUY_HIEM      = 1; // Nguy hiểm
    public static final int PRIORITY_NGHIEM_TRONG   = 2; // Nghiêm trọng
    public static final int PRIORITY_TRUNG_BINH     = 3; // Trung bình
    public static final int PRIORITY_NHE            = 4; // Nhẹ

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
    private final int    priority;      // Mức ưu tiên 1-4
    private final String registeredAt;  // Thời điểm đăng ký (dùng tie-breaker trong Heap)

    // Các field thay đổi theo trạng thái
    private String status;      // waiting | examining | done
    private String calledAt;    // Thời điểm được gọi khám
    private String completedAt; // Thời điểm hoàn thành khám
    private String doctor;      // Tên bác sĩ phụ trách
    private String room;        // Phòng khám

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
                            ? priority : PRIORITY_TRUNG_BINH;
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
    public String getId()           { return this.id; }
    public String getName()         { return this.name; }
    public int    getAge()          { return this.age; }
    public String getGender()       { return this.gender; }
    public String getPhone()        { return this.phone; }
    public String getSymptom()      { return this.symptom; }
    public int    getPriority()     { return this.priority; }
    public String getRegisteredAt() { return this.registeredAt; }
    public String getStatus()       { return this.status; }
    public String getCalledAt()     { return this.calledAt; }
    public String getCompletedAt()  { return this.completedAt; }
    public String getDoctor()       { return this.doctor; }
    public String getRoom()         { return this.room; }

    /* ── Setters (chỉ field có thể thay đổi) ─────────────────*/
    public void setStatus(String status)    { this.status      = status; }
    public void setCalledAt(String t)       { this.calledAt    = t; }
    public void setCompletedAt(String t)    { this.completedAt = t; }
    public void setDoctor(String doctor)    { this.doctor      = doctor; }
    public void setRoom(String room)        { this.room        = room; }

    /* ── Nhãn mức ưu tiên ────────────────────────────────────*/
    public String getPriorityLabel() {
        switch (priority) {
            case PRIORITY_NGUY_HIEM:    return "[!!!] Nguy hiem";
            case PRIORITY_NGHIEM_TRONG: return "[!! ] Nghiem trong";
            case PRIORITY_TRUNG_BINH:   return "[ ! ] Trung binh";
            case PRIORITY_NHE:          return "[   ] Nhe";
            default:                    return "Khong xac dinh";
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
            this.id, this.name, this.age, getPriorityLabel(), getStatusLabel(), this.registeredAt
        );
    }
}
