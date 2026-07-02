import java.util.ArrayList;
import java.util.List;

/**
 * DoublyLinkedList.java — Danh Sách Liên Kết Đôi
 * Ứng dụng: Lịch sử Bệnh Án
 */
public class DoublyLinkedList {

    private DLLNode head; // Con trỏ đầu danh sách
    private DLLNode tail; // Con trỏ cuối danh sách
    private int     size; // Số node hiện có

    public DoublyLinkedList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    /**
     * Thêm bệnh nhân vào CUỐI danh sách — O(1)
     */
    public DLLNode push(Patient data) {
        DLLNode node = new DLLNode(data);

        if (tail == null) {
            head = node;
            tail = node;
        } else {
            node.prev = tail;
            tail.next = node;
            tail      = node;
        }

        size++;
        return node;
    }

    /**
     * Thêm bệnh nhân vào ĐẦU danh sách — O(1)
     */
    public DLLNode unshift(Patient data) {
        DLLNode node = new DLLNode(data);

        if (head == null) {
            head = node;
            tail = node;
        } else {
            node.next = head;
            head.prev = node;
            head      = node;
        }

        size++;
        return node;
    }

    /**
     * Xóa bệnh nhân theo ID — O(n)
     */
    public boolean deleteById(String id) {
        DLLNode node = findNodeById(id);
        if (node == null) return false;

        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }

        node.prev = null;
        node.next = null;

        size--;
        return true;
    }

    /**
     * Xóa node ở ĐẦU danh sách — O(1)
     */
    public Patient popHead() {
        if (head == null) return null;

        Patient data = head.data;

        if (head == tail) {
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

    private DLLNode findNodeById(String id) {
        DLLNode cur = head;
        while (cur != null) {
            if (cur.data.getId().equals(id)) return cur;
            cur = cur.next;
        }
        return null;
    }

    public Patient findById(String id) {
        DLLNode node = findNodeById(id);
        return (node != null) ? node.data : null;
    }

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

    public Patient getByIndex(int index) {
        if (index < 0 || index >= size) return null;
        DLLNode cur = head;
        for (int i = 0; i < index; i++) cur = cur.next;
        return cur.data;
    }

    /**
     * Cập nhật trạng thái bệnh nhân
     */
    public boolean updateStatus(String id, String status, String doctor, String room) {
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

    public List<Patient> toList() {
        List<Patient> result = new ArrayList<>();
        DLLNode cur = head;
        while (cur != null) {
            result.add(cur.data);
            cur = cur.next;
        }
        return result;
    }

    public List<Patient> toListReverse() {
        List<Patient> result = new ArrayList<>();
        DLLNode cur = tail;
        while (cur != null) {
            result.add(cur.data);
            cur = cur.prev;
        }
        return result;
    }

    public List<Patient> filterByStatus(String status) {
        List<Patient> result = new ArrayList<>();
        DLLNode cur = head;
        while (cur != null) {
            if (cur.data.getStatus().equals(status)) result.add(cur.data);
            cur = cur.next;
        }
        return result;
    }

    public List<Patient> filterByPriority(int priority) {
        List<Patient> result = new ArrayList<>();
        DLLNode cur = head;
        while (cur != null) {
            if (cur.data.getPriority() == priority) result.add(cur.data);
            cur = cur.next;
        }
        return result;
    }

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

    public Navigator navigatorFromHead() {
        return new Navigator(head);
    }

    public Navigator navigatorFromTail() {
        return new Navigator(tail);
    }

    public int     getSize()  { return size; }
    public boolean isEmpty()  { return size == 0; }
    public Patient getHead()  { return head != null ? head.data : null; }
    public Patient getTail()  { return tail != null ? tail.data : null; }

    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }

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
        System.out.printf("  %s^HEAD%n", buildRepeat('-', 98));
    }

    public void printTable() {
        System.out.println();
        System.out.printf("  %-3s %-12s %-22s %-5s %-16s %-16s %-18s%n",
            "STT", "ID", "Ho ten", "Tuoi", "Uu tien", "Trang thai", "Dang ky luc");
        System.out.println("  " + buildRepeat('-', 98));

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
        System.out.println("  " + buildRepeat('-', 98));
        System.out.printf("  Tong: %d benh nhan%n", size);
    }

    private static String buildRepeat(char c, int n) {
        char[] arr = new char[n];
        java.util.Arrays.fill(arr, c);
        return new String(arr);
    }
}
