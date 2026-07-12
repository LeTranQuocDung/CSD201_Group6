import java.util.List;
import java.util.ArrayList;

/**
 * HospitalService.java
 * Tầng nghiệp vụ tích hợp cả 4 cấu trúc dữ liệu chính và các thực thể nghiệp vụ khác:
 * 1. DoublyLinkedList historyList (Lịch sử đăng ký bệnh án)
 * 2. TriageMinHeap triageQueue (Hàng đợi phân loại cấp cứu)
 * 3. HashTable patientCache (Bảng tra cứu nhanh bệnh nhân O(1))
 * 4. CircularLinkedList doctorRotation (Vòng xoay bác sĩ trực ca)
 * 5. Department & Doctor (Danh sách khoa ban và bác sĩ)
 * 6. MedicalRecord (Sinh bệnh án khi hoàn thành khám)
 */
public class HospitalService {

    private final DoublyLinkedList historyList;
    private final TriageMinHeap triageQueue;
    private final HashTable patientCache;
    private final CircularLinkedList doctorRotation;
    private final List<Department> departments;
    private int patientCounter;

    public HospitalService() {
        this.historyList = new DoublyLinkedList();
        this.triageQueue = new TriageMinHeap(100);
        this.patientCache = new HashTable(2048);
        this.doctorRotation = new CircularLinkedList();
        this.departments = new ArrayList<>();
        this.patientCounter = 0;

        // Khởi tạo các Khoa lâm sàng (Department)
        Department noi = new Department("DEP-NOI", "Khoa Noi tong quat", 1);
        Department nhi = new Department("DEP-NHI", "Khoa Nhi", 1);
        Department ngoai = new Department("DEP-NGOAI", "Khoa Ngoai chan thuong", 2);
        Department dalieu = new Department("DEP-DL", "Khoa Da lieu", 2);
        Department timmach = new Department("DEP-TM", "Khoa Tim mach", 3);

        departments.add(noi);
        departments.add(nhi);
        departments.add(ngoai);
        departments.add(dalieu);
        departments.add(timmach);

        // Khởi tạo các Bác sĩ điều trị (Doctor)
        Doctor doc1 = new Doctor("DOC-001", "BS. Nguyen Van Minh", "Phong 101", "DEP-NOI");
        Doctor doc2 = new Doctor("DOC-002", "BS. Tran Thi Hoa", "Phong 102", "DEP-NHI");
        Doctor doc3 = new Doctor("DOC-003", "BS. Le Hoang Nam", "Phong 103", "DEP-NGOAI");
        Doctor doc4 = new Doctor("DOC-004", "BS. Pham Thu Trang", "Phong 104", "DEP-DL");
        Doctor doc5 = new Doctor("DOC-005", "BS. Vo Minh Khoa", "Phong 105", "DEP-TM");

        noi.addDoctor(doc1);
        nhi.addDoctor(doc2);
        ngoai.addDoctor(doc3);
        dalieu.addDoctor(doc4);
        timmach.addDoctor(doc5);

        // Đưa bác sĩ vào hàng đợi xoay vòng ca trực (Circular Linked List)
        doctorRotation.addDoctor(doc1);
        doctorRotation.addDoctor(doc2);
        doctorRotation.addDoctor(doc3);
        doctorRotation.addDoctor(doc4);
        doctorRotation.addDoctor(doc5);
    }

    private String generateId() {
        String generatedId;
        do {
            patientCounter++;
            generatedId = String.format("BN-%04d", patientCounter);
        } while (patientCache.get(generatedId) != null);
        return generatedId;
    }

    /* ══════════════════════════════════════════════════════════
       1. ĐĂNG KÝ BỆNH NHÂN MỚI (Tích hợp DLL, HashTable, Min-Heap)
       ══════════════════════════════════════════════════════════ */
    public Patient register(String id, String name, int age, String gender,
                             String phone, String symptom, int priority) {
        if (id == null || id.trim().isEmpty() || id.equalsIgnoreCase("auto")) {
            id = generateId();
        } else {
            // Đồng bộ patientCounter nếu ID nhập thủ công có dạng BN-xxxx
            if (id.startsWith("BN-")) {
                try {
                    int num = Integer.parseInt(id.substring(3));
                    if (num > patientCounter) {
                        patientCounter = num;
                    }
                } catch (NumberFormatException ignored) {}
            }
            // Kiểm tra trùng lặp ID
            if (patientCache.get(id) != null) {
                throw new IllegalArgumentException("ID benh nhan '" + id + "' da ton tai trong he thong!");
            }
        }

        Patient p = new Patient(id, name, age, gender, phone, symptom, priority);

        historyList.push(p);
        patientCache.put(id, p);
        triageQueue.push(new PatientNode(p));

        return p;
    }

    /* ══════════════════════════════════════════════════════════
       2. GỌI KHÁM THEO THỨ TỰ ƯU TIÊN TRONG MIN-HEAP
       ══════════════════════════════════════════════════════════ */
    public Patient callNextPatient() {
        if (triageQueue.isEmpty()) {
            return null;
        }

        // Lấy bệnh nhân ưu tiên nhất ra khỏi Min-Heap
        PatientNode nextNode = triageQueue.pop();
        Patient p = nextNode.getData();

        // Xoay vòng lấy bác sĩ tiếp theo từ CLL
        Doctor doc = doctorRotation.nextDoctor();

        // Cập nhật trạng thái trong DLL (và trên đối tượng Patient gốc)
        historyList.updateStatus(p.getId(), Patient.STATUS_EXAMINING, doc.getDoctorName(), doc.getRoom());

        return p;
    }

    /* ══════════════════════════════════════════════════════════
       3. GỌI KHÁM THEO ID CHỈ ĐỊNH
       ══════════════════════════════════════════════════════════ */
    public Patient callPatient(String id) {
        Patient p = patientCache.get(id);
        if (p == null || !p.getStatus().equals(Patient.STATUS_WAITING)) {
            return null;
        }

        // Xóa khỏi hàng đợi Triage Min-Heap
        triageQueue.remove(id);

        // Phân công bác sĩ xoay vòng từ CLL
        Doctor doc = doctorRotation.nextDoctor();

        // Cập nhật trạng thái
        historyList.updateStatus(id, Patient.STATUS_EXAMINING, doc.getDoctorName(), doc.getRoom());

        return p;
    }

    /* ══════════════════════════════════════════════════════════
       4. HOÀN THÀNH KHÁM BỆNH & GHI BỆNH ÁN (Tạo MedicalRecord)
       ══════════════════════════════════════════════════════════ */
    public boolean completeExam(String id, String diagnosis, String prescription, String notes) {
        Patient p = patientCache.get(id);
        if (p == null || !p.getStatus().equals(Patient.STATUS_EXAMINING)) {
            return false;
        }

        // Cập nhật trạng thái trong DLL
        boolean updated = historyList.updateStatus(id, Patient.STATUS_DONE, null, null);
        if (updated) {
            // Sinh mã bệnh án tự động
            String recordId = "REC-" + id + "-" + (p.getMedicalHistory().size() + 1);
            String docName = p.getDoctor();
            
            // Tạo đối tượng MedicalRecord
            MedicalRecord record = new MedicalRecord(
                recordId, 
                id, 
                docName, 
                diagnosis, 
                prescription, 
                notes
            );
            
            // Lưu bệnh án vào hồ sơ bệnh nhân
            p.addMedicalRecord(record);
            return true;
        }
        return false;
    }

    // Nạp chồng phương thức hoàn thành khám nhanh với mô tả mặc định
    public boolean completeExam(String id) {
        return completeExam(id, "Kham lam sang cap cuu", "Theo doi them tai nha", "On dinh.");
    }

    /* ══════════════════════════════════════════════════════════
       5. TRA CỨU NHANH O(1) QUA HASH TABLE
       ══════════════════════════════════════════════════════════ */
    public Patient searchTriage(String id) {
        return patientCache.get(id);
    }

    /* ══════════════════════════════════════════════════════════
       6. CẬP NHẬT ĐỘ ƯU TIÊN TRONG HÀNG ĐỢI MIN-HEAP
       ══════════════════════════════════════════════════════════ */
    public void updatePriority(String id, int newPriority) {
        Patient p = patientCache.get(id);
        if (p == null) {
            throw new RuntimeException("Khong tim thay benh nhan de cap nhat: " + id);
        }
        
        p.setPriority(newPriority);
        
        if (p.getStatus().equals(Patient.STATUS_WAITING)) {
            triageQueue.updatePriority(id, newPriority);
        }
    }

    /* ══════════════════════════════════════════════════════════
       7. CÁC HÀM TIỆN ÍCH HIỂN THỊ & THỐNG KÊ
       ══════════════════════════════════════════════════════════ */
    public List<Patient> getHistory()          { return historyList.toListReverse(); }
    public List<Patient> getWaiting()          { return historyList.filterByStatus(Patient.STATUS_WAITING); }
    public List<Patient> getExamining()        { return historyList.filterByStatus(Patient.STATUS_EXAMINING); }
    public List<Patient> getByPriority(int p)  { return historyList.filterByPriority(p); }

    public int[] getStats() { return historyList.countByStatus(); }
    public int   getTotal() { return historyList.getSize(); }

    public boolean deleteById(String id) {
        triageQueue.remove(id);
        patientCache.remove(id);
        return historyList.deleteById(id);
    }

    public void clearAll() {
        historyList.clear();
        patientCounter = 0;
    }

    public PatientNode[] getTriageQueueArray() {
        return triageQueue.getHeapArray();
    }

    public List<String> getDoctorsList() {
        return doctorRotation.toList();
    }

    public List<Department> getDepartments() {
        return this.departments;
    }

    public Navigator navigatorFromHead() { return historyList.navigatorFromHead(); }
    public Navigator navigatorFromTail() { return historyList.navigatorFromTail(); }

    public void printDiagram() { historyList.printDiagram(); }
    public void printTable()   { historyList.printTable(); }
}
