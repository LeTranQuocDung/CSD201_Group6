package triage;

/**
 * Cấu trúc 2: Sorted Linked List
 * Danh sách liên kết được giữ theo thứ tự priority tăng dần.
 *
 * Độ phức tạp lý thuyết:
 *   insert     -> O(n)   (tìm vị trí đúng rồi chèn)
 *   extractMin -> O(1)   (luôn lấy đầu danh sách)
 */
public class SortedLinkedListTriage implements TriageStructure {

    // Node nội bộ
    private static class Node {
        Patient data;
        Node next;
        Node(Patient data) { this.data = data; }
    }

    private Node head;
    private int size;

    @Override
    public void insert(Patient patient) {
        Node newNode = new Node(patient);

        // Chèn vào đầu nếu danh sách rỗng hoặc priority nhỏ hơn head
        if (head == null || patient.getPriority() < head.data.getPriority()) {
            newNode.next = head;
            head = newNode;
        } else {
            // Tìm vị trí đúng để giữ thứ tự tăng dần
            Node current = head;
            while (current.next != null
                    && current.next.data.getPriority() <= patient.getPriority()) {
                current = current.next;
            }
            newNode.next = current.next;
            current.next = newNode;
        }
        size++;
    }

    @Override
    public Patient extractMin() {
        if (head == null) return null;
        Patient min = head.data;
        head = head.next;
        size--;
        return min;
    }

    @Override
    public int size() { return size; }

    @Override
    public String getName() { return "Sorted Linked List"; }
}
