/**
 * HashTable.java
 * Cấu trúc bảng băm (Hash Table) xử lý đụng độ bằng phương pháp Chaining.
 * Khóa: String (ID bệnh nhân)
 * Giá trị: Patient (Hồ sơ bệnh nhân)
 */
public class HashTable {
    private HashNode[] buckets;
    private int capacity;

    // Khởi tạo capacity lớn (vd: 2048) để giảm thiểu đụng độ
    public HashTable(int capacity) {
        this.capacity = capacity;
        this.buckets = new HashNode[capacity];
    }

    // Thuật toán Polynomial Rolling Hash chống đụng độ đảo chữ
    private int hash(String key) {
        long hashVal = 0;
        for (int i = 0; i < key.length(); i++) {
            hashVal = (hashVal * 31 + key.charAt(i)) % capacity;
        }
        return (int) Math.abs(hashVal); 
    }

    // Hàm Thêm/Cập nhật dữ liệu
    public void put(String key, Patient value) {
        int index = hash(key);
        HashNode newNode = new HashNode(key, value);

        if (buckets[index] == null) {
            buckets[index] = newNode;
            return;
        }

        HashNode current = buckets[index];
        HashNode prev = null;
        
        while (current != null) {
            if (current.key.equals(key)) {
                current.value = value; // Cập nhật nếu trùng khóa ID
                return;
            }
            prev = current;
            current = current.next;
        }
        
        // Nối Node mới vào cuối chuỗi
        prev.next = newNode;
    }

    // Hàm Lấy dữ liệu hồ sơ bệnh nhân
    public Patient get(String key) {
        int index = hash(key);
        HashNode current = buckets[index];

        while (current != null) {
            if (current.key.equals(key)) {
                return current.value;
            }
            current = current.next;
        }
        return null; // Không tìm thấy
    }

    // Hàm Xóa dữ liệu
    public boolean remove(String key) {
        int index = hash(key);
        HashNode current = buckets[index];
        HashNode prev = null;

        while (current != null) {
            if (current.key.equals(key)) {
                if (prev == null) {
                    // Xóa ở đầu chuỗi
                    buckets[index] = current.next;
                } else {
                    // Nối tắt qua Node hiện tại để xóa
                    prev.next = current.next;
                }
                return true; // Xóa thành công
            }
            prev = current;
            current = current.next;
        }
        return false; // Không tìm thấy ID để xóa
    }
}
