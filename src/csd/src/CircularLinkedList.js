/**
 * Class Node represents a single doctor node in the Circular Linked List
 */
class Node {
    constructor(doctorName) {
        this.doctorName = doctorName; // Name of the doctor (e.g., "Doctor A")
        this.next = null;             // Pointer to the next doctor node
    }
}

/**
 * Class CircularLinkedList (CLL) for Doctor Shift Management
 * This class rotates active doctors in a circular manner.
 */
class CircularLinkedList {
    constructor() {
        this.head = null;    // First doctor in the list
        this.tail = null;    // Last doctor in the list
        this.current = null; // Cursor to track who is currently on duty/triage
        this.size = 0;       // Number of doctors in the queue
    }

    /**
     * Adds a new doctor to the end of the circular list
     * @param {string} name - Name of the doctor
     */
    addDoctor(name) {
        const newNode = new Node(name);

        if (!this.head) {
            // Case 1: The list is empty
            this.head = newNode;
            this.tail = newNode;
            newNode.next = this.head;   // Self-reference to create the circle
            this.current = this.head;   // Start the rotation cursor at Head
        } else {
            // Case 2: The list already has doctors
            this.tail.next = newNode;   // Link old tail to new node
            this.tail = newNode;        // Update tail pointer to the new node
            this.tail.next = this.head; // CRITICAL: Ngắt nối vòng tròn ở đây (point tail back to head)
        }
        this.size++;
    }

    /**
     * Retrieves the current doctor, and rotates the cursor to the next doctor.
     * Handles wrapping from tail to head automatically due to circular references.
     * @returns {string|null} Name of the doctor called, or null if list is empty
     */
    nextDoctor() {
        if (!this.current) {
            return null; // Handle edge case: empty list
        }

        // 1. Get the name of the current active doctor
        const activeDoctorName = this.current.doctorName;

        // 2. Slide the current cursor to the next doctor in the circular list
        // Since tail.next points to head, this will wrap around automatically!
        this.current = this.current.next;

        // 3. Return the doctor name
        return activeDoctorName;
    }

    /**
     * Returns an array representation of all doctors starting from head.
     * Useful for API output, rendering frontend lists, or logging.
     * @returns {string[]} Array of doctor names
     */
    toArray() {
        if (!this.head) return [];
        
        const doctors = [];
        let curr = this.head;
        do {
            doctors.push(curr.doctorName);
            curr = curr.next;
        } while (curr !== this.head);
        
        return doctors;
    }
}

// Export for server usage (CommonJS style)
if (typeof module !== 'undefined' && module.exports) {
    module.exports = { Node, CircularLinkedList };
}
