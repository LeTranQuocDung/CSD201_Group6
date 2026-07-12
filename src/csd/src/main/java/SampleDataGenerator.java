import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Random;

/**
 * SampleDataGenerator.java
 * Class sinh 1 triệu bệnh nhân ngẫu nhiên nhằm kiểm tra hiệu năng hệ thống.
 * Tự động tắt dòng in nhật ký log của Min-Heap trong lúc sinh dữ liệu để tối ưu tốc độ.
 */
public class SampleDataGenerator {

    private static final String[] HO = {"Nguyen", "Tran", "Le", "Pham", "Hoang", "Phan", "Vu", "Vo", "Dang", "Bui", "Do", "Ho", "Ngo", "Duong", "Ly"};
    private static final String[] DEM = {"Van", "Thi", "Minh", "Thu", "Hong", "Quoc", "Xuan", "Duc", "Ngoc", "Thanh", "Hai", "Tuan", "Anh"};
    private static final String[] TEN = {"Anh", "Binh", "Cuong", "Dung", "Giang", "Hung", "Huong", "Khanh", "Lan", "Linh", "Nam", "Oanh", "Phong", "Phuc", "Quynh", "Son", "Thao", "Trang", "Tuan", "Van", "Viet", "Vy", "Yen"};

    private static final String[] GENDERS = {"Nam", "Nu"};
    private static final String[] SYMPTOMS = {
        "Dau dau", "Sot cao", "Kho tho", "Dau nguc", "Dau bung", 
        "Chan thuong nhe", "Man ngua", "Ho khan", "Met moi", 
        "Mat ngu", "Chong mat", "Dau lung", "Sot co giat", "Suy ho hap"
    };

    /**
     * Sinh tự động số lượng bệnh nhân theo yêu cầu và đăng ký vào HospitalService.
     */
    public static void generate(HospitalService service, int count) {
        PrintStream originalOut = System.out;
        
        // Vô hiệu hóa System.out tạm thời để tránh in 1 triệu dòng log "[THEM HA]"
        // Việc này giúp quá trình chèn dữ liệu nhanh hơn gấp hàng trăm lần
        System.setOut(new PrintStream(new OutputStream() {
            @Override
            public void write(int b) {
                // Không làm gì
            }
        }));

        Random rand = new Random();
        long startTime = System.currentTimeMillis();

        try {
            for (int i = 1; i <= count; i++) {
                String name = HO[rand.nextInt(HO.length)] + " " + DEM[rand.nextInt(DEM.length)] + " " + TEN[rand.nextInt(TEN.length)];
                int age = 1 + rand.nextInt(90);
                String gender = GENDERS[rand.nextInt(GENDERS.length)];
                String phone = String.format("09%08d", rand.nextInt(100000000));
                String symptom = SYMPTOMS[rand.nextInt(SYMPTOMS.length)];
                int priority = 1 + rand.nextInt(4); // Ưu tiên từ 1 (Nguy kịch) đến 4 (Nhẹ)

                service.register("auto", name, age, gender, phone, symptom, priority);

                if (i % 100000 == 0) {
                    originalOut.printf("Da sinh va dang ky: %,d / %,d benh nhan (%,d%%)...\n", i, count, (i * 100) / count);
                }
            }
        } catch (Exception e) {
            originalOut.println("[LOI] Xay ra loi trong qua trinh sinh du lieu: " + e.getMessage());
            e.printStackTrace(originalOut);
        } finally {
            // Khôi phục lại luồng đầu ra tiêu chuẩn
            System.setOut(originalOut);
        }

        long duration = System.currentTimeMillis() - startTime;
        System.out.printf("[SUCCESS] Da khoi tao thanh cong %,d benh nhan mau trong %,d ms (khoang %.2f giay)!\n", 
                count, duration, duration / 1000.0);
    }
}
