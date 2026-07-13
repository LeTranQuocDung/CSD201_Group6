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

```mermaid
flowchart TD
    A([Start]) --> D[/Call HospitalService.printDiagram/]

    D --> F{Is the history list empty?}

    F -- Yes --> G[/Display empty list message/]
    F -- No --> H[/Display patient nodes with two-way links/]

    G --> I[/Call HospitalService.printTable/]
    H --> I

    I --> J[/Display medical history table/]
    J --> K{Are there more patient records?}

    K -- Yes --> L[/Display patient information in the table/]
    L --> K

    K -- No --> M[/Display total number of patients/]
    M --> N([End])
```
