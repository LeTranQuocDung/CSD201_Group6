public class DoublyLinkedList {
    
    // 1. Cấu trúc 1 Bệnh án
    private class MedicalRecordNode {
        String recordId;
        String date;
        String diagnosis;
        
        MedicalRecordNode next; // Dây tiến
        MedicalRecordNode prev; // Dây lùi
        
        MedicalRecordNode(String recordId, String date, String diagnosis) {
            this.recordId = recordId;
            this.date = date;
            this.diagnosis = diagnosis;
            this.next = null;
            this.prev = null;
        }
        
        public void printInfo() {
            System.out.printf("[Ngày: %s | Bệnh: %s] ", date, diagnosis);
        }
    }
    
    private MedicalRecordNode head; // Ca khám cũ nhất (Lần đầu đến viện)
    private MedicalRecordNode tail; // Ca khám mới nhất (Gần đây nhất)
    
    // Đổi tên Constructor theo tên Class
    public DoublyLinkedList() {
        head = null;
        tail = null;
    }
    
    // ========================================================
    // TÍNH NĂNG 1: THÊM BỆNH ÁN MỚI (Luôn thêm vào cuối)
    // ========================================================
    public void addRecord(String id, String date, String diag) {
        MedicalRecordNode newNode = new MedicalRecordNode(id, date, diag);
        if (tail == null) { // Nếu bệnh nhân mới tinh chưa từng khám
            head = newNode;
            tail = newNode;
        } else {
            newNode.prev = tail; // Móc quá khứ của hồ sơ mới vào hồ sơ cũ
            tail.next = newNode; // Móc tương lai của hồ sơ cũ vào hồ sơ mới
            tail = newNode;      // Dời chốt Tail sang hồ sơ mới
        }
    }
    
    // ========================================================
    // TÍNH NĂNG 2: XEM LỊCH SỬ TỪ CŨ -> MỚI (Duyệt Forward)
    // ========================================================
    public void viewHistoryChronological() {
        if (head == null) {
            System.out.println("Bệnh nhân chưa có lịch sử khám.");
            return;
        }
        MedicalRecordNode current = head;
        System.out.print("Lịch sử (Cũ -> Mới): ");
        while (current != null) {
            current.printInfo();
            if (current.next != null) System.out.print(" ➡ ");
            current = current.next;
        }
        System.out.println();
    }
    
    // ========================================================
    // TÍNH NĂNG 3: XEM LỊCH SỬ TỪ MỚI -> CŨ (Duyệt Backward)
    // ========================================================
    public void viewHistoryRecentFirst() {
        if (tail == null) {
            System.out.println("Bệnh nhân chưa có lịch sử khám.");
            return;
        }
        MedicalRecordNode current = tail;
        System.out.print("Lịch sử (Mới -> Cũ): ");
        while (current != null) {
            current.printInfo();
            if (current.prev != null) System.out.print(" ⬅ ");
            current = current.prev;
        }
        System.out.println();
    }
}
