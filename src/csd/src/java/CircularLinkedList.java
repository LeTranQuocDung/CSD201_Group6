/**
 * DoctorNode class representing a single doctor node in the Circular Linked List.
 */
class DoctorNode {
    String doctorName;
    DoctorNode next;

    public DoctorNode(String doctorName) {
        this.doctorName = doctorName;
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

    public void addDoctor(String name) {
        DoctorNode newNode = new DoctorNode(name);

        if (head == null) {
            head = newNode;
            tail = newNode;
            newNode.next = head; // Self-loop for the first node
            current = head;      
        } else {
            tail.next = newNode;
            tail = newNode;
            // CRITICAL POINT: Lock the circular reference here
            tail.next = head;    
        }
        size++;
    }

    public String nextDoctor() {
        if (current == null) {
            return null; // Defensive programming: Prevent server crash if the list is empty
        }

        String activeDoctor = current.doctorName;
        current = current.next; // Slide the cursor to the next doctor
        return activeDoctor;
    }

    public int getSize() {
        return this.size;
    }

    // ==========================================
    // MAIN METHOD FOR TESTING
    // ==========================================
    public static void main(String[] args) {
        CircularLinkedList cll = new CircularLinkedList();

        // Add 3 doctors to the list
        cll.addDoctor("Doctor A");
        cll.addDoctor("Doctor B");
        cll.addDoctor("Doctor C");

        System.out.println("Total doctors on shift: " + cll.getSize());
        System.out.println("--- Starting shift rotation (Expected: A -> B -> C -> A) ---");

        // Call nextDoctor 4 times to prove the cursor wraps around back to A
        for (int i = 1; i <= 4; i++) {
            System.out.println("Call #" + i + ": " + cll.nextDoctor());
        }
    }
}