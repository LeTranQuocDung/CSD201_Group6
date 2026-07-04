import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

public class HashTableTest {
    public static void main(String[] args) {
        String csvFile = "c:\\Users\\PC\\Documents\\GitHub\\CSD201_Group6\\data\\mock_data.csv";

        // Khởi tạo HashTable với capacity = 1,000,000 để giảm đụng độ
        HashTable hashTable = new HashTable(1000000);

        System.out.println("=== BAT DAU KIEM TRA HIEU SUAT HASH TABLE ===");
        System.out.println("Dang doc file: " + csvFile);

        // 1. Kiểm tra thời gian Thêm (Insert) 1 triệu bản ghi
        long startTime = System.nanoTime();
        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            br.readLine(); // Bỏ qua dòng tiêu đề
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length >= 4) {
                    String id = "BN-" + values[0];
                    String name = values[1];
                    int age = Integer.parseInt(values[2]);
                    int priority = Integer.parseInt(values[3]);

                    // Tạo đối tượng Patient với các thông tin mặc định còn thiếu
                    Patient p = new Patient(id, name, age, "Unknown", "0000000000", "None", priority);
                    hashTable.put(id, p);
                    count++;
                }
            }
        } catch (IOException e) {
            System.err.println("Loi doc file: " + e.getMessage());
            return;
        }
        long endTime = System.nanoTime();
        long insertTimeMs = (endTime - startTime) / 1000000;
        System.out.println("[1. INSERT] Da them " + count + " ban ghi vao HashTable mat: " + insertTimeMs + " ms");

        // 2. Kiểm tra thời gian Tìm kiếm (Search) ngẫu nhiên
        String[] searchKeys = { "BN-1", "BN-500000", "BN-999999", "BN-1000000", "BN-9999999" };
        System.out.println("\n[2. SEARCH] Kiem tra toc do tim kiem cac ID ngau nhien:");
        for (String key : searchKeys) {
            startTime = System.nanoTime();
            Patient p = hashTable.get(key);
            endTime = System.nanoTime();
            System.out.printf(" - Tim kiem %s: %10d ns (Ket qua: %s)\n", key, (endTime - startTime),
                    (p != null ? "Tim thay" : "Khong tim thay"));
        }

        // 3. Kiểm tra thời gian Xóa (Delete) ngẫu nhiên
        String[] deleteKeys = { "BN-100", "BN-500001", "BN-1000000", "BN-9999999" };
        System.out.println("\n[3. DELETE] Kiem tra toc do xoa cac ID ngau nhien:");
        for (String key : deleteKeys) {
            startTime = System.nanoTime();
            boolean removed = hashTable.remove(key);
            endTime = System.nanoTime();
            System.out.printf(" - Xoa %s: %10d ns (Ket qua: %s)\n", key, (endTime - startTime),
                    (removed ? "Thanh cong" : "That bai/Khong ton tai"));
        }

        // 4. Kiểm tra thời gian Cập nhật (Update Priority) 1000 lần
        System.out.println("\n[4. UPDATE] Kiem tra toc do cap nhat muc do uu tien 1000 lan:");
        long maxTime = 0;
        long totalTime = 0;
        Random rand = new Random();
        int updateCount = 1000;

        for (int i = 0; i < updateCount; i++) {
            // Random ID từ 1 đến 1,000,000 (dựa trên file mock data)
            String randomId = "BN-" + (rand.nextInt(1000000) + 1);
            int newPriority = rand.nextInt(4) + 1;

            long start = System.nanoTime();

            // Tìm và cập nhật
            Patient p = hashTable.get(randomId);
            if (p != null) {
                p.setPriority(newPriority);
                hashTable.put(randomId, p); // Cập nhật vào HashTable
            }

            long end = System.nanoTime();

            long timeTaken = end - start;
            if (timeTaken > maxTime) {
                maxTime = timeTaken;
            }
            totalTime += timeTaken;
        }

        long avgTime = totalTime / updateCount;
        System.out.println(" - Thoi gian trung binh (Average Time): " + avgTime + " ns");
        System.out.println(" - Thoi gian lon nhat (Max Time): " + maxTime + " ns");

        System.out.println("===============================================");
    }
}
