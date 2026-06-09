``` mermaid
graph TD
    %% Buoc 1: Dang ky va luu tru tuan tu
    Start([Start]) --> Register[1. Register & Triage]
    Register --> CreatePatient[Create Patient Object & Assign Priority 1-4]
    CreatePatient --> AddCache[Save to HashTable 'patientCache']
    
    %% Chuyen thang tu HashTable sang Queue thay vi tach nhanh
    AddCache --> PushMinHeap[Insert into TriageMinHeap 'triageQueue']

    %% Buoc 2: Kiem tra hang doi
    PushMinHeap --> CheckQueue{Is 'triageQueue' Empty?}
    
    CheckQueue -- Yes: Wait for New Patients --> Register
    
    CheckQueue -- No --> ExtractMin[Extract Patient with Lowest Priority Score]

    %% Buoc 3: Chi dinh bac si va kham benh
    ExtractMin --> GetDoctor[Get Next Doctor from CircularLinkedList]
    GetDoctor --> UpdateStatus[Update Status to EXAMINING & Assign Room]

    %% Buoc 4: Kiem tra hoan thanh
    UpdateStatus --> Complete{Is Examination Completed?}
    
    Complete -- Yes --> MoveToHistory[Set Status to DONE & Move to DoublyLinkedList]
    MoveToHistory --> End([End])
    
    %% Quay lai kiem tra hang doi neu can phan loai lai
    Complete -- No: Need Triage Update --> UpdatePriority[Update Priority Score in Heap]
    UpdatePriority --> CheckQueue
