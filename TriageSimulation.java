import java.util.Scanner;

// ================================================================
//  FILE: TriageSimulation.java
// ================================================================
public class TriageSimulation {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        TriageMinHeap triage = new TriageMinHeap(100);

        banner("HỆ THỐNG PHÂN LOẠI BỆNH NHÂN THÔNG MINH");
        System.out.println("  Cấu trúc: Array-based Min-Heap");
        System.out.println("  Quy ước : Priority 1 (Nguy kịch) -> 4 (Nhẹ nhất)\n");

        boolean isRunning = true;

        while (isRunning) {
            System.out.println("================= MENU =================");
            System.out.println("1. Thêm bệnh nhân mới (UC-01)");
            System.out.println("2. Lấy bệnh nhân ưu tiên nhất (UC-02)");
            System.out.println("3. Cập nhật độ ưu tiên (UC-03)");
            System.out.println("4. Xóa bệnh nhân (UC-04)");
            System.out.println("5. In danh sách hàng đợi");
            System.out.println("0. Thoát chương trình");
            System.out.print("Chọn chức năng (0-5): ");
            
            String choiceStr = scanner.nextLine();
            int choice;
            try {
                choice = Integer.parseInt(choiceStr);
            } catch (NumberFormatException e) {
                System.out.println("Lỗi: Vui lòng nhập số hợp lệ!\n");
                continue;
            }

            switch (choice) {
                case 1:
                    System.out.print("Nhập ID bệnh nhân: ");
                    String id = scanner.nextLine();
                    System.out.print("Nhập mức ưu tiên (1-4): ");
                    try {
                        int priority = Integer.parseInt(scanner.nextLine());
                        triage.push(new PatientNode(id, priority));
                    } catch (Exception e) {
                        System.out.println("Thêm thất bại: " + e.getMessage());
                    }
                    break;

                case 2:
                    try {
                        triage.pop();
                    } catch (RuntimeException e) {
                        System.out.println(e.getMessage());
                    }
                    break;

                case 3:
                    System.out.print("Nhập ID bệnh nhân cần cập nhật: ");
                    String updateId = scanner.nextLine();
                    System.out.print("Nhập mức ưu tiên mới (1-4): ");
                    try {
                        int newPriority = Integer.parseInt(scanner.nextLine());
                        triage.updatePriority(updateId, newPriority);
                    } catch (Exception e) {
                        System.out.println("Cập nhật thất bại: " + e.getMessage());
                    }
                    break;

                case 4:
                    System.out.print("Nhập ID bệnh nhân cần xóa: ");
                    String removeId = scanner.nextLine();
                    triage.remove(removeId);
                    break;

                case 5:
                    triage.printHeap();
                    break;

                case 0:
                    isRunning = false;
                    System.out.println("Đã thoát chương trình.");
                    break;

                default:
                    System.out.println("Lựa chọn không hợp lệ, vui lòng thử lại.");
            }
            System.out.println(); // In dòng trống cho dễ nhìn
        }
        
        scanner.close();
    }

    private static void banner(String title) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 55; i++) {
            sb.append("=");
        }
        String line = sb.toString();

        System.out.println("\n" + line);
        System.out.println("  " + title);
        System.out.println(line);
    }
}