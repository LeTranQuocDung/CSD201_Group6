/**
 * DLLNode.java — Node của Danh Sách Liên Kết Đôi
 */
public class DLLNode {
    Patient data;   // Dữ liệu bệnh nhân
    DLLNode prev;   // Con trỏ tới node TRƯỚC
    DLLNode next;   // Con trỏ tới node SAU

    public DLLNode(Patient data) {
        this.data = data;
        this.prev = null;
        this.next = null;
    }
}
