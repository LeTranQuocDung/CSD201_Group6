package triage;

import java.util.PriorityQueue;
import java.util.Comparator;

/**
 * Multi-Level Priority Queue (3 tiers).
 *
 * Tier 1 - Urgent     : priority 1-3  -> processed FIRST
 * Tier 2 - Semi-Urgent: priority 4-6  -> processed only when Tier 1 is empty
 * Tier 3 - Non-Urgent : priority 7-10 -> processed only when Tier 1+2 are empty
 *
 * Within each tier, patients are ordered by their exact priority value.
 *
 * Key characteristic vs Single-Level PQ:
 *   Inside a tier, patients with THE SAME priority enter in FIFO order.
 *   A patient in Tier 1 with priority 3 will be served BEFORE
 *   a Tier 2 patient with priority 4, even if priority 4 arrived first.
 *
 * This is realistic: a doctor finishes ALL urgent cases before semi-urgent.
 */
public class MultiLevelPQ {

    private final PriorityQueue<Patient> urgent;       // p 1-3
    private final PriorityQueue<Patient> semiUrgent;   // p 4-6
    private final PriorityQueue<Patient> nonUrgent;    // p 7-10

    public MultiLevelPQ() {
        Comparator<Patient> byPriority = new Comparator<Patient>() {
            public int compare(Patient a, Patient b) {
                return Integer.compare(a.getPriority(), b.getPriority());
            }
        };
        urgent     = new PriorityQueue<>(byPriority);
        semiUrgent = new PriorityQueue<>(byPriority);
        nonUrgent  = new PriorityQueue<>(byPriority);
    }

    /** Route patient to the correct tier based on level. */
    public void insert(Patient p) {
        switch (p.getLevel()) {
            case 1: urgent.offer(p);     break;
            case 2: semiUrgent.offer(p); break;
            default: nonUrgent.offer(p); break;
        }
    }

    /**
     * Extract the most urgent patient across all tiers.
     * Always drains Tier 1 before Tier 2, Tier 2 before Tier 3.
     */
    public Patient extract() {
        if (!urgent.isEmpty())     return urgent.poll();
        if (!semiUrgent.isEmpty()) return semiUrgent.poll();
        return nonUrgent.poll();
    }

    /**
     * Peek at the patient who would be extracted next.
     * Used by the misordering checker.
     */
    public Patient peekMin() {
        if (!urgent.isEmpty())     return urgent.peek();
        if (!semiUrgent.isEmpty()) return semiUrgent.peek();
        return nonUrgent.peek();
    }

    public int size() {
        return urgent.size() + semiUrgent.size() + nonUrgent.size();
    }

    public boolean isEmpty() {
        return urgent.isEmpty() && semiUrgent.isEmpty() && nonUrgent.isEmpty();
    }

    public String getName() { return "Multi-Level PQ (3-tier)"; }

    // Tier sizes for reporting
    public int urgentSize()     { return urgent.size(); }
    public int semiUrgentSize() { return semiUrgent.size(); }
    public int nonUrgentSize()  { return nonUrgent.size(); }
}
