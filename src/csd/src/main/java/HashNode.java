/**
 * HashNode.java
 * Lớp đại diện cho một phần tử (node) trong Hash Table sử dụng Chaining để giải quyết đụng độ.
 */
public class HashNode {
    String key;     // ID bệnh nhân (Mã khóa)
    Patient value;  // Hồ sơ bệnh nhân (Giá trị lưu trữ)
    HashNode next;  // Con trỏ trỏ tới node tiếp theo khi xảy ra đụng độ (Chaining)

    public HashNode(String key, Patient value) {
        this.key = key;
        this.value = value;
        this.next = null;
    }
}
