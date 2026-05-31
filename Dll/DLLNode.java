/**
 * ╔══════════════════════════════════════════════════════╗
 * ║  DLLNode.java — Node của Danh Sách Liên Kết Đôi     ║
 * ╠══════════════════════════════════════════════════════╣
 * ║  Cấu trúc:                                           ║
 * ║    ┌──────┬──────────────────┬──────┐               ║
 * ║    │ prev │  data (Patient)  │ next │               ║
 * ║    └──────┴──────────────────┴──────┘               ║
 * ║                                                      ║
 * ║  prev → trỏ về node phía TRƯỚC (predecessor)        ║
 * ║  next → trỏ về node phía SAU  (successor)           ║
 * ╚══════════════════════════════════════════════════════╝
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