/**
 * DoctorNode class representing a single doctor node in the Circular Linked List.
 */
class DoctorNode {
    Doctor doctor;
    DoctorNode next;

    public DoctorNode(Doctor doctor) {
        this.doctor = doctor;
        this.next = null;
    }
}

/**
 * Circular Linked List (CLL) implementation for coordinating continuous doctor shifts.
 */
public class CircularLinkedList {
    private DoctorNode head;
    private DoctorNode tail;
    private DoctorNode current; // Cursor to track the current doctor on shift
    private int size;

    public CircularLinkedList() {
        this.head = null;
        this.tail = null;
        this.current = null;
        this.size = 0;
    }

    public void addDoctor(Doctor doctor) {
        DoctorNode newNode = new DoctorNode(doctor);

        if (head == null) {
            head = newNode;
            tail = newNode;
            newNode.next = head; // Self-loop for the first node
            current = head;      
        } else {
            tail.next = newNode;
            tail = newNode;
            tail.next = head; // Lock the circular reference here
        }
        size++;
    }

    public Doctor nextDoctor() {
        if (current == null) {
            return null; // Defensive programming
        }

        Doctor activeDoctor = current.doctor;
        current = current.next; // Slide the cursor to the next doctor
        return activeDoctor;
    }

    public int getSize() {
        return this.size;
    }

    /**
     * Trả về danh sách tên bác sĩ trong vòng xoay để hiển thị trực quan.
     */
    public java.util.List<String> toList() {
        java.util.List<String> list = new java.util.ArrayList<>();
        if (head == null) return list;
        DoctorNode temp = head;
        do {
            // Đánh dấu bác sĩ hiện tại chuẩn bị nhận ca tiếp theo
            String statusMarker = (temp == current) ? " [Tiep theo]" : "";
            list.add(temp.doctor.getDoctorName() + " (" + temp.doctor.getRoom() + ")" + statusMarker);
            temp = temp.next;
        } while (temp != head);
        return list;
    }
}