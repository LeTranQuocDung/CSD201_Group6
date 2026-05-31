import java.util.List;

/**
 * HospitalService.java
 * Tầng nghiệp vụ: sử dụng DoublyLinkedList để quản lý lịch sử bệnh án.
 * Cung cấp các thao tác cấp cao cho menu console.
 */
public class HospitalService {

    private final DoublyLinkedList historyList; // DLL lưu toàn bộ bệnh án
    private int patientCounter;                 // Bộ đếm để sinh ID

    // Danh sách bác sĩ xoay vòng (đơn giản, không dùng CLL ở đây)
    private static final String[][] DOCTORS = {
        {"BS. Nguyen Van Minh",  "Noi khoa",  "Phong 101"},
        {"BS. Tran Thi Hoa",     "Nhi khoa",  "Phong 102"},
        {"BS. Le Hoang Nam",     "Ngoai khoa","Phong 103"},
        {"BS. Pham Thu Trang",   "Da lieu",   "Phong 104"},
        {"BS. Vo Minh Khoa",     "Tim mach",  "Phong 105"},
    };
    private int doctorIndex = 0; // Index bác sĩ hiện tại (Round-Robin)

    public HospitalService() {
        this.historyList   = new DoublyLinkedList();
        this.patientCounter = 0;
    }

    // ── Sinh ID bệnh nhân ────────────────────────────────────
    private String generateId() {
        patientCounter++;
        return String.format("BN-%04d", patientCounter);
    }

    // ── Lấy bác sĩ tiếp theo (Round-Robin) ──────────────────
    private String[] nextDoctor() {
        String[] doc = DOCTORS[doctorIndex % DOCTORS.length];
        doctorIndex++;
        return doc;
    }

    /* ══════════════════════════════════════════════════════════
       ĐĂNG KÝ BỆNH NHÂN MỚI — push() vào tail DLL
       ══════════════════════════════════════════════════════════ */
    public Patient register(String name, int age, String gender,
                            String phone, String symptom, int priority) {
        String id = generateId();
        Patient p = new Patient(id, name, age, gender, phone, symptom, priority);
        historyList.push(p); // O(1) — thêm vào cuối DLL
        return p;
    }

    /* ══════════════════════════════════════════════════════════
       GỌI KHÁM — cập nhật trạng thái, phân công bác sĩ
       ══════════════════════════════════════════════════════════ */
    public Patient callPatient(String id) {
        Patient p = historyList.findById(id);
        if (p == null) return null;
        if (!p.getStatus().equals(Patient.STATUS_WAITING)) return null;

        String[] doc = nextDoctor();
        historyList.updateStatus(id, Patient.STATUS_EXAMINING, doc[0], doc[2]);
        return historyList.findById(id);
    }

    /* ══════════════════════════════════════════════════════════
       HOÀN THÀNH KHÁM
       ══════════════════════════════════════════════════════════ */
    public boolean completeExam(String id) {
        Patient p = historyList.findById(id);
        if (p == null || !p.getStatus().equals(Patient.STATUS_EXAMINING)) return false;
        return historyList.updateStatus(id, Patient.STATUS_DONE, null, null);
    }

    /* ══════════════════════════════════════════════════════════
       LỊCH SỬ — duyệt ngược DLL (mới nhất trước)
       ══════════════════════════════════════════════════════════ */
    public List<Patient> getHistory()          { return historyList.toListReverse(); }
    public List<Patient> getWaiting()          { return historyList.filterByStatus(Patient.STATUS_WAITING); }
    public List<Patient> getExamining()        { return historyList.filterByStatus(Patient.STATUS_EXAMINING); }
    public List<Patient> getByPriority(int p)  { return historyList.filterByPriority(p); }

    /* ══════════════════════════════════════════════════════════
       THỐNG KÊ
       ══════════════════════════════════════════════════════════ */
    public int[] getStats() { return historyList.countByStatus(); }
    public int   getTotal() { return historyList.getSize(); }

    /* ══════════════════════════════════════════════════════════
       XÓA
       ══════════════════════════════════════════════════════════ */
    public boolean deleteById(String id) { return historyList.deleteById(id); }
    public void    clearAll()            { historyList.clear(); }

    /* ══════════════════════════════════════════════════════════
       NAVIGATOR — duyệt hai chiều
       ══════════════════════════════════════════════════════════ */
    public Navigator navigatorFromHead() { return historyList.navigatorFromHead(); }
    public Navigator navigatorFromTail() { return historyList.navigatorFromTail(); }

    /* ══════════════════════════════════════════════════════════
       VISUALIZATION
       ══════════════════════════════════════════════════════════ */
    public void printDiagram() { historyList.printDiagram(); }
    public void printTable()   { historyList.printTable(); }
}
