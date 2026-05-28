import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.Scanner;

// ── 1. CLASS MEDICALRECORD (Data) ──────────────────────────
class MedicalRecord {
    String recordId;       // ID duy nhất của bản ghi
    String patientId;      // ID bệnh nhân
    String date;           // Ngày khám
    String diagnosis;      // Chẩn đoán
    String doctor;         // Bác sĩ phụ trách
    String department;     // Khoa khám
    String symptoms;       // Triệu chứng
    String labResults;     // Kết quả xét nghiệm
    String prescription;   // Đơn thuốc
    String notes;          // Ghi chú
    String severity;       // Nhẹ | Trung bình | Nặng | Nguy kịch
    double visitCost;      // Chi phí khám (VNĐ)
    String createdAt;      // Timestamp tạo bản ghi

    // Constructor đầy đủ
    public MedicalRecord(String patientId, String diagnosis, String doctor,
                         String department, String symptoms, String labResults,
                         String prescription, String notes,
                         String severity, double visitCost) {
        this.recordId    = "REC-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        this.patientId   = patientId;
        this.date        = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        this.createdAt   = this.date;
        this.diagnosis   = diagnosis;
        this.doctor      = doctor;
        this.department  = department;
        this.symptoms    = symptoms;
        this.labResults  = (labResults == null || labResults.isEmpty()) ? "Chưa có" : labResults;
        this.prescription = (prescription == null || prescription.isEmpty()) ? "Chưa có" : prescription;
        this.notes       = (notes == null || notes.isEmpty()) ? "Không có" : notes;
        this.severity    = severity;
        this.visitCost   = visitCost;
    }

    // Constructor rút gọn (dùng khi demo)
    public MedicalRecord(String patientId, String diagnosis, String doctor,
                         String department, String severity, double visitCost) {
        this(patientId, diagnosis, doctor, department,
             "Không ghi rõ", "Chưa có", "Chưa có", "Không có", severity, visitCost);
    }

    // Hiển thị thông tin bản ghi
    public void display() {
        System.out.println("  ┌─────────────────────────────────────────────┐");
        System.out.printf ("  │ 🆔  ID Bản ghi : %-28s│%n", recordId);
        System.out.printf ("  │ 📅  Ngày khám  : %-28s│%n", date);
        System.out.printf ("  │ 🩺  Chẩn đoán  : %-28s│%n", truncate(diagnosis, 28));
        System.out.printf ("  │ 👨‍⚕️  Bác sĩ     : %-28s│%n", truncate(doctor, 28));
        System.out.printf ("  │ 🏥  Khoa       : %-28s│%n", department);
        System.out.printf ("  │ ⚠️   Mức độ     : %-28s│%n", severity);
        System.out.printf ("  │ 🔬  XN kết quả : %-28s│%n", truncate(labResults, 28));
        System.out.printf ("  │ 💊  Đơn thuốc  : %-28s│%n", truncate(prescription, 28));
        System.out.printf ("  │ 📝  Ghi chú    : %-28s│%n", truncate(notes, 28));
        System.out.printf ("  │ 💰  Chi phí    : %,-28.0f│%n", visitCost);
        System.out.println("  └─────────────────────────────────────────────┘");
    }

    // Hiển thị dạng ngắn (dùng trong danh sách)
    public void displayShort() {
        System.out.printf("  [%s] %s — %s — %s (%.0f VNĐ)%n",
            recordId, date, truncate(diagnosis, 20), severity, visitCost);
    }

    private String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max - 3) + "...";
    }
}

// ── 2. CLASS NODE (Structure) ───────────────────────────────
// Tách riêng DATA (MedicalRecord) khỏi STRUCTURE (Node)
// → Single Responsibility Principle
class DLLNode {
    MedicalRecord data; // payload — bệnh án
    DLLNode prev;       // con trỏ về bản ghi CŨ HƠN (về phía HEAD)
    DLLNode next;       // con trỏ về bản ghi MỚI HƠN (về phía TAIL)

    public DLLNode(MedicalRecord record) {
        this.data = record;
        this.prev = null;
        this.next = null;
    }
}

// ── 3. CLASS DOUBLYLINKEDLIST ───────────────────────────────
class MedicalHistoryDLL {

    private DLLNode head; // bản ghi CŨ NHẤT (thêm vào đầu tiên)
    private DLLNode tail; // bản ghi MỚI NHẤT (luôn thêm vào đây)
    private int     size; // số bản ghi hiện có
    private String  patientId;

    public MedicalHistoryDLL(String patientId) {
        this.patientId = patientId;
        this.head      = null;
        this.tail      = null;
        this.size      = 0;
    }

    // ── A. THÊM bản ghi MỚI vào cuối (TAIL) — O(1) ─────────
    // Lý do thêm vào TAIL: bản ghi mới nhất luôn ở cuối,
    // duyệt ngược từ TAIL = xem lịch sử mới nhất trước
    public MedicalRecord addRecord(MedicalRecord record) {
        DLLNode newNode = new DLLNode(record);

        if (tail == null) {
            // DLL rỗng → node đầu tiên là cả HEAD lẫn TAIL
            head = newNode;
            tail = newNode;
            // newNode.prev = null, newNode.next = null (mặc định)
        } else {
            // ─ Bước 1: node mới nhìn về phía CŨ (tail hiện tại)
            newNode.prev = tail;
            // ─ Bước 2: tail cũ nhìn về phía MỚI (node mới)
            tail.next    = newNode;
            // ─ Bước 3: cập nhật tail → node mới
            tail         = newNode;
        }

        size++;
        System.out.println("  ✅ Đã thêm bản ghi: " + record.recordId
                         + " | Tổng: " + size + " bản ghi");
        return record;
    }

    // ── B. XÓA bản ghi theo recordId — O(n) tìm + O(1) xóa ─
    public MedicalRecord deleteRecord(String recordId) {
        // EDGE CASE 1: DLL rỗng
        if (head == null) {
            System.out.println("  ❌ DLL rỗng, không có gì để xóa.");
            return null;
        }

        // Tìm node cần xóa
        DLLNode current = head;
        while (current != null && !current.data.recordId.equals(recordId)) {
            current = current.next;
        }

        // EDGE CASE 2: không tìm thấy
        if (current == null) {
            System.out.println("  ❌ Không tìm thấy bản ghi: " + recordId);
            return null;
        }

        MedicalRecord deleted = current.data;

        // EDGE CASE 3: DLL chỉ có 1 nút (head == tail)
        if (size == 1) {
            head = null;
            tail = null;
            // Không cần xử lý prev/next (chỉ có 1 node)

        } else if (current == head) {
            // EDGE CASE 4: xóa HEAD
            head       = head.next;  // head mới = node kế tiếp
            head.prev  = null;        // head mới không có node trước

        } else if (current == tail) {
            // EDGE CASE 5: xóa TAIL
            tail       = tail.prev;  // tail mới = node trước đó
            tail.next  = null;        // tail mới không có node sau

        } else {
            // TRƯỜNG HỢP THÔNG THƯỜNG: xóa node GIỮA
            // Nối trực tiếp prev ↔ next, bỏ qua current
            current.prev.next = current.next; // prev nhìn qua current → next
            current.next.prev = current.prev; // next nhìn qua current → prev
        }

        // Ngắt liên kết của node đã xóa (tránh memory leak)
        current.prev = null;
        current.next = null;

        size--;
        System.out.println("  🗑️  Đã xóa bản ghi: " + recordId
                         + " | Còn lại: " + size + " bản ghi");
        return deleted;
    }

    // ── C. DUYỆT XUÔI: HEAD → TAIL (cũ nhất → mới nhất) ───
    public void traverseForward() {
        if (head == null) {
            System.out.println("  📭 Không có bản ghi nào.");
            return;
        }

        System.out.println("\n  ════ LỊCH SỬ (CŨ → MỚI) | Tổng: " + size + " bản ghi ════");
        DLLNode current = head; // bắt đầu từ bản ghi CŨ NHẤT
        int no = 1;

        while (current != null) {
            System.out.printf("\n  [%d/%d] %s %s%n",
                no, size,
                current == head ? "◀ HEAD" : "",
                current == tail ? "▶ TAIL" : "");
            current.data.displayShort();
            current = current.next; // tiến về phía TAIL
            no++;
        }
        System.out.println();
    }

    // ── D. DUYỆT NGƯỢC: TAIL → HEAD (mới nhất → cũ nhất) ──
    // ĐÂY LÀ LÝ DO CHÍNH dùng DLL thay vì SLL:
    // SLL không có con trỏ prev → phải đi từ head đến tail trước (O(n) thừa)
    // DLL có tail pointer → bắt đầu từ tail NGAY, dùng current.prev
    public void traverseBackward() {
        if (tail == null) {
            System.out.println("  📭 Không có bản ghi nào.");
            return;
        }

        System.out.println("\n  ════ LỊCH SỬ (MỚI → CŨ) | Tổng: " + size + " bản ghi ════");
        DLLNode current = tail; // BẮT ĐẦU TỪ TAIL NGAY — không cần duyệt forward trước
        int no = 1;

        while (current != null) {
            System.out.printf("\n  [%d/%d] %s %s%n",
                no, size,
                current == tail ? "▶ TAIL (mới nhất)" : "",
                current == head ? "◀ HEAD (cũ nhất)"  : "");
            current.data.displayShort();
            current = current.prev; // dùng con trỏ PREV — đặc tính của DLL
            no++;
        }
        System.out.println();
    }

    // ── E. XEM CHI TIẾT bản ghi theo recordId — O(n) ───────
    public MedicalRecord findRecord(String recordId) {
        DLLNode current = head;
        while (current != null) {
            if (current.data.recordId.equals(recordId)) {
                System.out.println("\n  🔍 Tìm thấy bản ghi:");
                System.out.println("  prev → " + (current.prev != null
                    ? current.prev.data.recordId : "null (đây là HEAD)"));
                current.data.display();
                System.out.println("  next → " + (current.next != null
                    ? current.next.data.recordId : "null (đây là TAIL)"));
                return current.data;
            }
            current = current.next;
        }
        System.out.println("  ❌ Không tìm thấy: " + recordId);
        return null;
    }

    // ── F. LẤY N bản ghi GẦN NHẤT — O(n) với n nhỏ ────────
    // Bắt đầu từ TAIL → dùng prev
    public void getLatestRecords(int n) {
        if (tail == null) {
            System.out.println("  📭 Không có bản ghi nào.");
            return;
        }

        System.out.println("\n  ════ " + n + " BẢN GHI GẦN NHẤT ════");
        DLLNode current = tail; // bắt đầu từ bản ghi mới nhất
        int count = 0;

        while (current != null && count < n) {
            System.out.printf("  [%d] ", count + 1);
            current.data.displayShort();
            current = current.prev;
            count++;
        }
        System.out.println();
    }

    // ── G. CẬP NHẬT bản ghi — O(n) ─────────────────────────
    public boolean updateRecord(String recordId, String newDiagnosis,
                                String newPrescription, String newNotes) {
        DLLNode current = head;
        while (current != null) {
            if (current.data.recordId.equals(recordId)) {
                if (newDiagnosis   != null) current.data.diagnosis   = newDiagnosis;
                if (newPrescription != null) current.data.prescription = newPrescription;
                if (newNotes       != null) current.data.notes       = newNotes;
                System.out.println("  ✏️  Đã cập nhật bản ghi: " + recordId);
                return true;
            }
            current = current.next;
        }
        System.out.println("  ❌ Không tìm thấy bản ghi: " + recordId);
        return false;
    }

    // ── H. THỐNG KÊ tổng hợp ────────────────────────────────
    public void printSummary() {
        System.out.println("\n  ══════════════ THỐNG KÊ BỆNH ÁN ══════════════");
        System.out.println("  Bệnh nhân ID : " + patientId);
        System.out.println("  Tổng lần khám: " + size);
        System.out.println("  Bản ghi đầu  : " + (head != null ? head.data.date : "Không có"));
        System.out.println("  Bản ghi cuối : " + (tail != null ? tail.data.date : "Không có"));

        if (size == 0) return;

        // Đếm theo mức độ & tính tổng chi phí
        int nhe = 0, tb = 0, nang = 0, nguyKich = 0;
        double totalCost = 0;
        DLLNode current = head;

        while (current != null) {
            switch (current.data.severity) {
                case "Nhẹ":        nhe++;       break;
                case "Trung bình": tb++;         break;
                case "Nặng":       nang++;       break;
                case "Nguy kịch":  nguyKich++;   break;
            }
            totalCost += current.data.visitCost;
            current = current.next;
        }

        System.out.println("  ─────────────────────────────────────────────");
        System.out.printf ("  Mức độ Nhẹ       : %d lần%n",      nhe);
        System.out.printf ("  Mức độ Trung bình: %d lần%n",      tb);
        System.out.printf ("  Mức độ Nặng      : %d lần%n",      nang);
        System.out.printf ("  Mức độ Nguy kịch : %d lần%n",      nguyKich);
        System.out.printf ("  Tổng chi phí     : %,.0f VNĐ%n",   totalCost);
        System.out.printf ("  Chi phí trung bình: %,.0f VNĐ%n",  totalCost / size);
        System.out.println("  ══════════════════════════════════════════════\n");
    }

    // ── I. HIỂN THỊ cấu trúc DLL trực quan ──────────────────
    public void displayStructure() {
        System.out.println("\n  ══ CẤU TRÚC DOUBLY LINKED LIST ══");
        System.out.println("  null");
        System.out.println("   ↑ prev");

        DLLNode current = head;
        while (current != null) {
            System.out.println("  ┌──────────────────┐");
            System.out.printf ("  │ prev: %-11s│%n",
                current.prev == null ? "null" : current.prev.data.recordId);
            System.out.printf ("  │ ID  : %-11s│  %s%n",
                current.data.recordId,
                current == head ? "◀ HEAD" : (current == tail ? "▶ TAIL" : ""));
            System.out.printf ("  │ data: %-11s│%n",
                truncate(current.data.diagnosis, 11));
            System.out.printf ("  │ next: %-11s│%n",
                current.next == null ? "null" : current.next.data.recordId);
            System.out.println("  └──────────────────┘");

            if (current.next != null) {
                System.out.println("       ↕ prev/next");
            }
            current = current.next;
        }
        System.out.println("   ↓ next");
        System.out.println("  null\n");
    }

    // Getters
    public int    getSize()      { return size; }
    public boolean isEmpty()     { return size == 0; }
    public String getPatientId() { return patientId; }

    // Lấy recordId của HEAD (dùng cho demo navigation)
    public String getHeadId() { return head != null ? head.data.recordId : null; }

    // Lấy recordId của TAIL
    public String getTailId() { return tail != null ? tail.data.recordId : null; }

    private String truncate(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max - 2) + "..";
    }
}

// ── 4. MAIN — Demo tất cả chức năng ────────────────────────
public class DoublyLinkedListDemo {

    static final String SEP = "\n" + "═".repeat(55) + "\n";
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println(SEP);
        System.out.println("  DEMO — DOUBLY LINKED LIST: Lịch sử bệnh án");
        System.out.println("  CSD201 | Smart Hospital Patient Triage System");
        System.out.println(SEP);

        // ── Tạo DLL cho bệnh nhân BN-482931 ─────────────────
        MedicalHistoryDLL history = new MedicalHistoryDLL("BN-482931");

        // ── THÊM bản ghi ────────────────────────────────────
        System.out.println("【1】THÊM BẢN GHI VÀO DLL (addRecord)\n");

        MedicalRecord r1 = new MedicalRecord(
            "BN-482931",
            "Viêm họng cấp",
            "BS. Nguyễn Văn Minh",
            "Tai Mũi Họng",
            "Ho, đau họng, sốt nhẹ 37.5°C",
            "Bạch cầu bình thường",
            "Amoxicillin 500mg x 3 lần/ngày",
            "Uống nhiều nước, nghỉ ngơi",
            "Nhẹ",
            150_000
        );
        history.addRecord(r1);

        MedicalRecord r2 = new MedicalRecord(
            "BN-482931",
            "Tăng huyết áp độ I",
            "BS. Trần Thị Lan",
            "Nội khoa",
            "Đau đầu, chóng mặt, huyết áp 150/95",
            "Huyết áp 150/95 mmHg",
            "Amlodipine 5mg x 1 lần/ngày",
            "Hạn chế muối, tái khám sau 2 tuần",
            "Trung bình",
            280_000
        );
        history.addRecord(r2);

        MedicalRecord r3 = new MedicalRecord(
            "BN-482931",
            "Đau thắt ngực không ổn định",
            "BS. Lê Quang Hùng",
            "Tim mạch",
            "Đau thắt ngực trái, khó thở khi gắng sức",
            "ECG: ST-elevation V1-V4, Troponin tăng",
            "Aspirin 100mg + Atorvastatin 20mg",
            "KHẨN CẤP — nhập viện ngay",
            "Nặng",
            2_500_000
        );
        history.addRecord(r3);

        MedicalRecord r4 = new MedicalRecord(
            "BN-482931",
            "Theo dõi sau can thiệp tim mạch",
            "BS. Lê Quang Hùng",
            "Tim mạch",
            "Bệnh nhân ổn định sau đặt stent",
            "ECG bình thường, Troponin về ngưỡng",
            "Clopidogrel 75mg + Aspirin 100mg",
            "Tái khám sau 1 tháng, hạn chế vận động mạnh",
            "Trung bình",
            500_000
        );
        history.addRecord(r4);

        MedicalRecord r5 = new MedicalRecord(
            "BN-482931",
            "Kiểm tra định kỳ tim mạch",
            "BS. Phạm Thị Mai",
            "Tim mạch",
            "Khỏe mạnh, không có triệu chứng",
            "Siêu âm tim: EF 60%, bình thường",
            "Duy trì thuốc hiện tại",
            "Tiếp tục tái khám mỗi 3 tháng",
            "Nhẹ",
            350_000
        );
        history.addRecord(r5);

        // ── HIỂN THỊ cấu trúc DLL ───────────────────────────
        pause();
        System.out.println(SEP + "【2】CẤU TRÚC DLL SAU KHI THÊM 5 BẢN GHI\n");
        history.displayStructure();

        // ── DUYỆT XUÔI ──────────────────────────────────────
        pause();
        System.out.println(SEP + "【3】DUYỆT XUÔI: HEAD → TAIL (cũ → mới)\n");
        System.out.println("  💡 Bắt đầu từ current = head, dùng current.next\n");
        history.traverseForward();

        // ── DUYỆT NGƯỢC ─────────────────────────────────────
        pause();
        System.out.println(SEP + "【4】DUYỆT NGƯỢC: TAIL → HEAD (mới → cũ)\n");
        System.out.println("  💡 Bắt đầu từ current = TAIL ngay (không cần pass qua HEAD)");
        System.out.println("  💡 Dùng current.prev — đặc tính của DLL vs SLL\n");
        history.traverseBackward();

        // ── LẤY N BẢN GHI GẦN NHẤT ─────────────────────────
        pause();
        System.out.println(SEP + "【5】3 BẢN GHI GẦN NHẤT (getLatestRecords)\n");
        System.out.println("  💡 Bắt đầu từ TAIL, dùng current.prev — O(k) với k=3\n");
        history.getLatestRecords(3);

        // ── TÌM KIẾM bản ghi ────────────────────────────────
        pause();
        System.out.println(SEP + "【6】TÌM KIẾM BẢN GHI (findRecord)\n");
        System.out.println("  💡 Tìm bản ghi đầu tiên (HEAD) để minh họa con trỏ:\n");
        history.findRecord(history.getHeadId());

        System.out.println("  💡 Tìm bản ghi cuối cùng (TAIL) để minh họa con trỏ:\n");
        history.findRecord(history.getTailId());

        // ── XÓA bản ghi (kiểm tra tất cả edge cases) ────────
        pause();
        System.out.println(SEP + "【7】XÓA BẢN GHI — KIỂM TRA EDGE CASES\n");

        System.out.println("  ▷ Xóa node GIỮA (r2):");
        history.deleteRecord(r2.recordId);
        history.displayStructure();

        System.out.println("  ▷ Xóa node HEAD (r1):");
        history.deleteRecord(r1.recordId);
        history.displayStructure();

        System.out.println("  ▷ Xóa node TAIL (r5):");
        history.deleteRecord(r5.recordId);
        history.displayStructure();

        System.out.println("  ▷ Xóa ID không tồn tại:");
        history.deleteRecord("REC-FAKE01");

        System.out.println("\n  ▷ Xóa đến khi còn 1 node rồi xóa tiếp (edge case: DLL 1 nút):");
        history.deleteRecord(r3.recordId);
        // Chỉ còn r4
        System.out.println("  Còn lại: " + history.getSize() + " bản ghi");
        System.out.println("  ▷ Xóa nút duy nhất (r4) — HEAD == TAIL:");
        history.deleteRecord(r4.recordId);
        System.out.println("  Sau xóa: size=" + history.getSize()
                         + " | HEAD=" + history.getHeadId()
                         + " | TAIL=" + history.getTailId());

        System.out.println("\n  ▷ Xóa tiếp khi DLL đã rỗng:");
        history.deleteRecord("REC-ANY");

        // ── THÊM lại + CẬP NHẬT ────────────────────────────
        pause();
        System.out.println(SEP + "【8】THÊM LẠI & CẬP NHẬT BẢN GHI\n");
        history.addRecord(new MedicalRecord("BN-482931", "Cảm cúm thông thường",
            "BS. Hoàng Đức Thành", "Nội khoa", "Nhẹ", 120_000));
        MedicalRecord rNew = new MedicalRecord("BN-482931", "Viêm dạ dày cấp",
            "BS. Trần Thị Lan", "Nội khoa", "Trung bình", 250_000);
        history.addRecord(rNew);

        System.out.println("\n  ▷ Cập nhật bản ghi mới nhất:");
        history.updateRecord(rNew.recordId,
            "Viêm loét dạ dày (H. pylori dương tính)",
            "Omeprazole 20mg + Amoxicillin 1g + Clarithromycin 500mg",
            "Tái khám sau 4 tuần, xét nghiệm lại H. pylori");

        // ── THỐNG KÊ ────────────────────────────────────────
        pause();
        System.out.println(SEP + "【9】THỐNG KÊ LỊCH SỬ BỆNH ÁN\n");

        // Thêm thêm vài bản ghi để thống kê có ý nghĩa
        history.addRecord(new MedicalRecord("BN-482931", "Tăng huyết áp kiểm soát kém",
            "BS. Nguyễn Văn Minh", "Nội khoa", "Nặng", 400_000));
        history.addRecord(new MedicalRecord("BN-482931", "Kiểm tra định kỳ",
            "BS. Phạm Thị Mai", "Nội khoa", "Nhẹ", 100_000));

        history.printSummary();
        history.traverseForward();

        // ── SO SÁNH với SLL ─────────────────────────────────
        System.out.println(SEP);
        System.out.println("  📊 SO SÁNH DLL vs SLL vs ARRAY\n");
        System.out.println("  Thao tác           │ DLL     │ SLL     │ Array");
        System.out.println("  ───────────────────┼─────────┼─────────┼──────────");
        System.out.println("  addRecord (tail)   │  O(1)   │  O(1)*  │  O(1) amortized");
        System.out.println("  deleteRecord       │  O(1)** │  O(n)   │  O(n) shift");
        System.out.println("  traverseForward    │  O(n)   │  O(n)   │  O(n)");
        System.out.println("  traverseBackward   │  O(n)   │  O(n)***│  O(n)");
        System.out.println("  getLatestRecords(k)│  O(k)   │  O(n)   │  O(k)");
        System.out.println("  findRecord         │  O(n)   │  O(n)   │  O(n)");
        System.out.println();
        System.out.println("  *  SLL cần giữ tail pointer");
        System.out.println("  ** O(1) xóa nếu đã có pointer; O(n) để tìm node");
        System.out.println("  *** SLL phải đi từ HEAD → TAIL trước (O(n) thừa),");
        System.out.println("      DLL bắt đầu từ TAIL ngay lập tức\n");

        System.out.println("  ✅ KẾT LUẬN: DLL là lựa chọn TỐI ƯU cho lịch sử bệnh án vì:");
        System.out.println("    1. Bác sĩ thường xem bệnh án MỚI NHẤT trước → traverseBackward O(1) khởi đầu");
        System.out.println("    2. Xóa bản ghi cụ thể nhanh hơn Array (không cần shift)");
        System.out.println("    3. Dynamic size — không giới hạn số bản ghi như Array");
        System.out.println(SEP);

        System.out.println("  👋 Demo hoàn tất!");
        sc.close();
    }

    // Tạm dừng giữa các phần demo
    static void pause() {
        try { Thread.sleep(300); } catch (InterruptedException e) { /* ignore */ }
    }
}
