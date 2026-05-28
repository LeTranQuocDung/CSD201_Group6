/**
 * Node class representing a single Doctor node in the Circular Linked List.
 */
class Node {
    String doctorName;
    Node next;

    public Node(String doctorName) {
        this.doctorName = doctorName;
        this.next = null;
    }
}

/**
 * CircularLinkedList (CLL) implementation for Doctor Shift/Triage Rotation.
 */
public class CircularLinkedList {
    private Node head;
    private Node tail;
    private Node current; // Cursor to track current doctor on shift
    private int size;

    public CircularLinkedList() {
        this.head = null;
        this.tail = null;
        this.current = null;
        this.size = 0;
    }

    /**
     * Adds a new doctor to the circular linked list.
     * Maintain tail.next = head circular property.
     */
    public void addDoctor(String name) {
        Node newNode = new Node(name);

        if (head == null) {
            // Case 1: Empty list
            head = newNode;
            tail = newNode;
            newNode.next = head; // Point to itself
            current = head;      // Set cursor to head
        } else {
            // Case 2: Nodes already exist
            tail.next = newNode;
            tail = newNode;
            tail.next = head;    // CRITICAL: Ngắt nối vòng tròn ở đây (point tail back to head)
        }
        size++;
    }

    /**
     * Fetches current doctor, slides cursor to the next doctor node.
     * Wraps around automatically due to the tail.next = head configuration.
     * @return Doctor's name, or null if list is empty
     */
    public String nextDoctor() {
        if (current == null) {
            return null; // Safe check for empty doctor list
        }

        // 1. Capture the doctor's name under the cursor
        String activeDoctor = current.doctorName;

        // 2. Advance the cursor to the next node (handles wrap-around automatically)
        current = current.next;

        // 3. Return the active doctor
        return activeDoctor;
    }

    public int getSize() {
        return this.size;
    }

    /**
     * Quick main method to demonstrate Phase 1 requirement:
     * Add A, B, C. Call next 4 times -> expected A, B, C, A.
     */
    public static void main(String[] args) {
        CircularLinkedList cll = new CircularLinkedList();
        cll.addDoctor("A");
        cll.addDoctor("B");
        cll.addDoctor("C");

        System.out.println("--- Test 4 next Doctor calls (Expected: A, B, C, A) ---");
        for (int i = 1; i <= 4; i++) {
            System.out.println("Call #" + i + ": " + cll.nextDoctor());
        }
    }
}
// Code updated to force NetBeans automatic compile-on-save.

