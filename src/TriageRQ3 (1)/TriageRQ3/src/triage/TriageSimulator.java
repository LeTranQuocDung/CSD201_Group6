package triage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Simulates 500 patient arrivals and measures misordering rate
 * for both Single-Level PQ and Multi-Level PQ.
 *
 * Misordering definition:
 *   When patient X is extracted at position i, compare with the ideal
 *   patient who SHOULD be at position i (sorted by actual priority).
 *   If actual[i].priority > ideal[i].priority, it's a MISORDERING.
 *
 * For Single-Level PQ: theoretically 0 misorderings (always extracts min).
 * For Multi-Level PQ : misorderings occur when a patient's condition
 *   worsens (priority drift) but they remain stuck in their original tier.
 *   A now-urgent patient in the semi-urgent tier won't be extracted
 *   until ALL original urgent-tier patients are processed first.
 */
public class TriageSimulator {

    private static final int TOTAL_PATIENTS = 500;
    private static final long SEED = 42L;

    // Priority distribution (realistic ER):
    // Urgent (1-3): 20%, Semi-Urgent (4-6): 50%, Non-Urgent (7-10): 30%
    private static final double URGENT_RATIO     = 0.20;
    private static final double SEMI_URGENT_RATIO = 0.50;

    private final Random random;

    public TriageSimulator() {
        this.random = new Random(SEED);
    }

    // ----------------------------------------------------------------
    // Generate 500 patients with realistic priority distribution
    // ----------------------------------------------------------------
    public List<Patient> generatePatients() {
        List<Patient> patients = new ArrayList<>();
        for (int i = 1; i <= TOTAL_PATIENTS; i++) {
            int priority = randomPriority();
            patients.add(new Patient(i, "Patient-" + i, priority));
        }
        return patients;
    }

    private int randomPriority() {
        double r = random.nextDouble();
        if (r < URGENT_RATIO) {
            return random.nextInt(3) + 1;   // 1, 2, or 3
        } else if (r < URGENT_RATIO + SEMI_URGENT_RATIO) {
            return random.nextInt(3) + 4;   // 4, 5, or 6
        } else {
            return random.nextInt(4) + 7;   // 7, 8, 9, or 10
        }
    }

    // ----------------------------------------------------------------
    // Priority Drift Simulation
    // ----------------------------------------------------------------
    /**
     * Simulate priority drift: some patients' conditions worsen
     * (priority number decreases = more urgent) while waiting.
     *
     * KEY DESIGN: Drifted patients keep their ORIGINAL tier (level).
     * - For Single-Level PQ: tier doesn't matter, only priority.
     *   The single heap always extracts globally minimum priority → 0%.
     * - For Multi-Level PQ: tier determines routing. A patient with
     *   drifted priority 2 (Urgent) but original tier Semi-Urgent
     *   will stay in the Semi-Urgent queue → MISORDERING.
     *
     * @param original   original patient list
     * @param driftRate  probability that each patient's condition worsens
     * @param seed       random seed for reproducibility per scenario
     * @return patients with drifted priorities but original tier assignments
     */
    public List<Patient> applyDrift(List<Patient> original,
                                     double driftRate, long seed) {
        Random driftRng = new Random(seed);
        List<Patient> drifted = new ArrayList<>();

        for (Patient p : original) {
            if (driftRng.nextDouble() < driftRate) {
                // Condition worsens: priority decreases by 1-3
                int drift = driftRng.nextInt(3) + 1;
                int newP  = Math.max(1, p.getPriority() - drift);
                // Use drift constructor: keeps ORIGINAL level/tier
                drifted.add(new Patient(p.getId(), p.getName(),
                                        newP, p.getLevel()));
            } else {
                drifted.add(p);
            }
        }
        return drifted;
    }

    // ----------------------------------------------------------------
    // Run Single-Level PQ simulation
    // ----------------------------------------------------------------
    /**
     * Insert patients into a single global heap ordered by priority.
     * Since it always extracts the globally minimum priority,
     * it should match ideal order perfectly → 0% misordering.
     */
    public MisorderingResult runSingleLevel(List<Patient> patients) {
        SingleLevelPQ pq = new SingleLevelPQ();
        for (Patient p : patients) {
            pq.insert(p);
        }
        return measureMisordering(pq.getName(), patients, extractAll_Single(pq));
    }

    private List<Patient> extractAll_Single(SingleLevelPQ pq) {
        List<Patient> order = new ArrayList<>();
        while (!pq.isEmpty()) {
            order.add(pq.extract());
        }
        return order;
    }

    // ----------------------------------------------------------------
    // Run Multi-Level PQ simulation
    // ----------------------------------------------------------------
    /**
     * Insert patients into 3-tier PQ based on their level (tier).
     * Patients with drifted priority but original tier will be
     * placed in the wrong queue → misorderings at tier boundaries.
     */
    public MisorderingResult runMultiLevel(List<Patient> patients) {
        MultiLevelPQ pq = new MultiLevelPQ();
        for (Patient p : patients) {
            pq.insert(p);  // routed by getLevel() → original tier
        }
        return measureMisordering(pq.getName(), patients, extractAll_Multi(pq));
    }

    private List<Patient> extractAll_Multi(MultiLevelPQ pq) {
        List<Patient> order = new ArrayList<>();
        while (!pq.isEmpty()) {
            order.add(pq.extract());
        }
        return order;
    }

    // ----------------------------------------------------------------
    // Core misordering measurement
    // ----------------------------------------------------------------
    /**
     * Compare actual extraction order against ideal order (sorted by priority).
     * Count positions where actual[i].priority > ideal[i].priority.
     *
     * Algorithm:
     *   1. Sort all patients by priority ascending → idealOrder
     *   2. For each position i, compare actual vs ideal
     *   3. If actual priority > ideal priority → misordering
     *   4. Track breakdown by severity (urgent/semi-urgent delayed)
     */
    private MisorderingResult measureMisordering(String pqName,
                                                  List<Patient> allPatients,
                                                  List<Patient> extractionOrder) {
        // Build ideal order (sorted by priority ascending)
        List<Patient> idealOrder = new ArrayList<>(allPatients);
        idealOrder.sort(new java.util.Comparator<Patient>() {
            public int compare(Patient a, Patient b) {
                return Integer.compare(a.getPriority(), b.getPriority());
            }
        });

        int misorderCount        = 0;
        int urgentMisordered     = 0;
        int semiUrgentMisordered = 0;
        List<String> log         = new ArrayList<>();

        int size = Math.min(extractionOrder.size(), idealOrder.size());
        for (int i = 0; i < size; i++) {
            Patient actual  = extractionOrder.get(i);
            Patient ideal   = idealOrder.get(i);

            // If actual priority is WORSE than ideal priority at this position
            if (actual.getPriority() > ideal.getPriority()) {
                misorderCount++;

                // Classify by ACTUAL priority level of the delayed patient
                int idealActualLevel = Patient.classifyLevel(ideal.getPriority());
                if (idealActualLevel == 1) {
                    urgentMisordered++;
                } else if (idealActualLevel == 2) {
                    semiUrgentMisordered++;
                }

                if (log.size() < 20) { // log first 20 only
                    log.add(String.format(
                        "  Position %3d: served %s but ideal was %s",
                        i + 1, actual, ideal));
                }
            }
        }

        return new MisorderingResult(pqName, allPatients.size(),
                misorderCount, urgentMisordered, semiUrgentMisordered, log);
    }

    // ----------------------------------------------------------------
    // Distribution report
    // ----------------------------------------------------------------
    public void printDistribution(List<Patient> patients) {
        int u = 0, s = 0, n = 0;
        for (Patient p : patients) {
            switch (p.getLevel()) {
                case 1: u++; break;
                case 2: s++; break;
                default: n++; break;
            }
        }
        System.out.println("Patient distribution (n=500):");
        System.out.printf("  Urgent      (p 1-3 ): %3d patients (%4.1f%%)%n",
                u, u * 100.0 / patients.size());
        System.out.printf("  Semi-Urgent (p 4-6 ): %3d patients (%4.1f%%)%n",
                s, s * 100.0 / patients.size());
        System.out.printf("  Non-Urgent  (p 7-10): %3d patients (%4.1f%%)%n",
                n, n * 100.0 / patients.size());
    }

    /**
     * Print drift statistics: how many patients drifted, how many crossed tiers.
     */
    public void printDriftStats(List<Patient> original, List<Patient> drifted) {
        int driftCount = 0;
        int crossTierDrift = 0;
        for (int i = 0; i < original.size(); i++) {
            if (drifted.get(i).getPriority() != original.get(i).getPriority()) {
                driftCount++;
                if (drifted.get(i).isDrifted()) {
                    crossTierDrift++;
                }
            }
        }
        System.out.printf("  Patients drifted: %d/%d | Cross-tier drifts: %d%n",
                driftCount, original.size(), crossTierDrift);
    }
}
