import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Random;

public class HashTableTest {
    public static void main(String[] args) {
        String csvFile = "c:\\Users\\PC\\Documents\\GitHub\\CSD201_Group6\\data\\mock_data.csv";

        HashTable hashTable = new HashTable(1000000);
        TriageMinHeap minHeap = new TriageMinHeap(1000000);

        System.out.println("=== BAT DAU KIEM TRA HIEU SUAT HASH TABLE VS MIN HEAP ===");
        System.out.println("Dang doc file: " + csvFile);

        PrintStream originalOut = System.out;
        PrintStream dummyOut = new PrintStream(new OutputStream() {
            @Override
            public void write(int b) {
            }
        });

        // ------------------------- 1. CREATE (Insert 1,000,000 records)
        // -------------------------
        long startHashInsert, endHashInsert;
        long startHeapInsert, endHeapInsert;
        long hashCreateMax = 0, heapCreateMax = 0;
        long totalHashInsertTime = 0;
        long totalHeapInsertTime = 0;
        int count = 0;

        System.out.println("\n[1/4] Dang Insert 1.000.000 records vao ca HashTable va MinHeap (Vui long doi)...");
        System.setOut(dummyOut); // Ẩn log console
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length >= 4) {
                    String id = "BN-" + values[0];
                    String name = values[1];
                    int age = Integer.parseInt(values[2]);
                    int priority = Integer.parseInt(values[3]);

                    Patient p = new Patient(id, name, age, "Unknown", "0000000000", "None", priority);
                    PatientNode pn = new PatientNode(p);

                    // Do HashTable
                    startHashInsert = System.nanoTime();
                    hashTable.put(id, p);
                    endHashInsert = System.nanoTime();
                    long tHashC = endHashInsert - startHashInsert;
                    if (tHashC > hashCreateMax)
                        hashCreateMax = tHashC;
                    totalHashInsertTime += tHashC;

                    // Do MinHeap
                    startHeapInsert = System.nanoTime();
                    minHeap.push(pn);
                    endHeapInsert = System.nanoTime();
                    long tHeapC = endHeapInsert - startHeapInsert;
                    if (tHeapC > heapCreateMax)
                        heapCreateMax = tHeapC;
                    totalHeapInsertTime += tHeapC;

                    count++;
                }
            }
        } catch (IOException e) {
            System.setOut(originalOut);
            System.err.println("Loi doc file: " + e.getMessage());
            return;
        }
        System.setOut(originalOut);
        System.out.println("      -> Xong! Da them " + count + " ban ghi.");

        // Chuẩn bị 1000 IDs random để test READ, UPDATE, DELETE
        Random rand = new Random();
        int testCount = 1000;
        String[] testIDs = new String[testCount];
        for (int i = 0; i < testCount; i++) {
            testIDs[i] = "BN-" + (rand.nextInt(1000000) + 1);
        }

        // ------------------------- 2. READ (Search 1000 records)
        // -------------------------
        System.out.println("[2/4] Dang test READ (Search) 1000 records...");
        long hashReadMax = 0, hashReadTotal = 0;
        long heapReadMax = 0, heapReadTotal = 0;

        for (String id : testIDs) {
            long sHash = System.nanoTime();
            hashTable.get(id);
            long eHash = System.nanoTime();
            long tHash = eHash - sHash;
            if (tHash > hashReadMax)
                hashReadMax = tHash;
            hashReadTotal += tHash;

            long sHeap = System.nanoTime();
            minHeap.getPatientNode(id);
            long eHeap = System.nanoTime();
            long tHeap = eHeap - sHeap;
            if (tHeap > heapReadMax)
                heapReadMax = tHeap;
            heapReadTotal += tHeap;
        }

        // ------------------------- 3. UPDATE (Update 1000 records)
        // -------------------------
        System.out.println("[3/4] Dang test UPDATE priority 1000 records...");
        long hashUpdateMax = 0, hashUpdateTotal = 0;
        long heapUpdateMax = 0, heapUpdateTotal = 0;

        System.setOut(dummyOut); // Ẩn log
        for (String id : testIDs) {
            int newPriority = rand.nextInt(4) + 1;

            long sHash = System.nanoTime();
            Patient p = hashTable.get(id);
            if (p != null) {
                p.setPriority(newPriority);
                hashTable.put(id, p);
            }
            long eHash = System.nanoTime();
            long tHash = eHash - sHash;
            if (tHash > hashUpdateMax)
                hashUpdateMax = tHash;
            hashUpdateTotal += tHash;

            long sHeap = System.nanoTime();
            try {
                minHeap.updatePriority(id, newPriority);
            } catch (Exception e) {
            }
            long eHeap = System.nanoTime();
            long tHeap = eHeap - sHeap;
            if (tHeap > heapUpdateMax)
                heapUpdateMax = tHeap;
            heapUpdateTotal += tHeap;
        }
        System.setOut(originalOut);

        // ------------------------- 4. DELETE (Delete 1000 records)
        // -------------------------
        System.out.println("[4/4] Dang test DELETE 1000 records...");
        long hashDeleteMax = 0, hashDeleteTotal = 0;
        long heapDeleteMax = 0, heapDeleteTotal = 0;

        System.setOut(dummyOut); // Ẩn log
        for (String id : testIDs) {
            long sHash = System.nanoTime();
            hashTable.remove(id);
            long eHash = System.nanoTime();
            long tHash = eHash - sHash;
            if (tHash > hashDeleteMax)
                hashDeleteMax = tHash;
            hashDeleteTotal += tHash;

            long sHeap = System.nanoTime();
            try {
                minHeap.remove(id);
            } catch (Exception e) {
            }
            long eHeap = System.nanoTime();
            long tHeap = eHeap - sHeap;
            if (tHeap > heapDeleteMax)
                heapDeleteMax = tHeap;
            heapDeleteTotal += tHeap;
        }
        System.setOut(originalOut);

        // ------------------------- KẾT QUẢ SO SÁNH -------------------------
        long hashCreateAvg = totalHashInsertTime / count;
        long heapCreateAvg = totalHeapInsertTime / count;

        long hashReadAvg = hashReadTotal / testCount;
        long heapReadAvg = heapReadTotal / testCount;

        long hashUpdateAvg = hashUpdateTotal / testCount;
        long heapUpdateAvg = heapUpdateTotal / testCount;

        long hashDeleteAvg = hashDeleteTotal / testCount;
        long heapDeleteAvg = heapDeleteTotal / testCount;

        System.out.println("\n====================== BANG SO SANH KET QUA (C-R-U-D) ======================");
        System.out.println("Ket qua do bang don vi Nanoseconds (ns).");
        System.out.println("+-----------+---------------------------------+---------------------------------+");
        System.out.println("| Thao tac  |          Hash Table             |         Triage Min Heap         |");
        System.out.println("|           |   Trung binh(ns)   |    Max (ns)    |   Trung binh(ns)   |    Max (ns)    |");
        System.out.println("+-----------+---------------------------------+---------------------------------+");
        System.out.printf("| CREATE    | %-14d | %-14d | %-14d | %-14d |\n", hashCreateAvg, hashCreateMax,
                heapCreateAvg, heapCreateMax);
        System.out.printf("| READ      | %-14d | %-14d | %-14d | %-14d |\n", hashReadAvg, hashReadMax, heapReadAvg,
                heapReadMax);
        System.out.printf("| UPDATE    | %-14d | %-14d | %-14d | %-14d |\n", hashUpdateAvg, hashUpdateMax,
                heapUpdateAvg, heapUpdateMax);
        System.out.printf("| DELETE    | %-14d | %-14d | %-14d | %-14d |\n", hashDeleteAvg, hashDeleteMax,
                heapDeleteAvg, heapDeleteMax);
        System.out.println("+-----------+---------------------------------+---------------------------------+");

        // ------------------------- 5. DEMO TỐC ĐỘ THẬT TẠI CÁC VỊ TRÍ CỤ THỂ
        // -------------------------
        System.out.println("\n================ TOC DO THAT TAI CAC ID NGAU NHIEN ====================");
        System.out.println("Chon ngau nhien 5 ID de kiem tra toc do READ truc tiep:");
        for (int i = 0; i < 5; i++) {
            String demoId = "BN-" + (rand.nextInt(1000000) + 1);
            System.out.println("\n >> Thu nghiem voi ID: " + demoId);

            // Đo HashTable
            long t1 = System.nanoTime();
            Patient pHash = hashTable.get(demoId);
            long t2 = System.nanoTime();

            // Đo MinHeap
            long t3 = System.nanoTime();
            PatientNode pHeap = minHeap.getPatientNode(demoId);
            long t4 = System.nanoTime();

            System.out.printf("    [HashTable] Thoi gian tim: %-8d ns (Ket qua: %s)\n", (t2 - t1),
                    (pHash != null ? "Co" : "Khong"));
            System.out.printf("    [MinHeap]   Thoi gian tim: %-8d ns (Ket qua: %s)\n", (t4 - t3),
                    (pHeap != null ? "Co" : "Khong"));

            if ((t4 - t3) > 0 && (t2 - t1) > 0) {
                System.out.printf("    => HashTable nhanh hon MinHeap khoang %,d lan.\n", ((t4 - t3) / (t2 - t1)));
            }
        }
        System.out.println("============================================================================");

    }
}
