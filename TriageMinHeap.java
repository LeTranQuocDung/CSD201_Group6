import java.util.Arrays;

// ================================================================
//  FILE: TriageMinHeap.java
// ================================================================
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
            throw new IllegalArgumentException("Node không được null.");

        this.heap[this.size] = node;    
        heapifyUp(this.size);      
        this.size++;

        System.out.printf("[THÊM]      %-6s | priority=%d\n", node.getPatientID(), node.getPriorityScore());
    }

    public PatientNode pop() {
        if (this.size == 0)
            throw new RuntimeException("Hàng đợi rỗng — không có bệnh nhân nào!");

        PatientNode root = this.heap[0];  
        this.heap[0] = this.heap[this.size - 1];
        this.size--;
        this.heap[this.size] = null; 

        if (this.size > 0) heapifyDown(0);

        System.out.printf("[LẤY RA]   %-6s | priority=%d  → đưa vào điều trị%n", root.getPatientID(), root.getPriorityScore());
        return root;
    }

    public void updatePriority(String patientID, int newScore) {
        // Cập nhật điều kiện kiểm tra từ 10 xuống 4
        if (newScore < 1 || newScore > 4)
            throw new IllegalArgumentException("Score phải trong [1, 4].");

        int idx = findIndex(patientID);
        if (idx == -1)
            throw new RuntimeException("Không tìm thấy bệnh nhân: " + patientID);

        int oldScore = this.heap[idx].getPriorityScore();
        this.heap[idx].setPriorityScore(newScore);

        System.out.printf("[CẬP NHẬT] %-6s | priority %d → %d%n", patientID, oldScore, newScore);

        if (newScore < oldScore) {
            heapifyUp(idx);   
        } else {
            heapifyDown(idx); 
        }
    }

    public boolean remove(String patientID) {
        int idx = findIndex(patientID);
        if (idx == -1) {
            System.out.printf("[XÓA]      %-6s | KHÔNG TÌM THẤY trong hàng đợi%n", patientID);
            return false;
        }

        this.heap[idx].setPriorityScore(Integer.MIN_VALUE);
        heapifyUp(idx);
        PatientNode removed = pop();
        System.out.printf("[XÓA]      %-6s | đã xóa khỏi hàng đợi%n", removed.getPatientID());
        return true;
    }

    public void printHeap() {
        System.out.println("\n  ┌── Hàng đợi hiện tại (size=" + this.size + ") ──");
        if (this.size == 0) {
            System.out.println("  │  [rỗng]");
        } else {
            for (int i = 0; i < this.size; i++) {
                String marker = (i == 0) ? " ← GỐC (ưu tiên cao nhất)" : "";
                System.out.printf("  │  [%2d] %-6s | priority=%d%s%n",
                                  i, this.heap[i].getPatientID(), this.heap[i].getPriorityScore(), marker);
            }
        }
        System.out.println("  └────────────────────────────────\n");
    }

    public boolean isEmpty() { return this.size == 0; }
    public int     getSize() { return this.size; }
}