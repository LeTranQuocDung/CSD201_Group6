package triage;

import java.util.PriorityQueue;

/**
 * Cấu trúc 1: Min-Heap
 * Dùng java.util.PriorityQueue (cài đặt bằng binary heap nội bộ).
 *
 * Độ phức tạp lý thuyết:
 *   insert     -> O(log n)
 *   extractMin -> O(log n)
 */
public class MinHeapTriage implements TriageStructure {

    private final PriorityQueue<Patient> heap;

    public MinHeapTriage() {
        // Comparator dựa trên priority (Patient implements Comparable)
        this.heap = new PriorityQueue<>();
    }

    @Override
    public void insert(Patient patient) {
        heap.offer(patient);
    }

    @Override
    public Patient extractMin() {
        return heap.poll();   // trả về null nếu rỗng
    }

    @Override
    public int size() {
        return heap.size();
    }

    @Override
    public String getName() {
        return "Min-Heap";
    }
}
