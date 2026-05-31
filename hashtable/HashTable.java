public class HashTable {
    private Node[] buckets;
    private int capacity;

    // Khởi tạo capacity lớn (vd: 2048) để triệt tiêu nhu cầu Rehashing như trong báo cáo
    public HashTable(int capacity) {
        this.capacity = capacity;
        this.buckets = new Node[capacity];
    }

    // Thuật toán Polynomial Rolling Hash chống đụng độ đảo chữ
    private int hash(String key) {
        long hashVal = 0;
        for (int i = 0; i < key.length(); i++) {
            hashVal = (hashVal * 31 + key.charAt(i)) % capacity;
        }
        return (int) Math.abs(hashVal); 
    }

    // Hàm Thêm/Cập nhật dữ liệu (Xử lý đụng độ bằng Chaining)
    public void put(String key, String value) {
        int index = hash(key);
        Node newNode = new Node(key, value);

        if (buckets[index] == null) {
            buckets[index] = newNode;
            return;
        }

        Node current = buckets[index];
        Node prev = null;
        
        while (current != null) {
            if (current.key.equals(key)) {
                current.value = value; // Cập nhật nếu trùng ID
                return;
            }
            prev = current;
            current = current.next;
        }
        
        // Nối Node mới vào cuối chuỗi
        prev.next = newNode;
    }

    // Hàm Lấy dữ liệu
    public String get(String key) {
        int index = hash(key);
        Node current = buckets[index];

        while (current != null) {
            if (current.key.equals(key)) {
                return current.value;
            }
            current = current.next;
        }
        return null; // Không tìm thấy
    }

    // Hàm Xóa dữ liệu (Sửa lỗi Memory Leak của AI theo chuẩn log của bạn)
    public boolean remove(String key) {
        int index = hash(key);
        Node current = buckets[index];
        Node prev = null;

        while (current != null) {
            if (current.key.equals(key)) {
                if (prev == null) {
                    // Xóa ở đầu chuỗi
                    buckets[index] = current.next;
                } else {
                    // Nối tắt qua Node hiện tại để xóa sạch sẽ
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