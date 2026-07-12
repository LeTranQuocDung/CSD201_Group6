import java.util.Scanner;
import java.util.List;

/**
 * Main.java
 * Giao diện Console CLI cho hệ thống phân loại bệnh nhân thông minh.
 * Tích hợp toàn bộ cấu trúc dữ liệu và thực thể: Min-Heap, Hash Table, DLL,
 * CLL, Doctor, Department, MedicalRecord.
 */
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        HospitalService service = new HospitalService();

        // Đăng ký trước một số bệnh nhân mặc định để test dễ dàng
        service.register("BN-0001", "Tran Thu Thuy", 25, "Nu", "0981234567", "Dau dau", 3);
        service.register("BN-0002", "Nguyen Van Binh", 40, "Nam", "0971234568", "Kho tho, dau nguc", 1);
        service.register("BN-0003", "Le Hoang Yen", 62, "Nu", "0961234569", "Sot cao co giat", 2);
        service.register("BN-0004", "Pham Minh Duc", 18, "Nam", "0951234570", "Zon chan thuong nhe", 4);

        System.out.println("[SYSTEM] Tu dong sinh va dang ky 1.000.000 benh nhan de chay kiem thu...");
        SampleDataGenerator.generate(service, 1000000);
        System.out.println("[SYSTEM] Khoi tao hoan tat! Chuan bi vao Menu.\n");

        System.out.println("==========================================================================");
        System.out.println("  HE THONG PHAN LOAI & DIEU PHOI BENH NHAN CAP CUU THONG MINH (INTEGRATED)");
        System.out.println("  Tuong thich 100% So do lop: Min-Heap, Hash Table, Doubly & Circular Linked List");
        System.out.println("==========================================================================\n");

        boolean isRunning = true;
        while (isRunning) {
            System.out.println("--------------------------------- MENU ---------------------------------");
            System.out.println("1. Dang ky benh nhan moi (DLL + Heap + Hash)");
            System.out.println("2. Goi kham benh nhan tiep theo (Min-Heap Pop + CLL Doctor Rotation)");
            System.out.println("3. Hoan thanh ca kham & Ghi benh an (Tao MedicalRecord)");
            System.out.println("4. Tra cuu nhanh ho so O(1) & Lich su benh an (Hash Table Get)");
            System.out.println("5. Cap nhat do uu tien Triage (Min-Heap Update)");
            System.out.println("6. Xoa benh nhan khoi he thong (DLL + Heap + Hash Remove)");
            System.out.println("7. Hien thi truc quan hang doi cap cuu (Heap visualization)");
            System.out.println("8. Hien thi truc quan lich su benh an (Doubly Linked List)");
            System.out.println("9. Hien thi danh sach bac si truc ca (Circular Linked List)");
            System.out.println("0. Thoat");
            System.out.print(">> Chon chuc nang (0-9): ");

            String choiceStr = scanner.nextLine().trim();
            int choice;
            try {
                choice = Integer.parseInt(choiceStr);
            } catch (NumberFormatException e) {
                System.out.println("[WARNING] Lua chon khong hop le. Vui long nhap so tu 0 den 9!\n");
                continue;
            }

            switch (choice) {
                case 1: // Đăng ký
                    String id;
                    while (true) {
                        System.out.print("Nhap ID benh nhan (de trong de tu dong sinh ID): ");
                        id = scanner.nextLine().trim();
                        if (id.isEmpty()) {
                            id = "auto";
                            break;
                        }
                        if (id.matches("^[a-zA-Z0-9-]+$")) {
                            break;
                        }
                        System.out.println(
                                "[LOI] ID chi duoc chua chu cai, so va dau gach ngang. Vui long nhap lai!");
                    }

                    String name;
                    while (true) {
                        System.out.print("Nhap ten benh nhan: ");
                        name = scanner.nextLine().trim();
                        if (name.matches("^[\\p{L}\\s]+$")) {
                            break;
                        }
                        System.out.println(
                                "[LOI] Ten chi duoc chua chu cai, khong chua so hoac ky tu dac biet. Vui long nhap lai!");
                    }

                    int age = 0;
                    while (true) {
                        System.out.print("Nhap tuoi: ");
                        try {
                            age = Integer.parseInt(scanner.nextLine().trim());
                            if (age > 0) {
                                break;
                            } else {
                                System.out.println("[LOI] Tuoi phai la so nguyen duong. Vui long nhap lai!");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("[LOI] Tuoi chi duoc nhap so. Vui long nhap lai!");
                        }
                    }

                    String gender;
                    while (true) {
                        System.out.print("Nhap gioi tinh (Nam/Nu): ");
                        gender = scanner.nextLine().trim();
                        if (gender.equalsIgnoreCase("Nam") || gender.equalsIgnoreCase("Nu")
                                || gender.equalsIgnoreCase("Nữ")) {
                            break;
                        }
                        System.out.println("[LOI] Gioi tinh chi nhan 'Nam' hoac 'Nu'. Vui long nhap lai!");
                    }

                    String phone;
                    while (true) {
                        System.out.print("Nhap so dien thoai: ");
                        phone = scanner.nextLine().trim();
                        if (phone.matches("^\\d{10}$")) {
                            break;
                        }
                        System.out.println(
                                "[LOI] So dien thoai chi duoc nhap so va phai co dung 10 chu so. Vui long nhap lai!");
                    }

                    String symptom;
                    while (true) {
                        System.out.print("Nhap trieu chung: ");
                        symptom = scanner.nextLine().trim();
                        if (symptom.matches("^[\\p{L}\\s]+$")) {
                            break;
                        }
                        System.out.println(
                                "[LOI] Trieu chung chi duoc chua chu cai, khong chua so hoac ky tu dac biet. Vui long nhap lai!");
                    }

                    int priority = 4;
                    while (true) {
                        System.out.print("Nhap muc do uu tien (1: Nguy kich -> 4: Nhe): ");
                        try {
                            priority = Integer.parseInt(scanner.nextLine().trim());
                            if (priority >= 1 && priority <= 4) {
                                break;
                            } else {
                                System.out.println("[LOI] Muc do uu tien chi tu 1 den 4. Vui long nhap lai!");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("[LOI] Muc do uu tien phai la so. Vui long nhap lai!");
                        }
                    }

                    try {
                        Patient p = service.register(id, name, age, gender, phone, symptom, priority);
                        System.out.println("[SYSTEM] Da dang ky thanh cong!");
                        System.out.println("         Ho so: " + p.getId() + " - " + p.getName() + " (Muc uu tien: "
                                + p.getPriorityLabel() + ")");
                    } catch (Exception e) {
                        System.out.println("[LOI] Dang ky that bai: " + e.getMessage());
                    }
                    break;

                case 2: // Gọi khám
                    System.out.println("[SYSTEM] Tien hanh goi kham tu hang doi Min-Heap...");
                    Patient called = service.callNextPatient();
                    if (called == null) {
                        System.out.println("[INFO] Khong co benh nhan nao trong hang doi cho kham.");
                    } else {
                        System.out.println("[GOI KHAM THANH CONG]");
                        System.out.println("  - Benh nhan: " + called.getId() + " | " + called.getName() + " (Uu tien: "
                                + called.getPriorityLabel() + ")");
                        System.out.println("  - Bac si phan cong: " + called.getDoctor());
                        System.out.println("  - Phong kham: " + called.getRoom());
                        System.out.println("  - Goi luc: " + called.getCalledAt());
                    }
                    break;

                case 3: // Hoàn thành ca khám & tạo bệnh án
                    System.out.print("Nhap ID benh nhan hoan thanh kham (VD: BN-0001): ");
                    String compId = scanner.nextLine().trim();
                    Patient checkingPatient = service.searchTriage(compId);
                    if (checkingPatient == null) {
                        System.out.println("[LOI] Khong tim thay benh nhan co ID: " + compId);
                        break;
                    }
                    if (!checkingPatient.getStatus().equals(Patient.STATUS_EXAMINING)) {
                        System.out.println("[LOI] Benh nhan nay khong o trang thai 'Dang kham' (can goi kham truoc)!");
                        break;
                    }

                    System.out.print("Nhap chan doan lam sang: ");
                    String diagnosis = scanner.nextLine().trim();
                    System.out.print("Nhap don thuoc dieu tri: ");
                    String prescription = scanner.nextLine().trim();
                    System.out.print("Nhap ghi chu them: ");
                    String notes = scanner.nextLine().trim();

                    boolean compSuccess = service.completeExam(compId, diagnosis, prescription, notes);
                    if (compSuccess) {
                        System.out.println(
                                "[SYSTEM] Kham xong! Da tu dong tao Benh an (Medical Record) va luu vao ho so.");
                    } else {
                        System.out.println("[LOI] Ghi benh an that bai.");
                    }
                    break;

                case 4: // Tra cứu O(1) và lịch sử bệnh án
                    System.out.print("Nhap ID benh nhan can tra cuu (O(1) HashTable): ");
                    String searchId = scanner.nextLine().trim();
                    long start = System.nanoTime();
                    Patient found = service.searchTriage(searchId);
                    long duration = System.nanoTime() - start;

                    if (found == null) {
                        System.out.println("[INFO] Khong tim thay benh nhan nao co ID: " + searchId);
                    } else {
                        System.out.println("[HO SO BENH NHAN - TRA CUU HASH TABLE O(1) trong " + duration + " ns]");
                        System.out.println("  - ID: " + found.getId() + " | Ho ten: " + found.getName() + " | Tuoi: "
                                + found.getAge());
                        System.out.println(
                                "  - So dien thoai: " + found.getPhone() + " | Gioi tinh: " + found.getGender());
                        System.out.println("  - Trieu chung ban dau: " + found.getSymptom());
                        System.out.println("  - Muc uu tien cap cuu: " + found.getPriorityLabel());
                        System.out.println("  - Trang thai kham hien tai: " + found.getStatusLabel());
                        if (!found.getDoctor().isEmpty()) {
                            System.out.println(
                                    "  - Bac si dieu tri gan nhat: " + found.getDoctor() + " tai " + found.getRoom());
                        }

                        // Hiển thị danh sách bệnh án (MedicalRecords) của bệnh nhân này
                        List<MedicalRecord> records = found.getMedicalHistory();
                        if (!records.isEmpty()) {
                            System.out.println("\n  -- LICH SU BENH AN CHI TIET (Medical Records) --");
                            for (MedicalRecord rec : records) {
                                System.out.println("    + Ma benh an: " + rec.getRecordId() + " (Kham luc: "
                                        + rec.getFormattedVisitDate() + ")");
                                System.out.println("      * Bac si thuc hien: " + rec.getDoctorId());
                                System.out.println("      * Chan doan lam sang: " + rec.getDiagnosis());
                                System.out.println("      * Toa thuoc duoc cap : " + rec.getPrescription());
                                System.out.println("      * Ghi chu y khoa     : " + rec.getNotes());
                            }
                            System.out.println("  ----------------------------------------------");
                        }
                    }
                    break;

                case 5: // Cập nhật độ ưu tiên
                    System.out.print("Nhap ID benh nhan can doi do uu tien: ");
                    String updateId = scanner.nextLine().trim();
                    int newPriority = 4;
                    while (true) {
                        System.out.print("Nhap do uu tien moi (1-4): ");
                        try {
                            newPriority = Integer.parseInt(scanner.nextLine().trim());
                            if (newPriority >= 1 && newPriority <= 4) {
                                break;
                            } else {
                                System.out.println("[LOI] Muc do uu tien chi tu 1 den 4. Vui long nhap lai!");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("[LOI] Muc do uu tien phai la so. Vui long nhap lai!");
                        }
                    }

                    try {
                        service.updatePriority(updateId, newPriority);
                        System.out.println(
                                "[SYSTEM] Da cap nhat do uu tien va can bang lai hang doi Heap cho: " + updateId);
                    } catch (Exception e) {
                        System.out.println("[LOI] Cap nhat that bai: " + e.getMessage());
                    }
                    break;

                case 6: // Xóa bệnh nhân
                    System.out.print("Nhap ID benh nhan can xoa khoi toan he thong: ");
                    String delId = scanner.nextLine().trim();
                    boolean delSuccess = service.deleteById(delId);
                    if (delSuccess) {
                        System.out.println("[SYSTEM] Da xoa hoan toan thong tin benh nhan: " + delId);
                    } else {
                        System.out.println("[LOI] Khong ton tai ID benh nhan: " + delId);
                    }
                    break;

                case 7: // In heap
                    System.out.println("[TRUC QUAN HOA HANG DOI MIN-HEAP]");
                    PatientNode[] arr = service.getTriageQueueArray();
                    System.out.println("\n  ┌── Hang doi uu tien (Heap size=" + arr.length + ") ──");
                    if (arr.length == 0) {
                        System.out.println("  │  [Hang doi rong]");
                    } else {
                        int limit = Math.min(arr.length, 20);
                        for (int i = 0; i < limit; i++) {
                            String marker = (i == 0) ? " ← GOC (Uu tien nhat)" : "";
                            System.out.printf("  │  [%2d] %-6s | priority=%d | ho_ten=%s%s%n",
                                    i, arr[i].getPatientID(), arr[i].getPriorityScore(),
                                    service.searchTriage(arr[i].getPatientID()).getName(), marker);
                        }
                        if (arr.length > limit) {
                            System.out.printf("  │  ... (va %d benh nhan khac dang cho trong hang doi) ...%n", arr.length - limit);
                        }
                    }
                    System.out.println("  └────────────────────────────────\n");
                    break;

                case 8: // In DLL
                    System.out.println("[TRUC QUAN HOA LICH SU BENH AN (DOUBLY LINKED LIST)]");
                    service.printDiagram();
                    service.printTable();
                    break;

                case 9: // In CLL
                    System.out.println("[TRUC QUAN HOA DOI BAC SI DIEU PHOI (CIRCULAR LINKED LIST)]");
                    List<String> docs = service.getDoctorsList();
                    System.out.println("\n  ┌── Vong xoay bac si truc ──");
                    for (int i = 0; i < docs.size(); i++) {
                        System.out.println("  │  " + (i + 1) + ". " + docs.get(i));
                    }
                    System.out.println("  │  [Vong noi: " + docs.get(docs.size() - 1) + " -> " + docs.get(0) + "]");
                    System.out.println("  └───────────────────────────\n");
                    break;

                case 0:
                    isRunning = false;
                    System.out.println("[SYSTEM] Da dong chuong trinh CLI.");
                    break;

                default:
                    System.out.println("[LOI] Lua chon khong hop le!");
            }
            System.out.println();
        }
        scanner.close();
    }
}
