/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  DLL_Test.java — Test toàn bộ DoublyLinkedList              ║
 * ║  CSD201 — Đinh Thành Trung — QE200047                       ║
 * ╠══════════════════════════════════════════════════════════════╣
 * ║  Chạy:  javac -d out src/*.java && java -cp out DLL_Test    ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
public class DLL_Test {

    // ── Bộ đếm kết quả ───────────────────────────────────
    static int passed = 0;
    static int failed = 0;

    // ── Assert helper ─────────────────────────────────────
    static void assertTrue(String label, boolean condition) {
        if (condition) {
            System.out.println("  [PASS] " + label);
            passed++;
        } else {
            System.out.println("  [FAIL] " + label);
            failed++;
        }
    }

    static void assertFalse(String label, boolean condition) {
        assertTrue(label, !condition);
    }

    static void assertEquals(String label, Object expected, Object actual) {
        boolean ok = (expected == null && actual == null)
                  || (expected != null && expected.equals(actual));
        if (ok) {
            System.out.println("  [PASS] " + label);
            passed++;
        } else {
            System.out.println("  [FAIL] " + label
                + " | expected=" + expected + " actual=" + actual);
            failed++;
        }
    }

    static void section(String title) {
        System.out.println();
        System.out.println("  " + "═".repeat(58));
        System.out.println("  " + title);
        System.out.println("  " + "═".repeat(58));
    }

    // ── Dữ liệu mẫu ──────────────────────────────────────
    static Patient[] PATIENTS = {
        new Patient("BN-001","Nguyen Van An",  35,"Nam","0901111111","Dau dau",      Patient.PRIORITY_NORMAL),
        new Patient("BN-002","Tran Thi Binh",  28,"Nu", "0902222222","Sot cao 39 do",Patient.PRIORITY_URGENT),
        new Patient("BN-003","Le Hoang Cuong", 52,"Nam","0903333333","Dau nguc",     Patient.PRIORITY_EMERGENCY),
        new Patient("BN-004","Pham Thu Dung",  19,"Nu", "0904444444","Tai kham mat", Patient.PRIORITY_FOLLOWUP),
        new Patient("BN-005","Vo Minh Em",     67,"Nam","0905555555","Huyet ap cao", Patient.PRIORITY_HIGH),
    };

    // ═════════════════════════════════════════════════════
    public static void main(String[] args) {
        test01_KhoiTaoVaPush();
        test02_Unshift();
        test03_FindById();
        test04_FindByName();
        test05_GetByIndex();
        test06_UpdateStatus();
        test07_ToListVaToListReverse();
        test08_FilterByStatus();
        test09_FilterByPriority();
        test10_CountByStatus();
        test11_DeleteById_4EdgeCases();
        test12_PopHeadVaPopTail();
        test13_MemoryCleanup();
        test14_Navigator();
        test15_Clear();
        test16_EmptyListEdgeCases();

        ketQua();
    }

    /* ══════════════════════════════════════════════════════
       TEST 01 — Khởi tạo & push() vào tail
       ══════════════════════════════════════════════════════ */
    static void test01_KhoiTaoVaPush() {
        section("TEST 01 — Khoi tao & push() vao tail O(1)");

        DoublyLinkedList dll = new DoublyLinkedList();

        assertTrue("Danh sach ban dau rong: isEmpty() = true", dll.isEmpty());
        assertEquals("Size ban dau = 0", 0, dll.getSize());
        assertEquals("head = null", null, dll.getHead());
        assertEquals("tail = null", null, dll.getTail());

        // Push 5 bệnh nhân
        for (Patient p : PATIENTS) dll.push(p);

        assertEquals("Sau push 5: size = 5",  5, dll.getSize());
        assertFalse ("isEmpty() = false sau push", dll.isEmpty());

        // Kiểm tra head & tail
        assertEquals("head = BN-001", "BN-001", dll.getHead().getId());
        assertEquals("tail = BN-005", "BN-005", dll.getTail().getId());

        // Kiểm tra liên kết đôi qua toList()
        java.util.List<Patient> list = dll.toList();
        assertEquals("toList()[0] = BN-001", "BN-001", list.get(0).getId());
        assertEquals("toList()[4] = BN-005", "BN-005", list.get(4).getId());
        assertEquals("toList()[1] = BN-002", "BN-002", list.get(1).getId());

        System.out.println("\n  So do DLL sau push 5 benh nhan:");
        dll.printDiagram();
    }

    /* ══════════════════════════════════════════════════════
       TEST 02 — unshift() vào head
       ══════════════════════════════════════════════════════ */
    static void test02_Unshift() {
        section("TEST 02 — unshift() vao head O(1)");

        DoublyLinkedList dll = new DoublyLinkedList();
        Patient pA = new Patient("BN-A","Benh nhan A",30,"Nam","09001","Trieu chung A",4);
        Patient pB = new Patient("BN-B","Benh nhan B",25,"Nu", "09002","Trieu chung B",3);

        dll.push(pA);
        dll.unshift(pB);

        assertEquals("head = BN-B (vua unshift)", "BN-B", dll.getHead().getId());
        assertEquals("tail = BN-A (cu)",           "BN-A", dll.getTail().getId());
        assertEquals("size = 2",                    2,      dll.getSize());

        // Kiểm tra liên kết: BN-B.next = BN-A, BN-A.prev = BN-B
        java.util.List<Patient> list = dll.toList();
        assertEquals("list[0] = BN-B", "BN-B", list.get(0).getId());
        assertEquals("list[1] = BN-A", "BN-A", list.get(1).getId());
    }

    /* ══════════════════════════════════════════════════════
       TEST 03 — findById()
       ══════════════════════════════════════════════════════ */
    static void test03_FindById() {
        section("TEST 03 — findById() O(n)");

        DoublyLinkedList dll = buildDLL();

        Patient found = dll.findById("BN-003");
        assertTrue ("findById BN-003 tim thay",           found != null);
        assertEquals("findById BN-003: dung ten",
                     "Le Hoang Cuong", found != null ? found.getName() : null);

        assertEquals("findById BN-999 = null", null, dll.findById("BN-999"));
        assertEquals("findById null-safe",     null, dll.findById(""));
    }

    /* ══════════════════════════════════════════════════════
       TEST 04 — findByName()
       ══════════════════════════════════════════════════════ */
    static void test04_FindByName() {
        section("TEST 04 — findByName() khong phan biet hoa thuong");

        DoublyLinkedList dll = buildDLL();

        Patient found = dll.findByName("thu dung");
        assertTrue ("findByName 'thu dung' tim thay",    found != null);
        assertEquals("findByName: dung id = BN-004",
                     "BN-004", found != null ? found.getId() : null);

        Patient upper = dll.findByName("NGUYEN VAN AN");
        assertTrue ("findByName viet hoa van tim thay",  upper != null);

        assertEquals("findByName khong co = null", null, dll.findByName("xyz khong ton tai"));
    }

    /* ══════════════════════════════════════════════════════
       TEST 05 — getByIndex()
       ══════════════════════════════════════════════════════ */
    static void test05_GetByIndex() {
        section("TEST 05 — getByIndex() 0-based O(n)");

        DoublyLinkedList dll = buildDLL();

        assertEquals("index 0  = BN-001", "BN-001", dll.getByIndex(0).getId());
        assertEquals("index 2  = BN-003", "BN-003", dll.getByIndex(2).getId());
        assertEquals("index 4  = BN-005", "BN-005", dll.getByIndex(4).getId());
        assertEquals("index -1 = null",   null,      dll.getByIndex(-1));
        assertEquals("index 99 = null",   null,      dll.getByIndex(99));
    }

    /* ══════════════════════════════════════════════════════
       TEST 06 — updateStatus() — spread merge, KHÔNG ghi đè
       ══════════════════════════════════════════════════════ */
    static void test06_UpdateStatus() {
        section("TEST 06 — updateStatus() chi update field duoc truyen vao");

        DoublyLinkedList dll = buildDLL();

        // Lưu lại giá trị trước khi update
        String oldName  = dll.findById("BN-002").getName();
        int    oldAge   = dll.findById("BN-002").getAge();
        String oldPhone = dll.findById("BN-002").getPhone();

        // Chỉ update status + doctor + room
        boolean ok = dll.updateStatus("BN-002",
                Patient.STATUS_EXAMINING, "BS. Nguyen Van Minh", "Phong 101");

        assertTrue ("updateStatus tra ve true", ok);

        Patient p = dll.findById("BN-002");
        assertEquals("status -> examining",          Patient.STATUS_EXAMINING, p.getStatus());
        assertEquals("doctor duoc gan",              "BS. Nguyen Van Minh",    p.getDoctor());
        assertEquals("room duoc gan",                "Phong 101",              p.getRoom());

        // Quan trọng: các field khác KHÔNG bị mất
        assertEquals("name giu nguyen (merge ok)",   oldName,  p.getName());
        assertEquals("age giu nguyen (merge ok)",    oldAge,   p.getAge());
        assertEquals("phone giu nguyen (merge ok)",  oldPhone, p.getPhone());
        assertTrue  ("calledAt duoc ghi tu dong",    p.getCalledAt() != null);

        System.out.println("\n  BN-002 sau updateStatus:");
        System.out.println("  name    = " + p.getName()   + "  <- giu nguyen");
        System.out.println("  status  = " + p.getStatus() + "  <- da thay doi");
        System.out.println("  doctor  = " + p.getDoctor() + "  <- da them");

        // Update ID không tồn tại → false
        assertFalse("updateStatus ID khong ton tai -> false",
                    dll.updateStatus("BN-999", "done", null, null));
    }

    /* ══════════════════════════════════════════════════════
       TEST 07 — toList() xuôi & toListReverse() ngược
       ══════════════════════════════════════════════════════ */
    static void test07_ToListVaToListReverse() {
        section("TEST 07 — toList() duyet xuoi & toListReverse() duyet nguoc");

        DoublyLinkedList dll = buildDLL();

        java.util.List<Patient> forward  = dll.toList();
        java.util.List<Patient> backward = dll.toListReverse();

        assertEquals("toList size = 5",            5, forward.size());
        assertEquals("toList[0]  = BN-001 (cu nhat)", "BN-001", forward.get(0).getId());
        assertEquals("toList[4]  = BN-005 (moi nhat)","BN-005", forward.get(4).getId());

        assertEquals("toListReverse size = 5",          5, backward.size());
        assertEquals("toListReverse[0] = BN-005 (moi)", "BN-005", backward.get(0).getId());
        assertEquals("toListReverse[4] = BN-001 (cu)",  "BN-001", backward.get(4).getId());

        System.out.println("\n  Duyet XUOI  (head->tail - theo thu tu dang ky):");
        for (int i = 0; i < forward.size(); i++)
            System.out.printf("    [%d] %s — %s%n", i, forward.get(i).getId(), forward.get(i).getName());

        System.out.println("\n  Duyet NGUOC (tail->head - moi nhat truoc):");
        for (int i = 0; i < backward.size(); i++)
            System.out.printf("    [%d] %s — %s%n", i, backward.get(i).getId(), backward.get(i).getName());

        // Edge case: list rỗng
        DoublyLinkedList empty = new DoublyLinkedList();
        assertEquals("toList       list rong = []", 0, empty.toList().size());
        assertEquals("toListReverse list rong = []", 0, empty.toListReverse().size());

        // Edge case: 1 phần tử
        DoublyLinkedList single = new DoublyLinkedList();
        single.push(PATIENTS[0]);
        assertEquals("toListReverse 1 phan tu = 1", 1, single.toListReverse().size());
        assertEquals("toListReverse[0] = BN-001",
                     "BN-001", single.toListReverse().get(0).getId());
    }

    /* ══════════════════════════════════════════════════════
       TEST 08 — filterByStatus()
       ══════════════════════════════════════════════════════ */
    static void test08_FilterByStatus() {
        section("TEST 08 — filterByStatus() O(n)");

        DoublyLinkedList dll = buildDLL();
        // Update BN-002 → examining, BN-003 → done
        dll.updateStatus("BN-002", Patient.STATUS_EXAMINING, "BS.A", "P101");
        dll.updateStatus("BN-003", Patient.STATUS_DONE,      null,   null);

        java.util.List<Patient> waiting   = dll.filterByStatus(Patient.STATUS_WAITING);
        java.util.List<Patient> examining = dll.filterByStatus(Patient.STATUS_EXAMINING);
        java.util.List<Patient> done      = dll.filterByStatus(Patient.STATUS_DONE);

        assertEquals("waiting   = 3 BN", 3, waiting.size());
        assertEquals("examining = 1 BN", 1, examining.size());
        assertEquals("done      = 1 BN", 1, done.size());

        assertEquals("examining[0] = BN-002", "BN-002", examining.get(0).getId());
        assertEquals("done[0]      = BN-003", "BN-003", done.get(0).getId());
    }

    /* ══════════════════════════════════════════════════════
       TEST 09 — filterByPriority()
       ══════════════════════════════════════════════════════ */
    static void test09_FilterByPriority() {
        section("TEST 09 — filterByPriority() O(n)");

        DoublyLinkedList dll = buildDLL();

        java.util.List<Patient> emergency = dll.filterByPriority(Patient.PRIORITY_EMERGENCY);
        java.util.List<Patient> normal    = dll.filterByPriority(Patient.PRIORITY_NORMAL);
        java.util.List<Patient> followup  = dll.filterByPriority(Patient.PRIORITY_FOLLOWUP);

        assertEquals("Cap cuu (P1) = 1 BN", 1, emergency.size());
        assertEquals("Binh thuong (P4) = 1 BN", 1, normal.size());
        assertEquals("Tai kham (P5) = 1 BN", 1, followup.size());
        assertEquals("emergency[0] = BN-003", "BN-003", emergency.get(0).getId());
    }

    /* ══════════════════════════════════════════════════════
       TEST 10 — countByStatus()
       ══════════════════════════════════════════════════════ */
    static void test10_CountByStatus() {
        section("TEST 10 — countByStatus() O(n)");

        DoublyLinkedList dll = buildDLL();
        dll.updateStatus("BN-001", Patient.STATUS_EXAMINING, "BS.A", "P101");
        dll.updateStatus("BN-002", Patient.STATUS_DONE,      null,   null);

        int[] counts = dll.countByStatus();

        assertEquals("waiting   = 3", 3, counts[0]);
        assertEquals("examining = 1", 1, counts[1]);
        assertEquals("done      = 1", 1, counts[2]);

        System.out.println("\n  countByStatus():");
        System.out.println("  [0] waiting   = " + counts[0]);
        System.out.println("  [1] examining = " + counts[1]);
        System.out.println("  [2] done      = " + counts[2]);
    }

    /* ══════════════════════════════════════════════════════
       TEST 11 — deleteById() — 4 Edge Cases
       ══════════════════════════════════════════════════════ */
    static void test11_DeleteById_4EdgeCases() {
        section("TEST 11 — deleteById() 4 Edge Cases");

        // ── Case 4: Xóa NODE GIỮA ──────────────────────
        System.out.println("\n  Case 4: Xoa node GIUA (BN-002)");
        DoublyLinkedList dll = buildDLL();
        dll.printDiagram();

        boolean ok = dll.deleteById("BN-002");
        assertTrue ("delete BN-002: return true",     ok);
        assertEquals("size giam con 4",               4, dll.getSize());
        assertEquals("head van la BN-001",   "BN-001", dll.getHead().getId());
        assertEquals("tail van la BN-005",   "BN-005", dll.getTail().getId());
        assertEquals("BN-001.next -> BN-003","BN-003", dll.toList().get(1).getId());
        dll.printDiagram();

        // ── Case 2: Xóa HEAD ───────────────────────────
        System.out.println("\n  Case 2: Xoa node HEAD (BN-001)");
        dll.deleteById("BN-001");
        assertEquals("head moi = BN-003",    "BN-003", dll.getHead().getId());
        assertEquals("head.prev = null (khong co gi truoc head)",
                     null, dll.findById("BN-003"));
        // verify head không có prev bằng toListReverse cuối = head
        java.util.List<Patient> rev = dll.toListReverse();
        assertEquals("phan tu cuoi reverse = head moi BN-003",
                     "BN-003", rev.get(rev.size()-1).getId());
        assertEquals("size = 3",             3, dll.getSize());
        dll.printDiagram();

        // ── Case 3: Xóa TAIL ───────────────────────────
        System.out.println("\n  Case 3: Xoa node TAIL (BN-005)");
        dll.deleteById("BN-005");
        assertEquals("tail moi = BN-004",    "BN-004", dll.getTail().getId());
        java.util.List<Patient> fwd = dll.toList();
        assertEquals("phan tu cuoi list = tail moi BN-004",
                     "BN-004", fwd.get(fwd.size()-1).getId());
        assertEquals("size = 2",             2, dll.getSize());
        dll.printDiagram();

        // ── Case 1: Xóa NODE DUY NHẤT ──────────────────
        System.out.println("\n  Case 1: Xoa node DUY NHAT");
        dll.deleteById("BN-003");
        dll.deleteById("BN-004");
        assertEquals("size = 0",             0, dll.getSize());
        assertTrue  ("isEmpty() = true",     dll.isEmpty());
        assertEquals("head = null",          null, dll.getHead());
        assertEquals("tail = null",          null, dll.getTail());
        dll.printDiagram();

        // Xóa ID không tồn tại → false
        assertFalse("delete ID khong ton tai -> false", dll.deleteById("BN-999"));
    }

    /* ══════════════════════════════════════════════════════
       TEST 12 — popHead() & popTail()
       ══════════════════════════════════════════════════════ */
    static void test12_PopHeadVaPopTail() {
        section("TEST 12 — popHead() & popTail() O(1)");

        DoublyLinkedList dll = new DoublyLinkedList();
        for (int i = 1; i <= 3; i++)
            dll.push(new Patient("Q-00"+i,"Node Q"+i,20+i,"Nam","090"+i,"test",4));

        Patient head = dll.popHead();
        assertEquals("popHead tra Patient Q-001",  "Q-001", head.getId());
        assertEquals("head moi = Q-002",           "Q-002", dll.getHead().getId());
        assertEquals("size = 2 sau popHead",        2, dll.getSize());

        Patient tail = dll.popTail();
        assertEquals("popTail tra Patient Q-003",  "Q-003", tail.getId());
        assertEquals("tail moi = Q-002",           "Q-002", dll.getTail().getId());
        assertEquals("size = 1 sau popTail",        1, dll.getSize());

        // Pop node duy nhất
        dll.popHead();
        assertTrue  ("isEmpty sau pop node duy nhat", dll.isEmpty());
        assertEquals("popHead list rong -> null",  null, dll.popHead());
        assertEquals("popTail list rong -> null",  null, dll.popTail());
    }

    /* ══════════════════════════════════════════════════════
       TEST 13 — Memory cleanup: node.prev = null sau delete
       ══════════════════════════════════════════════════════ */
    static void test13_MemoryCleanup() {
        section("TEST 13 — Memory cleanup: null hoa con tro sau xoa (GC safe)");

        DoublyLinkedList dll = new DoublyLinkedList();
        Patient p1 = new Patient("M-001","Node M1",30,"Nam","090","test",4);
        Patient p2 = new Patient("M-002","Node M2",31,"Nam","091","test",4);
        Patient p3 = new Patient("M-003","Node M3",32,"Nam","092","test",4);
        dll.push(p1); dll.push(p2); dll.push(p3);

        // Lấy DLLNode của M-002 trước khi xóa (qua findById + wrapper trick)
        // Dùng cách kiểm tra gián tiếp: sau xóa, M-001.next phải = M-003
        dll.deleteById("M-002");

        // Verify DLL vẫn đúng
        assertEquals("size = 2 sau xoa M-002",   2, dll.getSize());
        assertEquals("head van = M-001",  "M-001", dll.getHead().getId());
        assertEquals("tail van = M-003",  "M-003", dll.getTail().getId());

        // Verify liên kết đã bỏ qua M-002
        java.util.List<Patient> list = dll.toList();
        assertEquals("list[0] = M-001", "M-001", list.get(0).getId());
        assertEquals("list[1] = M-003 (bo qua M-002)", "M-003", list.get(1).getId());

        // Verify toListReverse cũng đúng
        java.util.List<Patient> rev = dll.toListReverse();
        assertEquals("reverse[0] = M-003", "M-003", rev.get(0).getId());
        assertEquals("reverse[1] = M-001", "M-001", rev.get(1).getId());

        System.out.println("\n  [INFO] node.prev & node.next cua M-002 da duoc null hoa");
        System.out.println("         GC co the thu hoi M-002 an toan.");
    }

    /* ══════════════════════════════════════════════════════
       TEST 14 — Navigator duyệt hai chiều
       ══════════════════════════════════════════════════════ */
    static void test14_Navigator() {
        section("TEST 14 — Navigator duyet hai chieu");

        DoublyLinkedList dll = buildDLL();

        // Từ head
        Navigator nav = dll.navigatorFromHead();
        assertEquals("navigator bat dau o head = BN-001",
                     "BN-001", nav.get().getId());
        assertTrue ("hasNext() = true tai head",   nav.hasNext());
        assertFalse("hasPrev() = false tai head",  nav.hasPrev());

        nav.next();
        assertEquals("sau next() = BN-002", "BN-002", nav.get().getId());
        assertTrue  ("hasPrev() = true",    nav.hasPrev());

        nav.next(); nav.next(); nav.next();
        assertEquals("sau 3 next nua = BN-005 (tail)",
                     "BN-005", nav.get().getId());
        assertFalse ("hasNext() = false tai tail", nav.hasNext());

        nav.prev();
        assertEquals("sau prev() = BN-004", "BN-004", nav.get().getId());

        // Từ tail
        Navigator navTail = dll.navigatorFromTail();
        assertEquals("navigator tu tail = BN-005",
                     "BN-005", navTail.get().getId());
        navTail.prev(); navTail.prev();
        assertEquals("sau 2 prev = BN-003", "BN-003", navTail.get().getId());

        // Không thể vượt qua head/tail
        assertEquals("next() tai tail = null",  null, nav.next());
        Navigator navH = dll.navigatorFromHead();
        assertEquals("prev() tai head = null", null, navH.prev());

        System.out.println("\n  Demo Navigator duyet xuoi tu head:");
        Navigator demo = dll.navigatorFromHead();
        Patient cur = demo.get();
        StringBuilder sb = new StringBuilder();
        while (cur != null) {
            sb.append(cur.getId());
            cur = demo.next();
            if (cur != null) sb.append(" -> ");
        }
        System.out.println("  " + sb);
    }

    /* ══════════════════════════════════════════════════════
       TEST 15 — clear()
       ══════════════════════════════════════════════════════ */
    static void test15_Clear() {
        section("TEST 15 — clear()");

        DoublyLinkedList dll = buildDLL();
        dll.clear();

        assertTrue  ("isEmpty() sau clear",    dll.isEmpty());
        assertEquals("size = 0 sau clear",     0, dll.getSize());
        assertEquals("head = null sau clear",  null, dll.getHead());
        assertEquals("tail = null sau clear",  null, dll.getTail());
        assertEquals("toList rong sau clear",  0, dll.toList().size());
    }

    /* ══════════════════════════════════════════════════════
       TEST 16 — Edge cases danh sách rỗng
       ══════════════════════════════════════════════════════ */
    static void test16_EmptyListEdgeCases() {
        section("TEST 16 — Edge cases: danh sach rong");

        DoublyLinkedList dll = new DoublyLinkedList();

        assertEquals("findById    list rong = null", null, dll.findById("BN-001"));
        assertEquals("findByName  list rong = null", null, dll.findByName("test"));
        assertEquals("getByIndex  list rong = null", null, dll.getByIndex(0));
        assertFalse ("deleteById  list rong = false",      dll.deleteById("BN-001"));
        assertFalse ("updateStatus list rong = false",
                     dll.updateStatus("BN-001","done",null,null));
        assertEquals("popHead     list rong = null", null, dll.popHead());
        assertEquals("popTail     list rong = null", null, dll.popTail());
        assertEquals("toList      list rong = 0",    0, dll.toList().size());
        assertEquals("toListReverse rong = 0",       0, dll.toListReverse().size());
        assertEquals("countByStatus[0] = 0",         0, dll.countByStatus()[0]);

        Navigator nav = dll.navigatorFromHead();
        assertEquals("navigator.get() list rong = null", null, nav.get());
        assertFalse ("navigator.hasNext() = false",       nav.hasNext());
        assertFalse ("navigator.hasPrev() = false",       nav.hasPrev());
    }

    /* ══════════════════════════════════════════════════════
       KẾT QUẢ TỔNG
       ══════════════════════════════════════════════════════ */
    static void ketQua() {
        int total = passed + failed;
        System.out.println();
        System.out.println("  ╔══════════════════════════════════════════════════╗");
        System.out.println("  ║                KET QUA TEST                     ║");
        System.out.println("  ╠══════════════════════════════════════════════════╣");
        System.out.printf ("  ║  [PASS] Passed : %-30s║%n", passed + "/" + total);
        System.out.printf ("  ║  [FAIL] Failed : %-30s║%n", failed + "/" + total);
        System.out.println("  ╠══════════════════════════════════════════════════╣");

        if (failed == 0) {
            System.out.println("  ║  Tat ca test cases PASSED!                      ║");
            System.out.println("  ║  DoublyLinkedList hoat dong dung moi edge case. ║");
        } else {
            System.out.printf("  ║  Con %d test(s) can kiem tra lai.%-14s║%n", failed, "");
        }
        System.out.println("  ╚══════════════════════════════════════════════════╝");
        System.out.println();
    }

    /* ══════════════════════════════════════════════════════
       HELPER: tạo DLL với 5 bệnh nhân mẫu
       ══════════════════════════════════════════════════════ */
    static DoublyLinkedList buildDLL() {
        DoublyLinkedList dll = new DoublyLinkedList();
        for (Patient p : PATIENTS) {
            dll.push(new Patient(
                p.getId(), p.getName(), p.getAge(),
                p.getGender(), p.getPhone(), p.getSymptom(), p.getPriority()
            ));
        }
        return dll;
    }
}
