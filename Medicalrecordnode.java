// ================================================================
//  FILE: MedicalRecordNode.java
//  Mô tả: Node của Doubly Linked List lưu bệnh án bệnh nhân.
//         Tách DATA (MedicalRecordNode) khỏi STRUCTURE (DLL)
//         theo nguyên tắc Single Responsibility.
//
//  Cấu trúc 1 node:
//
//    [prev] ←── [recordID | data | prev | next] ──→ [next]
//
// ================================================================

public class MedicalRecordNode {

    // ── THÔNG TIN BỆNH ÁN (DATA) ──────────────────────────────
    String recordID;        // ID duy nhất của bản ghi  (VD: REC-001)
    String patientID;       // ID bệnh nhân chủ sở hữu  (VD: BN-482)
    String date;            // Ngày khám                 (VD: 20/05/2025)
    String diagnosis;       // Chẩn đoán                 (VD: Tăng huyết áp)
    String doctor;          // Bác sĩ phụ trách
    String department;      // Khoa khám
    String prescription;    // Đơn thuốc
    String notes;           // Ghi chú thêm
    String severity;        // Mức độ: Nhẹ | Trung bình | Nặng | Nguy kịch
    double visitCost;       // Chi phí khám (VNĐ)

    // ── CON TRỎ HAI CHIỀU (STRUCTURE) ─────────────────────────
    MedicalRecordNode prev; // → bản ghi CŨ HƠN  (về phía HEAD)
    MedicalRecordNode next; // → bản ghi MỚI HƠN (về phía TAIL)

    // ── CONSTRUCTOR ĐẦY ĐỦ ────────────────────────────────────
    public MedicalRecordNode(String recordID,  String patientID,
                             String date,       String diagnosis,
                             String doctor,     String department,
                             String prescription, String notes,
                             String severity,   double visitCost) {
        // Validate các trường bắt buộc
        if (recordID   == null || recordID.isBlank())
            throw new IllegalArgumentException("recordID không được rỗng.");
        if (patientID  == null || patientID.isBlank())
            throw new IllegalArgumentException("patientID không được rỗng.");
        if (diagnosis  == null || diagnosis.isBlank())
            throw new IllegalArgumentException("Chẩn đoán không được rỗng.");
        if (visitCost  < 0)
            throw new IllegalArgumentException("Chi phí không được âm: " + visitCost);

        this.recordID     = recordID;
        this.patientID    = patientID;
        this.date         = (date        != null) ? date        : "Không rõ";
        this.diagnosis    = diagnosis;
        this.doctor       = (doctor      != null) ? doctor      : "Không rõ";
        this.department   = (department  != null) ? department  : "Không rõ";
        this.prescription = (prescription!= null) ? prescription: "Chưa có";
        this.notes        = (notes       != null) ? notes       : "Không có";
        this.severity     = (severity    != null) ? severity    : "Nhẹ";
        this.visitCost    = visitCost;

        this.prev = null; // mặc định chưa liên kết
        this.next = null;
    }

    // ── CONSTRUCTOR RÚT GỌN (dùng khi demo nhanh) ─────────────
    public MedicalRecordNode(String recordID, String patientID,
                             String date,     String diagnosis,
                             String doctor,   String severity,
                             double visitCost) {
        this(recordID, patientID, date, diagnosis,
             doctor, "Không rõ", "Chưa có", "Không có",
             severity, visitCost);
    }

    // ── HIỂN THỊ CHI TIẾT ─────────────────────────────────────
    public void display() {
        System.out.println("  ┌─────────────────────────────────────────────────┐");
        System.out.printf ("  │  ID Bản ghi  : %-32s│%n", recordID);
        System.out.printf ("  │  Bệnh nhân   : %-32s│%n", patientID);
        System.out.printf ("  │  Ngày khám   : %-32s│%n", date);
        System.out.printf ("  │  Chẩn đoán   : %-32s│%n", truncate(diagnosis, 32));
        System.out.printf ("  │  Bác sĩ      : %-32s│%n", truncate(doctor, 32));
        System.out.printf ("  │  Khoa        : %-32s│%n", department);
        System.out.printf ("  │  Mức độ      : %-32s│%n", severity);
        System.out.printf ("  │  Đơn thuốc   : %-32s│%n", truncate(prescription, 32));
        System.out.printf ("  │  Ghi chú     : %-32s│%n", truncate(notes, 32));
        System.out.printf ("  │  Chi phí     : %-32s│%n",
                           String.format("%,.0f VNĐ", visitCost));
        System.out.println("  └─────────────────────────────────────────────────┘");
    }

    // ── HIỂN THỊ NGẮN (dùng trong danh sách) ─────────────────
    public void displayShort() {
        System.out.printf("  %-12s | %-10s | %-22s | %-12s | %,.0f VNĐ%n",
                          recordID, date, truncate(diagnosis, 22),
                          severity, visitCost);
    }

    // ── TIỆN ÍCH ──────────────────────────────────────────────
    private String truncate(String s, int max) {
        if (s == null || s.length() <= max) return s == null ? "" : s;
        return s.substring(0, max - 3) + "...";
    }

    @Override
    public String toString() {
        return String.format("MedicalRecord{ id='%s', patient='%s', diagnosis='%s', severity='%s' }",
                             recordID, patientID, diagnosis, severity);
    }
}
