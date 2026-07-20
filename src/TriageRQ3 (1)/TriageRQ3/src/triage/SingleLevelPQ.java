package triage;

import java.util.PriorityQueue;
import java.util.Comparator;

/**
 * Single-Level Priority Queue.
 *
 * All patients enter ONE queue ordered by priority (1 = most urgent).
 * extract() always returns the patient with the smallest priority value.
 *
 * Complexity: insert O(log n), extract O(log n).
 */
public class SingleLevelPQ {

    private final PriorityQueue<Patient> queue;

    public SingleLevelPQ() {
        // Order by priority ascending (1 comes first)
        this.queue = new PriorityQueue<>(new Comparator<Patient>() {
            public int compare(Patient a, Patient b) {
                return Integer.compare(a.getPriority(), b.getPriority());
            }
        });
    }

    /** Add a patient to the queue. */
    public void insert(Patient p) {
        queue.offer(p);
    }

    /**
     * Remove and return the most urgent patient.
     * Returns null if empty.
     */
    public Patient extract() {
        return queue.poll();
    }

    /**
     * Peek at the most urgent patient WITHOUT removing.
     * Used by the misordering checker.
     */
    public Patient peekMin() {
        return queue.peek();
    }

    public int  size()    { return queue.size(); }
    public boolean isEmpty() { return queue.isEmpty(); }

    public String getName() { return "Single-Level PQ"; }
}
