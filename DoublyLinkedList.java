import java.util.Scanner;

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
            System.out.printf("[Mã BA: %s | Ngày: %s | Bệnh: %s] ", recordId, date, diagnosis);
        }
    }
    
    private MedicalRecordNode head; // Ca khám cũ nhất
    private MedicalRecordNode tail; // Ca khám mới nhất
    
    public DoublyLinkedList() {
        head = null;
        tail = null;
    }
    
    // --- TÍNH NĂNG 1: THÊM BỆNH ÁN ---
    public void addRecord(String id, String date, String diag) {
        MedicalRecordNode newNode = new MedicalRecordNode(id, date, diag);
        if (tail == null) { 
            head = newNode;
            tail = newNode;
            System.out.println("[LOG] Đã tạo hồ sơ gốc (Ca khám đầu tiên).");
        } else {
            newNode.prev = tail; 
            tail.next = newNode; 
            tail = newNode;      
            System.out.println("[LOG] Đã thêm ca khám mới vào cuối lịch sử.");
        }
    }
    
    // --- TÍNH NĂNG 2: DUYỆT TIẾN (FORWARD) ---
    public void viewHistoryChronological() {
        if (head == null) {
            System.out.println("❌ Bệnh nhân chưa có lịch sử khám.");
            return;
        }
        MedicalRecordNode current = head;
        System.out.print("[LOG] Lịch sử (Cũ -> Mới):\n");
        while (current != null) {
            current.printInfo();
            if (current.next != null) System.out.print("\n ⬇ \n");
            current = current.next;
        }
        System.out.println("\n(Kết thúc)");
    }
    
    // --- TÍNH NĂNG 3: DUYỆT LÙI (BACKWARD) ---
    public void viewHistoryRecentFirst() {
        if (tail == null) {
            System.out.println("❌ Bệnh nhân chưa có lịch sử khám.");
            return;
        }
        MedicalRecordNode current = tail;
        System.out.print("[LOG] Lịch sử (Mới -> Cũ):\n");
        while (current != null) {
            current.printInfo();
            if (current.prev != null) System.out.print("\n ⬇ \n");
            current = current.prev;
        }
        System.out.println("\n(Kết thúc)");
    }

    // ================================================================
    // MAIN METHOD: Interactive Console Menu
    // ================================================================
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        DoublyLinkedList dll = new DoublyLinkedList();
        boolean isRunning = true;

        System.out.println("=== HỆ THỐNG LỊCH SỬ BỆNH ÁN (DOUBLY LINKED LIST) ===");
        System.out.println("[LOG] Hệ thống đã sẵn sàng. Danh sách đang trống.");
        
        while (isRunning) {
            System.out.println("\n================= MENU =================");
            System.out.println("1. Thêm bệnh án mới (Insert Last)");
            System.out.println("2. Xem lịch sử: Cũ -> Mới (Forward)");
            System.out.println("3. Xem lịch sử: Mới -> Cũ (Backward)");
            System.out.println("0. Thoát chương trình");
            System.out.print("Chọn chức năng (0-3): ");

            String choiceStr = scanner.nextLine();
            int choice = -1;

            try {
                choice = Integer.parseInt(choiceStr);
            } catch (NumberFormatException e) {
                System.out.println("❌ Lỗi: Vui lòng nhập số hợp lệ!");
                continue;
            }

            switch (choice) {
                case 1:
                    System.out.print("Nhập Mã BA: ");
                    String id = scanner.nextLine();
                    if (id.trim().isEmpty()) {
                        System.out.println("❌ Lỗi: Mã BA không được để trống!");
                        break;
                    }
                    System.out.print("Nhập Ngày khám (dd/mm/yyyy): ");
                    String date = scanner.nextLine();
                    System.out.print("Nhập Chẩn đoán: ");
                    String diag = scanner.nextLine();
                    
                    dll.addRecord(id, date, diag);
                    break;

                case 2:
                    dll.viewHistoryChronological();
                    break;

                case 3:
                    dll.viewHistoryRecentFirst();
                    break;

                case 0:
                    isRunning = false;
                    System.out.println("Tắt hệ thống. Tạm biệt!");
                    break;

                default:
                    System.out.println("❌ Lựa chọn không hợp lệ. Vui lòng chọn từ 0 đến 3.");
            }
        }
        scanner.close();
    }
}
