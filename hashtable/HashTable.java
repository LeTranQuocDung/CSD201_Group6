import java.util.Scanner;

public class HashTable {

    private static class Node {
        String key;
        String value;
        Node next;

        public Node(String key, String value) {
            this.key = key;
            this.value = value;
            this.next = null;
        }
    }

    private Node[] table;
    private int capacity;

    public HashTable(int capacity) {
        this.capacity = capacity;
        this.table = new Node[capacity];
    }

    private int hash(String key) {
        return Math.abs(key.hashCode()) % capacity;
    }

    public void put(String key, String value) {
        int index = hash(key);
        Node newNode = new Node(key, value);

        if (table[index] == null) {
            table[index] = newNode;
            System.out.printf("[LOG] Inserted into empty bucket: Key='%s', Value='%s' (index: %d)\n", key, value,
                    index);
        } else {

            Node current = table[index];

            while (true) {

                if (current.key.equals(key)) {
                    current.value = value;
                    System.out.printf("[LOG] Updated successfully (Duplicate Key): Key='%s', New Value='%s'\n", key,
                            value);
                    return;
                }

                if (current.next == null) {
                    break;
                }

                current = current.next;
            }

            current.next = newNode;
            System.out.printf("[LOG] Chained successfully (Collision): Key='%s', Value='%s' (index: %d)\n", key, value,
                    index);
        }
    }

    public String get(String key) {
        int index = hash(key);
        Node current = table[index];

        while (current != null) {
            if (current.key.equals(key)) {
                System.out.printf("[LOG] Retrieved successfully: Key='%s' -> Value='%s'\n", key, current.value);
                return current.value;
            }
            current = current.next;
        }

        System.out.printf("[LOG] Not found: Key='%s'\n", key);
        return null;
    }

    // ================================================================
    // MAIN METHOD: Interactive Console Menu
    // ================================================================
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== HASH TABLE INITIALIZATION ===");
        System.out.print("Enter the capacity for the hash table: ");
        int capacity = 5; // Default value
        try {
            capacity = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input format, defaulting to capacity 5.");
        }

        HashTable ht = new HashTable(capacity);
        boolean isRunning = true;

        while (isRunning) {
            System.out.println("\n================= MENU =================");
            System.out.println("1. Insert or Update data (put)");
            System.out.println("2. Search data (get)");
            System.out.println("0. Exit program");
            System.out.print("Select an option (0-2): ");

            String choiceStr = scanner.nextLine();
            int choice = -1;

            try {
                choice = Integer.parseInt(choiceStr);
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid integer!");
                continue;
            }

            switch (choice) {
                case 1:
                    System.out.print("Enter Key (ID): ");
                    String key = scanner.nextLine();

                    // Remove leading/trailing spaces
                    if (key.trim().isEmpty()) {
                        System.out.println("Error: Key cannot be empty!");
                        break;
                    }

                    System.out.print("Enter Value (Data): ");
                    String value = scanner.nextLine();

                    ht.put(key, value);
                    break;

                case 2:
                    System.out.print("Enter Key to search: ");
                    String searchKey = scanner.nextLine();
                    ht.get(searchKey);
                    break;

                case 0:
                    isRunning = false;
                    System.out.println("Exiting Hash Table program. Goodbye!");
                    break;

                default:
                    System.out.println("Invalid selection. Please choose 0, 1, or 2.");
            }
        }

        scanner.close();
    }
}