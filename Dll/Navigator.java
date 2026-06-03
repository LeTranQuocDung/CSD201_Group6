/**
 * ╔══════════════════════════════════════════════════════╗
 * ║  Navigator.java — Con trỏ duyệt hai chiều           ║
 * ╠══════════════════════════════════════════════════════╣
 * ║  Cho phép di chuyển tiến / lùi trong DLL mà         ║
 * ║  KHÔNG làm thay đổi cấu trúc danh sách.             ║
 * ║                                                      ║
 * ║  Ứng dụng: "Xem bệnh án trước / tiếp theo"          ║
 * ║                                                      ║
 * ║  Cách dùng:                                          ║
 * ║    Navigator nav = dll.navigatorFromHead();          ║
 * ║    nav.get()  → bệnh nhân hiện tại                  ║
 * ║    nav.next() → tiến sang bệnh nhân kế tiếp         ║
 * ║    nav.prev() → lùi về bệnh nhân trước đó           ║
 * ╚══════════════════════════════════════════════════════╝
 */
public class Navigator {
    private DLLNode current; // Con trỏ tại vị trí hiện tại

    public Navigator(DLLNode startNode) {
        this.current = startNode;
    }

    /**
     * Lấy bệnh nhân tại vị trí hiện tại.
     * @return Patient hoặc null nếu danh sách rỗng
     */
    public Patient get() {
        return (current != null) ? current.data : null;
    }

    /**
     * Tiến sang bệnh nhân TIẾP THEO (→ next) — O(1)
     * @return Patient tiếp theo, hoặc null nếu đang ở tail
     */
    public Patient next() {
        if (current != null && current.next != null) {
            current = current.next;
            return current.data;
        }
        return null; // Đã ở cuối danh sách
    }

    /**
     * Lùi về bệnh nhân TRƯỚC ĐÓ (← prev) — O(1)
     * @return Patient trước đó, hoặc null nếu đang ở head
     */
    public Patient prev() {
        if (current != null && current.prev != null) {
            current = current.prev;
            return current.data;
        }
        return null; // Đã ở đầu danh sách
    }

    /** Kiểm tra có thể tiến tiếp không */
    public boolean hasNext() {
        return current != null && current.next != null;
    }

    /** Kiểm tra có thể lùi không */
    public boolean hasPrev() {
        return current != null && current.prev != null;
    }
}
