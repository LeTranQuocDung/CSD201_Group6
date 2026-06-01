```mermaid
classDiagram
    direction LR
    
    class DoctorNode {
        +String doctorName
        +DoctorNode next
        +DoctorNode(String doctorName)
    }

    class CircularLinkedList {
        -DoctorNode head
        -DoctorNode tail
        -DoctorNode current
        -int size
        +CircularLinkedList()
        +addDoctor(String name) void
        +nextDoctor() String
        +getSize() int
        +main(String[] args) static void
    }

 ```