import java.util.Scanner;

// ================================================================
//  FILE: DLLSimulation.java
//  Mô tả: Chương trình mô phỏng quản lý lịch sử bệnh án
//         bằng Doubly Linked List có menu tương tác.
//
//  Cấu trúc dữ liệu: Doubly Linked List
//  Cấu trúc node   : HEAD (cũ) ↔ ... ↔ TAIL (mới)
//
//  Cách chạy:
//    javac MedicalRecordNode.java MedicalHistoryDLL.java DLLSimulation.java
//    java  DLLSimulation
// ================================================================
public class DLLSimulation {

    private static int recordCounter = 1; // đếm ID tự động

    public static void main(String[] args) {
        Scanner            scanner = new Scanner(System.in);
        MedicalHistoryDLL  dll     = new MedicalHistoryDLL("BN-482931");

        banner("HỆ THỐNG QUẢN LÝ LỊCH SỬ BỆNH ÁN");
        System.out.println("  Cấu trúc  : Doubly Linked List");
        System.out.println("  Bệnh nhân : BN-482931");
        System.out.println("  Quy ước   : HEAD = cũ nhất  |  TAIL = mới nhất\n");

        // ── Nạp dữ liệu mẫu sẵn ────────────────────────────
        loadSampleData(dll);

        boolean isRunning = true;

        while (isRunning) {
            System.out.println("================= MENU =================");
            System.out.println("1.  Thêm bệnh án mới           (UC-01)");
            System.out.println("2.  Xóa bệnh án theo ID        (UC-02)");
            System.out.println("3.  Cập nhật bệnh án           (UC-03)");
            System.out.println("4.  Tìm kiếm bệnh án           (UC-04)");
            System.out.println("5.  Duyệt xuôi  (cũ → mới)    (UC-05)");
            System.out.println("6.  Duyệt ngược (mới → cũ)    (UC-06) ★ DLL");
            System.out.println("7.  Xem N bản ghi gần nhất     (UC-07) ★ DLL");
            System.out.println("8.  In danh sách hàng đợi");
            System.out.println("9.  In cấu trúc con trỏ DLL");
            System.out.println("10. Thống kê bệnh án");
            System.out.println("11. Kiểm tra tính nhất quán DLL");
            System.out.println("0.  Thoát chương trình");
            System.out.print("Chọn chức năng (0-11): ");

            String choiceStr = scanner.nextLine().trim();
            int choice;
            try {
                choice = Integer.parseInt(choiceStr);
            } catch (NumberFormatException e) {
                System.out.println("Lỗi: Vui lòng nhập số hợp lệ!\n");
                continue;
            }

            switch (choice) {

                // ── UC-01: Thêm bệnh án ─────────────────────
                case 1: {
                    System.out.println("\n[ THÊM BỆNH ÁN MỚI ]");
                    try {
                        // Tạo ID tự động
                        String autoID = String.format("REC-%03d", recordCounter);
                        System.out.printf("  ID tự động: %s%n", autoID);

                        System.out.print("  Ngày khám (VD: 20/05/2025): ");
                        String date = scanner.nextLine().trim();

                        System.out.print("  Chẩn đoán: ");
                        String diagnosis = scanner.nextLine().trim();

                        System.out.print("  Bác sĩ: ");
                        String doctor = scanner.nextLine().trim();

                        System.out.print("  Mức độ (Nhẹ / Trung bình / Nặng / Nguy kịch): ");
                        String severity = scanner.nextLine().trim();

                        System.out.print("  Chi phí (VNĐ, VD: 250000): ");
                        double cost = Double.parseDouble(scanner.nextLine().trim());

                        System.out.print("  Đơn thuốc (Enter để bỏ qua): ");
                        String prescription = scanner.nextLine().trim();
                        if (prescription.isEmpty()) prescription = null;

                        System.out.print("  Ghi chú (Enter để bỏ qua): ");
                        String notes = scanner.nextLine().trim();
                        if (notes.isEmpty()) notes = null;

                        MedicalRecordNode node = new MedicalRecordNode(
                            autoID, "BN-482931", date, diagnosis,
                            doctor, "Không rõ", prescription, notes,
                            severity, cost
                        );
                        dll.addRecord(node);
                        recordCounter++;

                    } catch (NumberFormatException e) {
                        System.out.println("Lỗi: Chi phí phải là số hợp lệ.");
                    } catch (Exception e) {
                        System.out.println("Thêm thất bại: " + e.getMessage());
                    }
                    break;
                }

                // ── UC-02: Xóa bệnh án ──────────────────────
                case 2: {
                    System.out.println("\n[ XÓA BỆNH ÁN ]");
                    System.out.print("  Nhập ID bệnh án cần xóa: ");
                    String delID = scanner.nextLine().trim();
                    dll.deleteRecord(delID);
                    break;
                }

                // ── UC-03: Cập nhật bệnh án ─────────────────
                case 3: {
                    System.out.println("\n[ CẬP NHẬT BỆNH ÁN ]");
                    System.out.print("  Nhập ID bệnh án cần cập nhật: ");
                    String upID = scanner.nextLine().trim();

                    System.out.print("  Chẩn đoán mới (Enter để giữ nguyên): ");
                    String upDiag = scanner.nextLine().trim();

                    System.out.print("  Đơn thuốc mới (Enter để giữ nguyên): ");
                    String upPresc = scanner.nextLine().trim();

                    System.out.print("  Ghi chú mới (Enter để giữ nguyên): ");
                    String upNotes = scanner.nextLine().trim();

                    System.out.print("  Mức độ mới (Enter để giữ nguyên): ");
                    String upSev = scanner.nextLine().trim();

                    dll.updateRecord(
                        upID,
                        upDiag.isEmpty()  ? null : upDiag,
                        upPresc.isEmpty() ? null : upPresc,
                        upNotes.isEmpty() ? null : upNotes,
                        upSev.isEmpty()   ? null : upSev
                    );
                    break;
                }

                // ── UC-04: Tìm kiếm ─────────────────────────
                case 4: {
                    System.out.println("\n[ TÌM KIẾM BỆNH ÁN ]");
                    System.out.print("  Nhập ID bệnh án cần tìm: ");
                    String findID = scanner.nextLine().trim();
                    dll.findRecord(findID);
                    break;
                }

                // ── UC-05: Duyệt xuôi ───────────────────────
                case 5: {
                    System.out.println("\n[ DUYỆT XUÔI: HEAD → TAIL (cũ → mới) ]");
                    dll.traverseForward();
                    break;
                }

                // ── UC-06: Duyệt ngược ──────────────────────
                case 6: {
                    System.out.println("\n[ DUYỆT NGƯỢC: TAIL → HEAD (mới → cũ) ]");
                    System.out.println("  ★ Bắt đầu từ TAIL ngay — lý do chính dùng DLL thay SLL");
                    dll.traverseBackward();
                    break;
                }

                // ── UC-07: Lấy N bản ghi gần nhất ──────────
                case 7: {
                    System.out.println("\n[ LẤY N BẢN GHI GẦN NHẤT ]");
                    System.out.println("  ★ O(k) — chỉ duyệt k bước từ TAIL, không duyệt toàn bộ");
                    System.out.print("  Nhập N (số bản ghi muốn xem): ");
                    try {
                        int k = Integer.parseInt(scanner.nextLine().trim());
                        dll.getLatestRecords(k);
                    } catch (NumberFormatException e) {
                        System.out.println("Lỗi: Nhập số hợp lệ.");
                    }
                    break;
                }

                // ── In danh sách ────────────────────────────
                case 8: {
                    dll.printDLL();
                    break;
                }

                // ── In cấu trúc ─────────────────────────────
                case 9: {
                    dll.printStructure();
                    break;
                }

                // ── Thống kê ────────────────────────────────
                case 10: {
                    dll.printStatistics();
                    break;
                }

                // ── Validate ────────────────────────────────
                case 11: {
                    String result = dll.validate();
                    System.out.println("\n  ── Kết quả validate DLL ──");
                    System.out.printf ("  HEAD   : %s%n", dll.getHeadID() != null ? dll.getHeadID() : "null");
                    System.out.printf ("  TAIL   : %s%n", dll.getTailID() != null ? dll.getTailID() : "null");
                    System.out.printf ("  Size   : %d%n", dll.getSize());
                    System.out.printf ("  Status : %s%n", result.equals("OK") ? "✓ OK — DLL nhất quán" : "✗ " + result);
                    System.out.println();
                    break;
                }

                // ── Thoát ───────────────────────────────────
                case 0: {
                    isRunning = false;
                    System.out.println("Đã thoát chương trình.");
                    break;
                }

                default:
                    System.out.println("Lựa chọn không hợp lệ, vui lòng thử lại.");
            }

            System.out.println(); // dòng trống cho dễ nhìn
        }

        scanner.close();
    }

    // ================================================================
    //  NẠP DỮ LIỆU MẪU (4 bản ghi theo thứ tự thời gian)
    // ================================================================
    private static void loadSampleData(MedicalHistoryDLL dll) {
        System.out.println("── Nạp dữ liệu mẫu ──");

        dll.addRecord(new MedicalRecordNode(
            "REC-001", "BN-482931",
            "10/01/2024", "Viêm họng cấp",
            "BS. Nguyễn Văn Minh",
            "Tai Mũi Họng",
            "Amoxicillin 500mg x 3 lần/ngày",
            "Uống nhiều nước, nghỉ ngơi",
            "Nhẹ", 150_000
        ));

        dll.addRecord(new MedicalRecordNode(
            "REC-002", "BN-482931",
            "15/03/2024", "Tăng huyết áp độ I",
            "BS. Trần Thị Lan",
            "Nội khoa",
            "Amlodipine 5mg x 1 lần/ngày",
            "Hạn chế muối, tái khám sau 2 tuần",
            "Trung bình", 280_000
        ));

        dll.addRecord(new MedicalRecordNode(
            "REC-003", "BN-482931",
            "20/06/2024", "Đau thắt ngực không ổn định",
            "BS. Lê Quang Hùng",
            "Tim mạch",
            "Aspirin 100mg + Nitrate",
            "Nhập viện khẩn cấp",
            "Nặng", 2_500_000
        ));

        dll.addRecord(new MedicalRecordNode(
            "REC-004", "BN-482931",
            "05/09/2024", "Kiểm tra tim mạch định kỳ",
            "BS. Phạm Thị Mai",
            "Tim mạch",
            "Duy trì thuốc hiện tại",
            "Tái khám sau 3 tháng",
            "Nhẹ", 350_000
        ));

        recordCounter = 5; // ID tiếp theo sẽ là REC-005
        System.out.println("── Đã nạp 4 bản ghi mẫu ──\n");
    }

    // ================================================================
    //  BANNER
    // ================================================================
    private static void banner(String title) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 55; i++) sb.append("=");
        String line = sb.toString();
        System.out.println("\n" + line);
        System.out.println("  " + title);
        System.out.println(line);
    }
}
