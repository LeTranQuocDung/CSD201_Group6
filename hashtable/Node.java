// Lớp đại diện cho một phần tử trong Hash Table
public class Node {
    String key;   
    String value; 
    Node next;    

    public Node(String key, String value) {
        this.key = key;
        this.value = value;
        this.next = null;
    }
}