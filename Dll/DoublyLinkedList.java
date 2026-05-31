import java.util.ArrayList;
import java.util.List;

/**
 * ╔════════════════════════════════════════════════════════════╗
 * ║     DoublyLinkedList.java — Danh Sách Liên Kết Đôi        ║
 * ║     Ứng dụng: Lịch sử Bệnh Án — MediCare System           ║
 * ╠════════════════════════════════════════════════════════════╣
 * ║                                                            ║
 * ║  Hình dạng (4 bệnh nhân):                                  ║
 * ║                                                            ║
 * ║  null←[BN-001]⇄[BN-002]⇄[BN-003]⇄[BN-004]→null          ║
 * ║        ▲head                        ▲tail                  ║
 * ║                                                            ║
 * ║  Độ phức tạp:                                              ║
 * ║  ┌─────────────────────────────────┬────────┐             ║
 * ║  │ push() / unshift()              │  O(1)  │             ║
 * ║  │ popHead() / popTail()           │  O(1)  │             ║
 * ║  │ findById() / deleteById()       │  O(n)  │             ║
 * ║  │ updateStatus()                  │  O(n)  │             ║
 * ║  │ toList() / toListReverse()      │  O(n)  │             ║
 * ║  │ filterByStatus()                │  O(n)  │             ║
 * ║  │ countByStatus()                 │  O(n)  │             ║
 * ║  └─────────────────────────────────┴────────┘             ║
 * ╚════════════════════════════════════════════════════════════╝
 */
public class DoublyLinkedList {

    // ── Fields ────────────────────────────────────────────────
    private DLLNode head; // Con trỏ đầu danh sách
    private DLLNode tail; // Con trỏ cuối danh sách — cho phép push() O(1)
    private int     size; // Số node hiện có

    // ── Constructor ───────────────────────────────────────────
    public DoublyLinkedList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    /* ══════════════════════════════════════════════════════════
       NHÓM 1 — THÊM NODE
       ══════════════════════════════════════════════════════════ */

    /**
     * Thêm bệnh nhân vào CUỐI danh sách — O(1)
     *
     * Trước:  ... ← [tail] → null
     * Sau:    ... ← [tail] ⇄ [node] → null
     *                           ▲ tail mới
     *
     * @param data Patient cần thêm
     * @return DLLNode vừa được tạo
     */
    public DLLNode push(Patient data) {
        DLLNode node = new DLLNode(data);

        if (tail == null) {
            // Danh sách đang rỗng
            head = node;
            tail = node;
        } else {
            node.prev = tail;   // node.prev → old tail
            tail.next = node;   // old tail.next → node
            tail      = node;   // cập nhật tail
        }

        size++;
        return node;
    }

    /**
     * Thêm bệnh nhân vào ĐẦU danh sách — O(1)
     *
     * Trước:  null ← [head] ⇄ ...
     * Sau:    null ← [node] ⇄ [head] ⇄ ...
     *                 ▲ head mới
     *
     * @param data Patient cần thêm
     * @return DLLNode vừa được tạo
     */
    public DLLNode unshift(Patient data) {
        DLLNode node = new DLLNode(data);

        if (head == null) {
            head = node;
            tail = node;
        } else {
            node.next = head;   // node.next → old head
            head.prev = node;   // old head.prev → node
            head      = node;   // cập nhật head
        }

        size++;
        return node;
    }

    /* ══════════════════════════════════════════════════════════
       NHÓM 2 — XÓA NODE
       ══════════════════════════════════════════════════════════ */

    /**
     * Xóa bệnh nhân theo ID — O(n) tìm + O(1) xóa
     *
     * 4 Edge Cases:
     *   Case 1 — Node DUY NHẤT : head = tail = null
     *   Case 2 — Xóa HEAD      : head tiến lên, head.prev = null
     *   Case 3 — Xóa TAIL      : tail lùi lại, tail.next = null
     *   Case 4 — Xóa GIỮA      : prev.next → next, next.prev → prev
     *
     * SAU KHI XÓA bắt buộc null hóa node.prev và node.next
     * để Java GC thu hồi bộ nhớ, tránh memory leak.
     *
     * @param id ID bệnh nhân cần xóa
     * @return true nếu xóa thành công, false nếu không tìm thấy
     */
    public boolean deleteById(String id) {
        DLLNode node = findNodeById(id);
        if (node == null) return false;

        // Bước 1: Cập nhật con trỏ của node TRƯỚC
        if (node.prev != null) {
            node.prev.next = node.next;   // prev bỏ qua node, trỏ sang next
        } else {
            head = node.next;             // node là HEAD → head mới = node.next
        }

        // Bước 2: Cập nhật con trỏ của node SAU
        if (node.next != null) {
            node.next.prev = node.prev;   // next bỏ qua node, trỏ sang prev
        } else {
            tail = node.prev;             // node là TAIL → tail mới = node.prev
        }

        // Bước 3: Null hóa — để GC thu hồi bộ nhớ node bị xóa
        node.prev = null;
        node.next = null;

        // Bước 4: Giảm size
        size--;
        return true;
    }

    /**
     * Xóa node ở ĐẦU danh sách — O(1)
     * @return Patient bị xóa, hoặc null nếu danh sách rỗng
     */
    public Patient popHead() {
        if (head == null) return null;

        Patient data = head.data;

        if (head == tail) {
            // Chỉ còn 1 node
            head = null;
            tail = null;
        } else {
            head      = head.next;
            head.prev = null;
        }

        size--;
        return data;
    }

    /**
     * Xóa node ở CUỐI danh sách — O(1)
     * @return Patient bị xóa, hoặc null nếu danh sách rỗng
     */
    public Patient popTail() {
        if (tail == null) return null;

        Patient data = tail.data;

        if (head == tail) {
            head = null;
            tail = null;
        } else {
            tail      = tail.prev;
            tail.next = null;
        }

        size--;
        return data;
    }

    /* ══════════════════════════════════════════════════════════
       NHÓM 3 — TÌM KIẾM
       ══════════════════════════════════════════════════════════ */

    /**
     * Tìm DLLNode theo patient.id — O(n)
     * Private: chỉ dùng nội bộ trong class
     */
    private DLLNode findNodeById(String id) {
        DLLNode cur = head;
        while (cur != null) {
            if (cur.data.getId().equals(id)) return cur;
            cur = cur.next;
        }
        return null;
    }

    /**
     * Tìm Patient theo ID — O(n)
     * @return Patient nếu tìm thấy, null nếu không có
     */
    public Patient findById(String id) {
        DLLNode node = findNodeById(id);
        return (node != null) ? node.data : null;
    }

    /**
     * Tìm Patient theo tên (không phân biệt hoa thường) — O(n)
     * @return Patient đầu tiên khớp, null nếu không có
     */
    public Patient findByName(String name) {
        String keyword = name.trim().toLowerCase();
        DLLNode cur = head;
        while (cur != null) {
            if (cur.data.getName().toLowerCase().contains(keyword)) {
                return cur.data;
            }
            cur = cur.next;
        }
        return null;
    }

    /**
     * Lấy Patient tại vị trí index (0-based) — O(n)
     * @return Patient hoặc null nếu index không hợp lệ
     */
    public Patient getByIndex(int index) {
        if (index < 0 || index >= size) return null;
        DLLNode cur = head;
        for (int i = 0; i < index; i++) cur = cur.next;
        return cur.data;
    }

    /* ══════════════════════════════════════════════════════════
       NHÓM 4 — CẬP NHẬT
       ══════════════════════════════════════════════════════════ */

    /**
     * Cập nhật trạng thái bệnh nhân — O(n) tìm + O(1) set
     *
     * CHỈ cập nhật field được truyền vào (khác null),
     * giữ nguyên các field còn lại (name, age, phone...).
     *
     * @param id     ID bệnh nhân
     * @param status Trạng thái mới (null nếu không đổi)
     * @param doctor Tên bác sĩ   (null nếu không đổi)
     * @param room   Phòng khám   (null nếu không đổi)
     * @return true nếu cập nhật thành công
     */
    public boolean updateStatus(String id, String status,
                                String doctor, String room) {
        Patient p = findById(id);
        if (p == null) return false;

        String now = java.time.LocalDateTime.now()
                     .format(java.time.format.DateTimeFormatter
                     .ofPattern("HH:mm:ss dd/MM/yyyy"));

        if (status != null) {
            p.setStatus(status);
            if (Patient.STATUS_EXAMINING.equals(status)) p.setCalledAt(now);
            if (Patient.STATUS_DONE.equals(status))      p.setCompletedAt(now);
        }
        if (doctor != null) p.setDoctor(doctor);
        if (room   != null) p.setRoom(room);

        return true;
    }

    /* ══════════════════════════════════════════════════════════
       NHÓM 5 — DUYỆT & LỌC
       ══════════════════════════════════════════════════════════ */

    /**
     * Duyệt XUÔI: head → tail (đăng ký cũ → mới) — O(n)
     * Dùng cho: export báo cáo, hiển thị theo thứ tự thời gian
     * @return List<Patient> theo thứ tự đăng ký
     */
    public List<Patient> toList() {
        List<Patient> result = new ArrayList<>();
        DLLNode cur = head;
        while (cur != null) {
            result.add(cur.data);
            cur = cur.next;
        }
        return result;
    }

    /**
     * Duyệt NGƯỢC: tail → head (mới nhất → cũ nhất) — O(n)
     * Dùng cho: màn hình lịch sử (hiển thị bệnh nhân mới nhất trước)
     * @return List<Patient> mới nhất trước
     */
    public List<Patient> toListReverse() {
        List<Patient> result = new ArrayList<>();
        DLLNode cur = tail;
        while (cur != null) {
            result.add(cur.data);
            cur = cur.prev;
        }
        return result;
    }

    /**
     * Lọc bệnh nhân theo trạng thái — O(n)
     * @param status "waiting" / "examining" / "done"
     */
    public List<Patient> filterByStatus(String status) {
        List<Patient> result = new ArrayList<>();
        DLLNode cur = head;
        while (cur != null) {
            if (cur.data.getStatus().equals(status)) result.add(cur.data);
            cur = cur.next;
        }
        return result;
    }

    /**
     * Lọc bệnh nhân theo mức ưu tiên — O(n)
     * @param priority 1 (cao nhất) đến 5 (thấp nhất)
     */
    public List<Patient> filterByPriority(int priority) {
        List<Patient> result = new ArrayList<>();
        DLLNode cur = head;
        while (cur != null) {
            if (cur.data.getPriority() == priority) result.add(cur.data);
            cur = cur.next;
        }
        return result;
    }

    /**
     * Đếm số bệnh nhân theo từng trạng thái — O(n)
     * @return int[] { [0]=waiting, [1]=examining, [2]=done }
     */
    public int[] countByStatus() {
        int[] counts = {0, 0, 0};
        DLLNode cur = head;
        while (cur != null) {
            switch (cur.data.getStatus()) {
                case Patient.STATUS_WAITING:   counts[0]++; break;
                case Patient.STATUS_EXAMINING: counts[1]++; break;
                case Patient.STATUS_DONE:      counts[2]++; break;
            }
            cur = cur.next;
        }
        return counts;
    }

    /* ══════════════════════════════════════════════════════════
       NHÓM 6 — NAVIGATOR
       ══════════════════════════════════════════════════════════ */

    /**
     * Tạo Navigator bắt đầu từ HEAD (bệnh nhân đăng ký đầu tiên)
     */
    public Navigator navigatorFromHead() {
        return new Navigator(head);
    }

    /**
     * Tạo Navigator bắt đầu từ TAIL (bệnh nhân đăng ký mới nhất)
     */
    public Navigator navigatorFromTail() {
        return new Navigator(tail);
    }

    /* ══════════════════════════════════════════════════════════
       NHÓM 7 — UTILITY & VISUALIZATION
       ══════════════════════════════════════════════════════════ */

    public int     getSize()  { return size; }
    public boolean isEmpty()  { return size == 0; }
    public Patient getHead()  { return head != null ? head.data : null; }
    public Patient getTail()  { return tail != null ? tail.data : null; }

    /** Xóa toàn bộ danh sách */
    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }

    /**
     * In sơ đồ ASCII trực quan của DLL ra console.
     *
     *   null <- [BN-001: An | Cho kham] <-> [BN-002: Binh | Dang kham] -> null
     *           ^HEAD                                               ^TAIL
     */
    public void printDiagram() {
        System.out.println();
        if (isEmpty()) {
            System.out.println("  null <-> (danh sach rong) <-> null");
            return;
        }

        StringBuilder line = new StringBuilder("  null <- ");
        DLLNode cur = head;
        while (cur != null) {
            Patient p = cur.data;
            line.append("[").append(p.getId())
                .append(": ").append(p.getName())
                .append(" | ").append(p.getStatusLabel())
                .append("]");
            if (cur.next != null) line.append(" <-> ");
            cur = cur.next;
        }
        line.append(" -> null");
        System.out.println(line);
        System.out.printf("  %s^HEAD%n", " ".repeat(9));
    }

    /**
     * In bảng danh sách đầy đủ ra console.
     */
    public void printTable() {
        System.out.println();
        System.out.printf("  %-3s %-12s %-22s %-5s %-16s %-16s %-18s%n",
            "STT", "ID", "Ho ten", "Tuoi", "Uu tien", "Trang thai", "Dang ky luc");
        System.out.println("  " + "-".repeat(98));

        DLLNode cur = head;
        int idx = 1;
        while (cur != null) {
            Patient p = cur.data;
            System.out.printf("  %-3d %-12s %-22s %-5d %-16s %-16s %-18s%n",
                idx, p.getId(), p.getName(), p.getAge(),
                p.getPriorityLabel(), p.getStatusLabel(), p.getRegisteredAt());
            cur = cur.next;
            idx++;
        }
        System.out.println("  " + "-".repeat(98));
        System.out.printf("  Tong: %d benh nhan%n", size);
    }
}