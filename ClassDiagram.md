```mermaid
classDiagram
direction TB


class TriageMinHeap {
    - heap: PatientNode[]
    - size: int
    - capacity: int


    + TriageMinHeap(capacity: int )
    + swap(i: int, j: int ) void
    - heapifyUp(index: int ) void
    - heapifyDown(index: int ) void
    - findIndex(patientID: String ) int
    + push(node: PatientNode ) void
    + pop() PatientNode
    + updatePriority(patientID: String,  newScore: int) void
    + remove(patientID: String) boolean
    + printHeap() void
    + isEmpty() boolean
    + getSize() int
}


class PatientNode {
    - data: Patient
    - patientID: String
    - priorityScore: int
    - timestamp: long

    + PatientNode(data: Patient)
    + getPatientID() String
    + getPriorityScore() int
    + setPriorityScore(priority: int) void
    + getTimestamp() long
    + hasHigherUrgencyThan(other: PatientNode) boolean
    + toString() String
}


class TriageSimulation{
    + main(args: String[] )
    + banner(title: String )
}

