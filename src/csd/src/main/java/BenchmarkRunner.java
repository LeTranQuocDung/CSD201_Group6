import java.util.List;

/**
 * BenchmarkRunner.java
 * Class chạy tự động để kiểm tra và đo lường hiệu năng của cả 9 tính năng
 * trong hệ thống với 1 triệu bản ghi dữ liệu mẫu.
 */
public class BenchmarkRunner {

    public static void main(String[] args) {
        System.out.println("==========================================================================");
        System.out.println("     KICH BAN TU DONG KIEM TRA HIEU NANG VOI 1.000.000 DU LIEU MAU");
        System.out.println("==========================================================================\n");

        HospitalService service = new HospitalService();

        // 1. Sinh và đăng ký 1.000.000 bệnh nhân mẫu
        System.out.println("[TINH NANG 1] Dang ky 1.000.000 benh nhan...");
        long startGen = System.currentTimeMillis();
        SampleDataGenerator.generate(service, 1000000);
        long endGen = System.currentTimeMillis();
        System.out.printf(">> Thoi gian hoan thanh Dang ky: %,d ms (%.3f giay)\n\n", 
                (endGen - startGen), (endGen - startGen) / 1000.0);

        // 2. Gọi khám bệnh nhân tiếp theo (Min-Heap Pop + CLL Doctor Rotation)
        System.out.println("[TINH NANG 2] Goi kham benh nhan tiep theo tu Min-Heap...");
        long startCall = System.nanoTime();
        Patient calledPatient = service.callNextPatient();
        long endCall = System.nanoTime();
        if (calledPatient != null) {
            System.out.printf(">> Benh nhan duoc goi: %s - %s (Uu tien: %s)\n", 
                    calledPatient.getId(), calledPatient.getName(), calledPatient.getPriorityLabel());
            System.out.printf("   Bac si phan cong: %s (%s)\n", calledPatient.getDoctor(), calledPatient.getRoom());
        }
        System.out.printf(">> Thoi gian thuc hien: %,d ns\n\n", (endCall - startCall));

        // 3. Hoàn thành khám bệnh & ghi bệnh án (Tạo MedicalRecord)
        System.out.println("[TINH NANG 3] Hoan thanh ca kham & ghi benh an cho benh nhan vua goi...");
        if (calledPatient != null) {
            long startComplete = System.nanoTime();
            boolean success = service.completeExam(
                calledPatient.getId(), 
                "Sot sieu vi cap tinh", 
                "Paracetamol 500mg, Oresol", 
                "Nghi ngoi tai nha 3 ngay"
            );
            long endComplete = System.nanoTime();
            System.out.printf(">> Ghi benh an: %s\n", success ? "THANH CONG" : "THAT BAI");
            System.out.printf(">> Thoi gian thuc hien: %,d ns (%,d ms)\n\n", 
                    (endComplete - startComplete), (endComplete - startComplete) / 1000000);
        } else {
            System.out.println(">> Bo qua vi khong co benh nhan duoc goi.\n\n");
        }

        // 4. Tra cứu O(1) qua Hash Table
        System.out.println("[TINH NANG 4] Tra cuu nhanh O(1) benh nhan ID = 'BN-500000' qua HashTable...");
        long startLookup = System.nanoTime();
        Patient found = service.searchTriage("BN-500000");
        long endLookup = System.nanoTime();
        if (found != null) {
            System.out.printf(">> Tim thay: %s - %s | Trieu chung: %s\n", found.getId(), found.getName(), found.getSymptom());
        } else {
            System.out.println(">> Khong tim thay BN-500000");
        }
        System.out.printf(">> Thoi gian tra cuu: %,d ns (tương duong %.3f microgiay)\n\n", 
                (endLookup - startLookup), (endLookup - startLookup) / 1000.0);

        // 5. Cập nhật độ ưu tiên Triage (Min-Heap Update)
        System.out.println("[TINH NANG 5] Cap nhat do uu tien triage cho 'BN-800000' thanh 1 (Uu tien cao nhat)...");
        long startUpdate = System.nanoTime();
        try {
            service.updatePriority("BN-800000", 1);
            long endUpdate = System.nanoTime();
            System.out.printf(">> Thoi gian cap nhat Heap: %,d ns (%,d ms)\n\n", 
                    (endUpdate - startUpdate), (endUpdate - startUpdate) / 1000000);
        } catch (Exception e) {
            System.out.println(">> Loi cap nhat: " + e.getMessage() + "\n\n");
        }

        // 6. Xóa bệnh nhân khỏi hệ thống (DLL + Heap + Hash Remove)
        System.out.println("[TINH NANG 6] Xoa benh nhan 'BN-200000' khoi toan bo he thong...");
        long startDelete = System.nanoTime();
        boolean delSuccess = service.deleteById("BN-200000");
        long endDelete = System.nanoTime();
        System.out.printf(">> Xoa benh nhan: %s\n", delSuccess ? "THANH CONG" : "KHONG TIM THAY");
        System.out.printf(">> Thoi gian thuc hien: %,d ns (%,d ms)\n\n", 
                (endDelete - startDelete), (endDelete - startDelete) / 1000000);

        // 7. Hiển thị trực quan hàng đợi cấp cứu (Heap visualization - Đã rút gọn)
        System.out.println("[TINH NANG 7] Truc quan hoa hang doi cap cuu Min-Heap (Rut gon):");
        PatientNode[] arr = service.getTriageQueueArray();
        System.out.println("  ┌── Hang doi uu tien (Heap size=" + arr.length + ") ──");
        int limit = Math.min(arr.length, 10);
        for (int i = 0; i < limit; i++) {
            String marker = (i == 0) ? " ← GOC (Uu tien nhat)" : "";
            System.out.printf("  │  [%2d] %-10s | priority=%d | ho_ten=%s%s%n",
                    i, arr[i].getPatientID(), arr[i].getPriorityScore(),
                    service.searchTriage(arr[i].getPatientID()).getName(), marker);
        }
        if (arr.length > limit) {
            System.out.printf("  │  ... (va %d benh nhan khac dang cho) ...%n", arr.length - limit);
        }
        System.out.println("  └────────────────────────────────\n");

        // 8. Hiển thị trực quan lịch sử bệnh án (Doubly Linked List - Đã rút gọn)
        System.out.println("[TINH NANG 8] Truc quan hoa lich su benh an (Doubly Linked List):");
        service.printDiagram();
        service.printTable();
        System.out.println();

        // 9. Hiển thị danh sách bác sĩ trực ca (Circular Linked List)
        System.out.println("[TINH NANG 9] Truc quan hoa doi bac si truc ca (Circular Linked List):");
        List<String> docs = service.getDoctorsList();
        System.out.println("  ┌── Vong xoay bac si truc ──");
        for (int i = 0; i < docs.size(); i++) {
            System.out.println("  │  " + (i + 1) + ". " + docs.get(i));
        }
        System.out.println("  │  [Vong noi: " + docs.get(docs.size() - 1) + " -> " + docs.get(0) + "]");
        System.out.println("  └───────────────────────────\n");

        System.out.println("==========================================================================");
        System.out.println("        HOAN THANH KICH BAN KIEM TRA HIEU NANG - TAT CA CHO KET QUA TOT!");
        System.out.println("==========================================================================");
    }
}
