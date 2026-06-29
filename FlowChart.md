# Smart Hospital Patient Triage System - Flowchart

## 1. Flowchart tong quan he thong

```mermaid
flowchart TD
    Start([Start])
    OpenSystem[/Open Smart Hospital Triage System/]
    MainMenu{Main Menu}

    Start --> OpenSystem --> MainMenu

    MainMenu -->|Register Patient| RegisterPatient[Register Patient]
    MainMenu -->|Conduct Initial Assessment| AssessPriority[/Input priority 1-4/]
    MainMenu -->|Call or Allocate Patient| CallPatient[Call Patient]
    MainMenu -->|Override Priority| OverridePriority[Override Priority]
    MainMenu -->|Order Diagnostic Tests| OrderTests[Order Diagnostic Tests]
    MainMenu -->|Monitor Priority Queue| MonitorQueue[Monitor Priority Queue]
    MainMenu -->|Search Patient| SearchPatient[Search Patient]
    MainMenu -->|View History| ViewHistory[View History]
    MainMenu -->|Delete Patient| DeletePatient[Delete Patient]
    MainMenu -->|Exit| End([End])

    AssessPriority --> ValidPriority{Priority valid?}
    ValidPriority -->|No| PriorityError[/Show invalid priority message/]
    PriorityError --> MainMenu
    ValidPriority -->|Yes| RegisterPatient

    RegisterPatient --> Registered[/Patient registered and waiting/]
    Registered --> MainMenu

    CallPatient --> Examining[/Patient assigned to doctor and room/]
    Examining --> MainMenu

    OverridePriority --> PriorityUpdated[/Priority queue updated/]
    PriorityUpdated --> MainMenu

    OrderTests --> RecordUpdated[/Medical information updated/]
    RecordUpdated --> MainMenu

    MonitorQueue --> QueueShown[/Display waiting patients by priority/]
    QueueShown --> MainMenu

    SearchPatient --> SearchShown[/Display patient or not found message/]
    SearchShown --> MainMenu

    ViewHistory --> HistoryShown[/Display history list or current record/]
    HistoryShown --> MainMenu

    DeletePatient --> DeleteResult[/Display delete result/]
    DeleteResult --> MainMenu
```

## 2. Flowchart Register Patient

```mermaid
flowchart TD
    Start([Start Register Patient])
    InputPatient[/Input patient information/]
    ValidateInfo{Information valid?}

    Start --> InputPatient --> ValidateInfo
    ValidateInfo -->|No| ShowError[/Show validation error/]
    ShowError --> InputPatient

    ValidateInfo -->|Yes| RegisterPatient[Register Patient]
    RegisterPatient --> StoreHistory[Store in History List]
    StoreHistory --> UpdateLookup[Update Patient Lookup]
    UpdateLookup --> InsertQueue[Insert into Priority Queue]
    InsertQueue --> Success[/Show generated patient ID/]
    Success --> End([End Register Patient])
```

Register flow chay tuan tu: `Register Patient` dai dien cho nghiep vu dang ky benh nhan. Ben trong implementation hien tai, luong nay duoc map voi `HospitalService.register`, `DoublyLinkedList.push`, `HashTable.put` va `TriageMinHeap.push`.

## 3. Flowchart Call Patient / Allocate Bed

```mermaid
flowchart TD
    Start([Start Call Patient])
    CheckQueue{Priority queue empty?}

    Start --> RequestCall --> CheckQueue
    CheckQueue -->|Yes| NoPatient[/Show no waiting patient message/]
    NoPatient --> ReturnMenu([Return Main Menu])

    CheckQueue -->|No| PopPatient[Get highest-priority patient]
    PopPatient --> GetDoctor[Get next available doctor]
    GetDoctor --> DoctorAvailable{Doctor available?}

    DoctorAvailable -->|No| WaitDoctor[/Show no doctor available message/]
    WaitDoctor --> ReturnMenu

    DoctorAvailable -->|Yes| AssignDoctor[Assign doctor and room]
    AssignDoctor --> UpdateStatus[Update patient status]
    UpdateStatus --> NotifyPatient[/Display called patient information/]
    NotifyPatient --> ReturnMenu
```

Flow nay gom use case `Allocate Bed by Priority` va `Call Patient`, vi ca hai deu lay benh nhan uu tien cao nhat tu priority queue, sau do phan cong bac si theo vong lap. Implementation tuong ung: `TriageMinHeap.pop`, `CircularLinkedList.nextDoctor` hoac `HospitalService.nextDoctor`, va `DoublyLinkedList.updateStatus`.

## 4. Flowchart Override Priority

```mermaid
flowchart TD
    Start([Start Override Priority])
    InputPriority[/Input patient ID and new priority/]
    ValidatePriority{Priority in range 1-4?}

    Start --> InputPriority --> ValidatePriority
    ValidatePriority -->|No| ShowInvalid[/Show invalid priority message/]
    ShowInvalid --> InputPriority

    ValidatePriority -->|Yes| FindPatient[Find patient in priority queue]
    FindPatient --> Found{Patient found?}
    Found -->|No| NotFound[/Show patient not found message/]
    NotFound --> ReturnMenu([Return Main Menu])

    Found -->|Yes| UpdatePriority[Update priority value]
    UpdatePriority --> ReorderQueue[Reorder priority queue]
    ReorderQueue --> Updated[/Show priority updated message/]
    Updated --> ReturnMenu
```

Trong flow he thong, `Reorder priority queue` dai dien cho viec heap tu can bang lai. Chi tiet `heapifyUp` hay `heapifyDown` thuoc flow thuat toan cua `TriageMinHeap`, khong dat trong flow tong quan.

## 5. Flowchart Order Diagnostic Tests

```mermaid
flowchart TD
    Start([Start Order Diagnostic Tests])
    DoctorInput[/Emergency Physician inputs diagnostic request/]
    FindPatient[Find patient by ID]
    Found{Patient found?}

    Start --> DoctorInput --> FindPatient --> Found
    Found -->|No| NotFound[/Show patient not found message/]
    NotFound --> ReturnMenu([Return Main Menu])

    Found -->|Yes| AddTest[Add diagnosis, test, prescription, or note]
    AddTest --> UpdateRecord[Update patient record]
    UpdateRecord --> ShowResult[/Show record updated message/]
    ShowResult --> ReturnMenu
```

Use case nay dung mot flow rieng, tranh lap lai `Order Tests` o nhieu vi tri trong flow tong quan.

## 6. Flowchart Search Patient

```mermaid
flowchart TD
    Start([Start Search Patient])
    InputSearch[/Input patient ID/]
    SearchLookup[Search in Patient Lookup]
    Found{Patient found?}

    Start --> InputSearch --> SearchLookup --> Found
    Found -->|Yes| ShowPatient[/Display patient information/]
    ShowPatient --> ReturnMenu([Return Main Menu])
    Found -->|No| ShowNotFound[/Show not found message/]
    ShowNotFound --> ReturnMenu
```

Search flow xem `HashTable` la lookup chinh cua benh nhan theo ID. `DoublyLinkedList` van dung cho View History, filter, navigate ho so, hoac kiem tra lich su khi can doi chieu du lieu.

## 7. Flowchart View History

```mermaid
flowchart TD
    Start([Start View History])
    SelectMode{View mode?}

    Start --> SelectMode
    SelectMode -->|Newest first| ReverseList[Load newest history list]
    SelectMode -->|By status| FilterStatus[Filter history by status]
    SelectMode -->|By priority| FilterPriority[Filter history by priority]
    SelectMode -->|Navigate record| CreateNavigator[Create Navigator]

    ReverseList --> DisplayList[/Display history list/]
    FilterStatus --> DisplayList
    FilterPriority --> DisplayList

    CreateNavigator --> ShowCurrent[/Display current record/]
    ShowCurrent --> MoveChoice{Next, previous, or stop?}
    MoveChoice -->|Next| NavNext[Move to next record]
    MoveChoice -->|Previous| NavPrev[Move to previous record]
    MoveChoice -->|Stop| ReturnMenu([Return Main Menu])
    NavNext --> HasRecord{Record exists?}
    NavPrev --> HasRecord
    HasRecord -->|Yes| ShowCurrent
    HasRecord -->|No| EndList[/Show end of list message/]
    EndList --> ReturnMenu

    DisplayList --> ReturnMenu
```

Implementation tuong ung: `DoublyLinkedList.toListReverse`, `filterByStatus`, `filterByPriority`, `navigatorFromHead`, `navigatorFromTail`, `Navigator.next` va `Navigator.prev`.

## 8. Flowchart Delete Patient

```mermaid
flowchart TD
    Start([Start Delete Patient])
    InputDelete[/Input patient ID/]
    FindPatient[Find patient]
    Found{Patient found?}

    Start --> InputDelete --> FindPatient --> Found
    Found -->|No| NotFound[/Show patient not found message/]
    NotFound --> ReturnMenu([Return Main Menu])

    Found -->|Yes| Confirm{Confirm delete?}
    Confirm -->|No| Cancel[/Cancel delete/]
    Cancel --> ReturnMenu

    Confirm -->|Yes| RemoveLookup[Delete from Patient Lookup]
    RemoveLookup --> RemoveHistory[Delete from History List]
    RemoveHistory --> RemoveQueue[Remove from Priority Queue]
    RemoveQueue --> Success[/Show delete success message/]
    Success --> ReturnMenu
```

Delete flow chay tuan tu: tim benh nhan truoc, neu tim thay va duoc xac nhan thi moi xoa khoi lookup, history list va priority queue. Cach nay tranh hieu nham rang cac cau truc du lieu xoa song song hoac van xoa khi benh nhan khong ton tai.

