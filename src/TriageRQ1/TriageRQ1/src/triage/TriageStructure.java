package triage;

/**
 * Interface chung cho 3 cấu trúc dữ liệu được so sánh trong RQ1.
 * Mỗi cấu trúc phải cài đặt insert và extractMin.
 */
public interface TriageStructure {

    /** Thêm bệnh nhân vào cấu trúc. */
    void insert(Patient patient);

    /**
     * Lấy ra và xóa bệnh nhân có priority nhỏ nhất (khẩn cấp nhất).
     * @return Patient, hoặc null nếu rỗng.
     */
    Patient extractMin();

    /** Số bệnh nhân hiện tại. */
    int size();

    /** Tên cấu trúc, dùng in báo cáo. */
    String getName();
}
