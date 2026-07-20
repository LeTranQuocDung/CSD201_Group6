package triage;

/**
 * Represents a hospital patient.
 * priority : 1 = most urgent, 10 = least urgent.
 * level    : tier for Multi-Level PQ routing
 *            1 = Urgent (1-3), 2 = Semi-Urgent (4-6), 3 = Non-Urgent (7-10)
 *
 * When priority drift occurs, the patient's actual priority changes
 * but their assigned tier (level) remains the same — simulating
 * a real scenario where the PQ cannot automatically re-classify patients.
 */
public class Patient {

    private final int    id;
    private final String name;
    private final int    priority;   // 1-10, actual priority (may be drifted)
    private final int    level;      // 1 / 2 / 3 - tier for PQ routing

    /** Normal constructor: level auto-classified from priority. */
    public Patient(int id, String name, int priority) {
        this.id       = id;
        this.name     = name;
        this.priority = priority;
        this.level    = classifyLevel(priority);
    }

    /**
     * Drift constructor: priority has changed but patient stays in original tier.
     * Used to simulate condition worsening after initial triage assignment.
     * The Multi-Level PQ routes by level (original tier), so this patient
     * will be placed in the WRONG tier relative to their actual priority.
     *
     * @param id            patient ID
     * @param name          patient name
     * @param priority      actual (drifted) priority
     * @param originalLevel original tier assignment (1/2/3)
     */
    public Patient(int id, String name, int priority, int originalLevel) {
        this.id       = id;
        this.name     = name;
        this.priority = priority;
        this.level    = originalLevel;  // keep original tier, DON'T re-classify
    }

    /** Map priority 1-10 to level 1/2/3. Public for external use. */
    public static int classifyLevel(int p) {
        if (p <= 3) return 1;   // Urgent
        if (p <= 6) return 2;   // Semi-Urgent
        return 3;               // Non-Urgent
    }

    public int    getId()       { return id; }
    public String getName()     { return name; }
    public int    getPriority() { return priority; }
    public int    getLevel()    { return level; }

    /** Get level name based on the tier this patient is assigned to. */
    public String getLevelName() {
        return levelToName(level);
    }

    /** Get level name based on actual priority (may differ after drift). */
    public String getActualLevelName() {
        return levelToName(classifyLevel(priority));
    }

    /** Convert level number to human-readable name. */
    public static String levelToName(int lvl) {
        switch (lvl) {
            case 1:  return "Urgent";
            case 2:  return "Semi-Urgent";
            default: return "Non-Urgent";
        }
    }

    /** Returns true if patient's actual priority doesn't match their assigned tier. */
    public boolean isDrifted() {
        return classifyLevel(priority) != level;
    }

    @Override
    public String toString() {
        if (isDrifted()) {
            return String.format("Patient{id=%d, p=%d, %s, stuck in %s tier}",
                    id, priority, getActualLevelName(), getLevelName());
        }
        return String.format("Patient{id=%d, p=%d, %s}",
                id, priority, getLevelName());
    }
}
