package triage;

import java.util.ArrayList;
import java.util.List;

/**
 * Cấu trúc 3: Unsorted Array (dùng ArrayList)
 * Không sắp xếp khi insert, tìm min khi extract.
 *
 * Độ phức tạp lý thuyết:
 *   insert     -> O(1)   (thêm vào cuối)
 *   extractMin -> O(n)   (quét toàn bộ để tìm min)
 */
public class UnsortedArrayTriage implements TriageStructure {

    private final List<Patient> list;

    public UnsortedArrayTriage() {
        this.list = new ArrayList<>();
    }

    @Override
    public void insert(Patient patient) {
        list.add(patient);   // O(1) amortized
    }

    @Override
    public Patient extractMin() {
        if (list.isEmpty()) return null;

        // Tìm index của phần tử có priority nhỏ nhất -> O(n)
        int minIndex = 0;
        for (int i = 1; i < list.size(); i++) {
            if (list.get(i).getPriority() < list.get(minIndex).getPriority()) {
                minIndex = i;
            }
        }
        // Xóa và trả về
        return list.remove(minIndex);
    }

    @Override
    public int size() { return list.size(); }

    @Override
    public String getName() { return "Unsorted Array"; }
}
