import java.util.Scanner;

public class main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=== HỆ THỐNG TRIAGE CẤP CỨU (HASH TABLE) ===");
        System.out.print(">> Vui lòng nhập giới hạn (capacity) của bảng băm: ");
        
        int capacity = 2048; // Giá trị dự phòng nếu nhập sai
        try {
            capacity = Integer.parseInt(scanner.nextLine().trim());
            if (capacity <= 0) {
                System.out.println("[WARNING] Giới hạn phải lớn hơn 0. Hệ thống tự động gán mặc định = 2048.");
                capacity = 2048;
            }
        } catch (NumberFormatException e) {
            System.out.println("[WARNING] Nhập sai định dạng số! Hệ thống tự động gán mặc định = 2048.");
        }

        // Khởi tạo HashTable với giới hạn vừa nhập
        HashTable triageTable = new HashTable(capacity); 
        System.out.println("[SYSTEM] Đã khởi tạo Hash Table thành công với sức chứa: " + capacity + " buckets.");

        // Vòng lặp menu chính
        while (true) {
            System.out.println("\nChọn thao tác:");
            System.out.println("1. Nhập bệnh nhân (put)");
            System.out.println("2. Tra cứu hồ sơ (get)");
            System.out.println("3. Xóa bệnh nhân (remove)");
            System.out.println("4. Thoát");
            System.out.print(">> Lựa chọn: ");
            
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.print("Nhập ID (VD: ER-101): ");
                    String idPut = scanner.nextLine().trim();
                    System.out.print("Nhập thông tin (Data/Priority): ");
                    String data = scanner.nextLine().trim();
                    
                    triageTable.put(idPut, data);
                    System.out.println("[SYSTEM] Đã lưu thông tin vào Hash Table.");
                    break;

                case "2":
                    System.out.print("Nhập ID cần tìm: ");
                    String idGet = scanner.nextLine().trim();
                    
                    String result = triageTable.get(idGet);
                    if (result != null) {
                        System.out.println("[LOG] " + idGet + " -> " + result);
                    } else {
                        System.out.println("[LOG] KHÔNG TÌM THẤY dữ liệu cho ID: " + idGet);
                    }
                    break;
                    
                case "3":
                    System.out.print("Nhập ID cần xóa: ");
                    String idRemove = scanner.nextLine().trim();
                    
                    boolean isRemoved = triageTable.remove(idRemove);
                    if (isRemoved) {
                        System.out.println("[SYSTEM] Đã xóa thành công hồ sơ: " + idRemove);
                    } else {
                        System.out.println("[ERROR] Không tồn tại ID: " + idRemove + " để xóa!");
                    }
                    break;

                case "4":
                    System.out.println("[SYSTEM] Đã đóng hệ thống Triage.");
                    scanner.close();
                    return;

                default:
                    System.out.println("[ERROR] Lựa chọn không hợp lệ! Vui lòng nhập 1, 2, 3 hoặc 4.");
            }
        }
    }
}