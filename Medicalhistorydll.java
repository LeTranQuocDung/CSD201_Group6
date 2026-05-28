// ================================================================
//  FILE: MedicalHistoryDLL.java
//  Mô tả: Doubly Linked List lưu LỊCH SỬ BỆNH ÁN của bệnh nhân.
//
//  Cấu trúc:
//    null ← [HEAD: bản ghi cũ nhất] ↔ [...] ↔ [TAIL: bản ghi mới nhất] → null
//
//  Tại sao dùng DLL thay vì Array / SLL?
//  ┌─────────────────────────┬────────┬────────┬──────────┐
//  │ Thao tác                │  DLL   │  SLL   │  Array   │
//  ├─────────────────────────┼────────┼────────┼──────────┤
//  │ Thêm bản ghi mới (tail) │  O(1)  │  O(1)* │  O(1)**  │
//  │ Xóa node giữa           │  O(1)  │  O(n)  │  O(n)    │
//  │ Duyệt ngược (mới→cũ)    │  O(n)  │  O(n)† │  O(n)    │
//  │ Lấy N bản ghi gần nhất  │  O(k)  │  O(n)  │  O(k)    │
//  └─────────────────────────┴────────┴────────┴──────────┘
//  *  SLL cần giữ thêm tail pointer
//  ** Amortized (resize khi đầy)
//  †  SLL phải đi từ HEAD đến TAIL trước (O(n) thừa)
//
//  Các thao tác chính:
//    addRecord(node)               — Thêm bản ghi vào TAIL   O(1)
//    deleteRecord(recordID)        — Xóa theo ID             O(n)+O(1)
//    updateRecord(...)             — Cập nhật bản ghi        O(n)
//    findRecord(recordID)          — Tìm theo ID             O(n)
//    traverseForward()             — Duyệt cũ → mới          O(n)
//    traverseBackward()            — Duyệt mới → cũ          O(n)
//    getLatestRecords(k)           — Lấy k bản ghi gần nhất  O(k)
//    printDLL()                    — In danh sách
//    printStructure()              — In cấu trúc con trỏ
// ================================================================

public class MedicalHistoryDLL {

    private MedicalRecordNode head;     // bản ghi CŨ NHẤT
    private MedicalRecordNode tail;     // bản ghi MỚI NHẤT
    private int               size;     // số bản ghi hiện có
    private String            patientID;

    // ── Constructor ──────────────────────────────────────────
    public MedicalHistoryDLL(String patientID) {
        if (patientID == null || patientID.isBlank())
            throw new IllegalArgumentException("patientID không được rỗng.");
        this.patientID = patientID;
        this.head      = null;
        this.tail      = null;
        this.size      = 0;
    }

    // ================================================================
    //  THÊM bản ghi mới vào TAIL — O(1)
    // ================================================================
    //  Tại sao O(1)? Vì ta luôn giữ con trỏ tail, thêm vào cuối ngay.
    //  Sơ đồ:
    //    Trước: ... ↔ [TAIL]          → null
    //    Sau:   ... ↔ [TAIL] ↔ [NEW] → null
    //                            ↑ tail mới
    // ================================================================
    public void addRecord(MedicalRecordNode node) {
        if (node == null)
            throw new IllegalArgumentException("Node không được null.");

        if (tail == null) {
            // ── CASE: DLL rỗng ──────────────────────────────
            // Node đầu tiên vừa là HEAD vừa là TAIL
            head = node;
            tail = node;
            // node.prev = null, node.next = null (mặc định)

        } else {
            // ── CASE: DLL đã có ít nhất 1 node ─────────────
            node.prev  = tail;   // Bước 1: node mới nhìn về TAIL cũ
            tail.next  = node;   // Bước 2: TAIL cũ nhìn về node mới
            tail       = node;   // Bước 3: cập nhật TAIL → node mới
        }

        size++;
        System.out.printf("[THÊM]      %-12s | %-22s | %s%n",
                          node.recordID,
                          truncate(node.diagnosis, 22),
                          node.severity);
    }

    // ================================================================
    //  XÓA bản ghi theo recordID — O(n) tìm + O(1) xóa
    // ================================================================
    //  Xử lý đầy đủ 5 edge cases:
    //    (1) DLL rỗng
    //    (2) recordID không tồn tại
    //    (3) DLL chỉ có 1 nút  (head == tail)
    //    (4) Xóa HEAD
    //    (5) Xóa TAIL
    //    (6) Xóa node ở giữa
    // ================================================================
    public boolean deleteRecord(String recordID) {
        // ── EDGE CASE 1: DLL rỗng ──────────────────────────
        if (head == null) {
            System.out.printf("[XÓA]       %-12s | THẤT BẠI — danh sách đang rỗng%n", recordID);
            return false;
        }

        // Tìm node cần xóa (O(n))
        MedicalRecordNode target = findNode(recordID);

        // ── EDGE CASE 2: recordID không tồn tại ────────────
        if (target == null) {
            System.out.printf("[XÓA]       %-12s | KHÔNG TÌM THẤY trong danh sách%n", recordID);
            return false;
        }

        if (size == 1) {
            // ── EDGE CASE 3: DLL chỉ có 1 nút ─────────────
            // Cả head và tail phải → null
            head = null;
            tail = null;

        } else if (target == head) {
            // ── EDGE CASE 4: Xóa HEAD ──────────────────────
            head       = head.next; // head mới = node kế tiếp
            head.prev  = null;      // head mới không có node trước

        } else if (target == tail) {
            // ── EDGE CASE 5: Xóa TAIL ──────────────────────
            tail       = tail.prev; // tail mới = node trước đó
            tail.next  = null;      // tail mới không có node sau

        } else {
            // ── EDGE CASE 6: Xóa node GIỮA ─────────────────
            // Nối trực tiếp prev ↔ next, bỏ qua target
            target.prev.next = target.next;
            target.next.prev = target.prev;
        }

        // Ngắt liên kết của node đã xóa (giúp GC thu hồi bộ nhớ)
        target.prev = null;
        target.next = null;

        size--;
        System.out.printf("[XÓA]       %-12s | %-22s | đã xóa khỏi danh sách%n",
                          recordID, truncate(target.diagnosis, 22));
        return true;
    }

    // ================================================================
    //  CẬP NHẬT bản ghi — O(n)
    // ================================================================
    public boolean updateRecord(String recordID,  String newDiagnosis,
                                String newPrescription, String newNotes,
                                String newSeverity) {
        MedicalRecordNode node = findNode(recordID);

        if (node == null) {
            System.out.printf("[CẬP NHẬT]  %-12s | KHÔNG TÌM THẤY%n", recordID);
            return false;
        }

        // Chỉ cập nhật field nào được truyền vào (null = giữ nguyên)
        if (newDiagnosis    != null) node.diagnosis    = newDiagnosis;
        if (newPrescription != null) node.prescription = newPrescription;
        if (newNotes        != null) node.notes        = newNotes;
        if (newSeverity     != null) node.severity     = newSeverity;

        System.out.printf("[CẬP NHẬT]  %-12s | cập nhật thành công%n", recordID);
        return true;
    }

    // ================================================================
    //  TÌM KIẾM bản ghi theo recordID — O(n)
    // ================================================================
    public MedicalRecordNode findRecord(String recordID) {
        MedicalRecordNode node = findNode(recordID);

        if (node == null) {
            System.out.printf("[TÌM]       %-12s | KHÔNG TÌM THẤY%n", recordID);
            return null;
        }

        System.out.printf("[TÌM]       %-12s | ĐÃ TÌM THẤY%n", recordID);
        // Hiển thị con trỏ prev/next để minh họa cấu trúc DLL
        System.out.printf("             prev → %s%n",
            node.prev != null ? node.prev.recordID : "null (đây là HEAD)");
        node.display();
        System.out.printf("             next → %s%n",
            node.next != null ? node.next.recordID : "null (đây là TAIL)");
        return node;
    }

    // ================================================================
    //  DUYỆT XUÔI: HEAD → TAIL  (cũ → mới) — O(n)
    // ================================================================
    //  Bắt đầu từ HEAD, dùng current.next để tiến về TAIL.
    // ================================================================
    public void traverseForward() {
        System.out.println("\n  ┌── DUYỆT XUÔI (cũ → mới) | size=" + size + " ──");

        if (head == null) {
            System.out.println("  │  [danh sách rỗng]");
            System.out.println("  └────────────────────────────────────────\n");
            return;
        }

        System.out.printf("  │  %-12s | %-10s | %-22s | %-12s | Chi phí%n",
                          "ID Bản ghi", "Ngày khám", "Chẩn đoán", "Mức độ");
        System.out.println("  │  " + "─".repeat(75));

        MedicalRecordNode current = head; // bắt đầu từ HEAD
        int no = 1;

        while (current != null) {
            String tag = "";
            if (current == head) tag = " ← HEAD";
            if (current == tail) tag = " ← TAIL";
            System.out.printf("  │  [%2d]", no);
            current.displayShort();
            if (!tag.isEmpty()) System.out.printf("  │       %s%n", tag);
            current = current.next; // tiến về TAIL
            no++;
        }

        System.out.println("  └────────────────────────────────────────\n");
    }

    // ================================================================
    //  DUYỆT NGƯỢC: TAIL → HEAD  (mới → cũ) — O(n)
    // ================================================================
    //  ⭐ ĐÂY LÀ LÝ DO CHÍNH dùng DLL thay vì SLL:
    //     SLL phải đi từ HEAD đến TAIL trước → O(n) thừa.
    //     DLL có sẵn con trỏ TAIL → bắt đầu ngay, dùng current.prev.
    // ================================================================
    public void traverseBackward() {
        System.out.println("\n  ┌── DUYỆT NGƯỢC (mới → cũ) | size=" + size + " ──");
        System.out.println("  │  [Bắt đầu từ TAIL ngay — đặc tính DLL, không cần pass qua HEAD]");

        if (tail == null) {
            System.out.println("  │  [danh sách rỗng]");
            System.out.println("  └────────────────────────────────────────\n");
            return;
        }

        System.out.printf("  │  %-12s | %-10s | %-22s | %-12s | Chi phí%n",
                          "ID Bản ghi", "Ngày khám", "Chẩn đoán", "Mức độ");
        System.out.println("  │  " + "─".repeat(75));

        MedicalRecordNode current = tail; // BẮT ĐẦU TỪ TAIL NGAY
        int no = 1;

        while (current != null) {
            String tag = "";
            if (current == tail) tag = " ← TAIL (mới nhất)";
            if (current == head) tag = " ← HEAD (cũ nhất)";
            System.out.printf("  │  [%2d]", no);
            current.displayShort();
            if (!tag.isEmpty()) System.out.printf("  │       %s%n", tag);
            current = current.prev; // lui về HEAD (dùng con trỏ prev của DLL)
            no++;
        }

        System.out.println("  └────────────────────────────────────────\n");
    }

    // ================================================================
    //  LẤY K bản ghi GẦN NHẤT — O(k)
    // ================================================================
    //  Bắt đầu từ TAIL, dùng current.prev, dừng sau k bước.
    //  Không cần duyệt toàn bộ n node → O(k) thực sự.
    // ================================================================
    public void getLatestRecords(int k) {
        System.out.println("\n  ┌── " + k + " BẢN GHI GẦN NHẤT (bắt đầu từ TAIL) ──");

        if (tail == null) {
            System.out.println("  │  [danh sách rỗng]");
            System.out.println("  └────────────────────────────────────────\n");
            return;
        }

        if (k <= 0) {
            System.out.println("  │  k phải > 0");
            System.out.println("  └────────────────────────────────────────\n");
            return;
        }

        MedicalRecordNode current = tail; // bắt đầu từ bản ghi MỚI NHẤT
        int count = 0;

        System.out.printf("  │  %-12s | %-10s | %-22s | %-12s | Chi phí%n",
                          "ID Bản ghi", "Ngày khám", "Chẩn đoán", "Mức độ");
        System.out.println("  │  " + "─".repeat(75));

        while (current != null && count < k) {
            System.out.printf("  │  [%2d]", count + 1);
            current.displayShort();
            current = current.prev; // dùng prev — đặc tính DLL
            count++;
        }

        System.out.println("  └────────────────────────────────────────\n");
    }

    // ================================================================
    //  IN DANH SÁCH (mặc định: MỚI → CŨ)
    // ================================================================
    public void printDLL() {
        System.out.println("\n  ┌── Danh sách bệnh án của " + patientID
                         + " (size=" + size + ") ──");
        if (size == 0) {
            System.out.println("  │  [rỗng]");
        } else {
            MedicalRecordNode current = tail; // từ mới nhất
            int no = 1;
            while (current != null) {
                String marker = "";
                if (current == tail) marker = " ← MỚI NHẤT (TAIL)";
                if (current == head) marker = " ← CŨ NHẤT (HEAD)";
                System.out.printf("  │  [%2d] %-12s | %-22s | %s%s%n",
                                  no, current.recordID,
                                  truncate(current.diagnosis, 22),
                                  current.severity, marker);
                current = current.prev;
                no++;
            }
        }
        System.out.println("  └────────────────────────────────────────\n");
    }

    // ================================================================
    //  IN CẤU TRÚC DLL (hiển thị con trỏ prev/next)
    // ================================================================
    public void printStructure() {
        System.out.println("\n  ══ CẤU TRÚC DOUBLY LINKED LIST ══");
        System.out.println("  null");
        System.out.println("   ↑ head.prev = null");

        MedicalRecordNode current = head;
        int pos = 1;

        while (current != null) {
            String tag = (current == head) ? " [HEAD]" :
                         (current == tail) ? " [TAIL]" :
                         (" [" + pos + "]");

            System.out.println("  ┌─────────────────────────────────────┐");
            System.out.printf ("  │ prev ← %-29s│%n",
                current.prev != null ? current.prev.recordID : "null");
            System.out.printf ("  │ ID  : %-29s│  %s%n",
                current.recordID, tag);
            System.out.printf ("  │ diag: %-29s│%n",
                truncate(current.diagnosis, 29));
            System.out.printf ("  │ next → %-29s│%n",
                current.next != null ? current.next.recordID : "null");
            System.out.println("  └─────────────────────────────────────┘");

            if (current.next != null) {
                System.out.println("       ↕ prev/next");
            }
            current = current.next;
            pos++;
        }

        System.out.println("   ↓ tail.next = null");
        System.out.println("  null\n");
    }

    // ================================================================
    //  THỐNG KÊ bệnh án
    // ================================================================
    public void printStatistics() {
        System.out.println("\n  ═══════════ THỐNG KÊ BỆNH ÁN ═══════════");
        System.out.printf ("  Bệnh nhân    : %s%n", patientID);
        System.out.printf ("  Tổng lần khám: %d%n", size);
        System.out.printf ("  Bản ghi đầu  : %s%n",
            head != null ? head.recordID + " (" + head.date + ")" : "Không có");
        System.out.printf ("  Bản ghi cuối : %s%n",
            tail != null ? tail.recordID + " (" + tail.date + ")" : "Không có");

        if (size == 0) {
            System.out.println("  ═════════════════════════════════════════\n");
            return;
        }

        int nhe = 0, tb = 0, nang = 0, nguyKich = 0;
        double totalCost = 0;
        MedicalRecordNode current = head;

        while (current != null) {
            switch (current.severity) {
                case "Nhẹ":        nhe++;      break;
                case "Trung bình": tb++;        break;
                case "Nặng":       nang++;      break;
                case "Nguy kịch":  nguyKich++;  break;
            }
            totalCost += current.visitCost;
            current    = current.next;
        }

        System.out.println("  ─────────────────────────────────────────");
        System.out.printf ("  Nhẹ        : %d lần%n",       nhe);
        System.out.printf ("  Trung bình : %d lần%n",       tb);
        System.out.printf ("  Nặng       : %d lần%n",       nang);
        System.out.printf ("  Nguy kịch  : %d lần%n",       nguyKich);
        System.out.println("  ─────────────────────────────────────────");
        System.out.printf ("  Tổng chi phí     : %,.0f VNĐ%n", totalCost);
        System.out.printf ("  Chi phí trung bình: %,.0f VNĐ%n",
            size > 0 ? totalCost / size : 0);
        System.out.println("  ═════════════════════════════════════════\n");
    }

    // ================================================================
    //  VALIDATE — kiểm tra tính nhất quán nội bộ (dùng khi debug)
    // ================================================================
    public String validate() {
        if (size == 0) {
            if (head != null) return "FAIL: size=0 nhưng head != null";
            if (tail != null) return "FAIL: size=0 nhưng tail != null";
            return "OK";
        }
        if (head == null) return "FAIL: size>0 nhưng head == null";
        if (tail == null) return "FAIL: size>0 nhưng tail == null";
        if (head.prev != null) return "FAIL: head.prev phải là null";
        if (tail.next != null) return "FAIL: tail.next phải là null";

        // Đếm xuôi + kiểm tra liên kết prev
        MedicalRecordNode cur = head, prevNode = null;
        int countFwd = 0;
        while (cur != null) {
            if (cur.prev != prevNode)
                return "FAIL: prev pointer sai tại node " + countFwd;
            prevNode = cur;
            cur      = cur.next;
            countFwd++;
        }
        if (countFwd != size)
            return "FAIL: đếm xuôi=" + countFwd + " khác size=" + size;

        // Đếm ngược
        cur = tail;
        int countBwd = 0;
        while (cur != null) { cur = cur.prev; countBwd++; }
        if (countBwd != size)
            return "FAIL: đếm ngược=" + countBwd + " khác size=" + size;

        return "OK";
    }

    // ================================================================
    //  PRIVATE HELPERS
    // ================================================================
    private MedicalRecordNode findNode(String recordID) {
        if (recordID == null) return null;
        MedicalRecordNode current = head;
        while (current != null) {
            if (current.recordID.equals(recordID)) return current;
            current = current.next;
        }
        return null;
    }

    private String truncate(String s, int max) {
        if (s == null || s.length() <= max) return s == null ? "" : s;
        return s.substring(0, max - 3) + "...";
    }

    // ── Getters ──────────────────────────────────────────────
    public boolean isEmpty()      { return size == 0;  }
    public int     getSize()      { return size;        }
    public String  getPatientID() { return patientID;   }

    public String getHeadID() { return head != null ? head.recordID : null; }
    public String getTailID() { return tail != null ? tail.recordID : null; }
}
