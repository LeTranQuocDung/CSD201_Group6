/**
 * TriageMinHeap.java
 * Cấu trúc dữ liệu Min-Heap lưu trữ hàng đợi phân loại bệnh nhân (Triage Queue).
 * Ưu tiên các bệnh nhân có mức độ khẩn cấp cao nhất (priority = 1).
 */
public class TriageMinHeap {

    private PatientNode[] heap;     
    private int           size;     
    private int           capacity; 

    public TriageMinHeap(int capacity) {
        this.capacity = capacity;
        this.heap     = new PatientNode[this.capacity];
        this.size     = 0;
    }

    private int parent(int i)     { return (i - 1) / 2; }
    private int leftChild(int i)  { return 2 * i + 1;   }
    private int rightChild(int i) { return 2 * i + 2;   }

    private void swap(int i, int j) {
        PatientNode tmp = this.heap[i];
        this.heap[i]         = this.heap[j];
        this.heap[j]         = tmp;
    }

    private void heapifyUp(int index) {
        while (index > 0) {
            int p = parent(index);
            if (this.heap[index].hasHigherUrgencyThan(this.heap[p])) {
                swap(index, p);
                index = p; 
            } else {
                break; 
            }
        }
    }

    private void heapifyDown(int index) {
        while (true) {
            int mostUrgent = index;   
            int left       = leftChild(index);
            int right      = rightChild(index);

            if (left < this.size && this.heap[left].hasHigherUrgencyThan(this.heap[mostUrgent]))
                mostUrgent = left;

            if (right < this.size && this.heap[right].hasHigherUrgencyThan(this.heap[mostUrgent]))
                mostUrgent = right;

            if (mostUrgent != index) {
                swap(index, mostUrgent);
                index = mostUrgent; 
            } else {
                break; 
            }
        }
    }

    private int findIndex(String patientID) {
        for (int i = 0; i < this.size; i++) {
            if (this.heap[i].getPatientID().equals(patientID))
                return i;
        }
        return -1;
    }

    public PatientNode getPatientNode(String patientID) {
        int idx = findIndex(patientID);
        if (idx != -1) return this.heap[idx];
        return null;
    }

    public void push(PatientNode node) {
        if (this.size == this.capacity) {
            this.capacity = (this.capacity == 0) ? 10 : this.capacity * 2; 
            if (this.heap == null) {
                this.heap = new PatientNode[this.capacity];
            } else {
                this.heap = java.util.Arrays.copyOf(this.heap, this.capacity);
            }
        }
        
        if (node == null)
            throw new IllegalArgumentException("Node khong duoc null.");

        this.heap[this.size] = node;    
        heapifyUp(this.size);      
        this.size++;

        System.out.printf("[THEM HA]   %-6s | priority=%d\n", node.getPatientID(), node.getPriorityScore());
    }

    public PatientNode pop() {
        if (this.size == 0)
            throw new RuntimeException("Hang doi rong — khong co benh nhan nao!");

        PatientNode root = this.heap[0];  
        this.heap[0] = this.heap[this.size - 1];
        this.size--;
        this.heap[this.size] = null; 

        if (this.size > 0) heapifyDown(0);

        System.out.printf("[LAY HA]    %-6s | priority=%d  → dua vao dieu tri%n", root.getPatientID(), root.getPriorityScore());
        return root;
    }

    public void updatePriority(String patientID, int newScore) {
        if (newScore < 1 || newScore > 4)
            throw new IllegalArgumentException("Score phai trong [1, 4].");

        int idx = findIndex(patientID);
        if (idx == -1)
            throw new RuntimeException("Khong tim thay benh nhan trong hang doi: " + patientID);

        int oldScore = this.heap[idx].getPriorityScore();
        this.heap[idx].setPriorityScore(newScore);

        System.out.printf("[CAP NHAT HA] %-6s | priority %d → %d%n", patientID, oldScore, newScore);

        if (newScore < oldScore) {
            heapifyUp(idx);   
        } else {
            heapifyDown(idx); 
        }
    }

    public boolean remove(String patientID) {
        int idx = findIndex(patientID);
        if (idx == -1) {
            System.out.printf("[XOA HA]    %-6s | KHONG TIM THAY trong hang doi%n", patientID);
            return false;
        }

        this.heap[idx].setPriorityScore(Integer.MIN_VALUE);
        heapifyUp(idx);
        PatientNode removed = pop();
        System.out.printf("[XOA HA]    %-6s | da xoa khoi hang doi%n", removed.getPatientID());
        return true;
    }

    public void printHeap() {
        System.out.println("\n  ┌── Hang doi hien tai (size=" + this.size + ") ──");
        if (this.size == 0) {
            System.out.println("  │  [rong]");
        } else {
            for (int i = 0; i < this.size; i++) {
                String marker = (i == 0) ? " ← GOC (uu tien cao nhat)" : "";
                System.out.printf("  │  [%2d] %-6s | priority=%d%s%n",
                                  i, this.heap[i].getPatientID(), this.heap[i].getPriorityScore(), marker);
            }
        }
        System.out.println("  └────────────────────────────────\n");
    }

    // Trả về mảng để hiển thị trên UI
    public PatientNode[] getHeapArray() {
        return java.util.Arrays.copyOf(this.heap, this.size);
    }

    public boolean isEmpty() { return this.size == 0; }
    public int     getSize() { return this.size; }
}
