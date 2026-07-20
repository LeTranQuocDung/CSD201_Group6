package triage;

/**
 * Đại diện một bệnh nhân trong hệ thống triage.
 * priority: 1 = khẩn cấp nhất, số càng lớn càng ít khẩn.
 */
public class Patient implements Comparable<Patient> {

    private final int id;
    private final String name;
    private int priority;

    public Patient(int id, String name, int priority) {
        this.id = id;
        this.name = name;
        this.priority = priority;
    }

    public int getId()           { return id; }
    public String getName()      { return name; }
    public int getPriority()     { return priority; }
    public void setPriority(int p) { this.priority = p; }

    @Override
    public int compareTo(Patient other) {
        return Integer.compare(this.priority, other.priority);
    }

    @Override
    public String toString() {
        return "Patient{id=" + id + ", name='" + name + "', priority=" + priority + "}";
    }
}
