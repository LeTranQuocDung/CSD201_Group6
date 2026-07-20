package triage;

import java.util.List;

/**
 * RQ3 Entry Point:
 *
 * "How to measure the misordering rate when comparing
 *  Single-Level PQ vs Multi-Level PQ (3-tier) on 500 simulated patients?"
 *
 * Runs 3 scenarios:
 *   Scenario A: 500 patients, no priority drift (baseline)
 *   Scenario B: 500 patients, with 15% priority drift (realistic ER)
 *   Scenario C: 500 patients, with 30% priority drift (high-stress ER)
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("  RQ3 - Misordering Rate Comparison");
        System.out.println("  Single-Level PQ vs Multi-Level PQ (3-tier)");
        System.out.println("  Smart Hospital Patient Triage System");
        System.out.println("=================================================\n");

        TriageSimulator sim = new TriageSimulator();

        // Generate 500 patients (same set for fair comparison)
        List<Patient> patients = sim.generatePatients();

        System.out.println("--- Patient Dataset ---");
        sim.printDistribution(patients);
        System.out.println();

        // ---- Scenario A: No drift (baseline) ----
        System.out.println("=================================================");
        System.out.println("  SCENARIO A: No priority drift (baseline)");
        System.out.println("=================================================");
        runScenario(sim, patients, 0.0, 100L);

        // ---- Scenario B: 15% drift (realistic) ----
        System.out.println("=================================================");
        System.out.println("  SCENARIO B: 15% priority drift (realistic ER)");
        System.out.println("=================================================");
        runScenario(sim, patients, 0.15, 200L);

        // ---- Scenario C: 30% drift (high-stress) ----
        System.out.println("=================================================");
        System.out.println("  SCENARIO C: 30% priority drift (high-stress ER)");
        System.out.println("=================================================");
        runScenario(sim, patients, 0.30, 300L);

        // ---- Final comparison table ----
        printFinalTable(sim, patients);
    }

    private static void runScenario(TriageSimulator sim,
                                    List<Patient> patients,
                                    double driftRate,
                                    long seed) {
        // Apply drift ONCE - same drifted patients used for both PQ types
        List<Patient> drifted = sim.applyDrift(patients, driftRate, seed);

        // Show drift statistics
        sim.printDriftStats(patients, drifted);
        System.out.println();

        // Run both PQ types on the SAME drifted patient set
        MisorderingResult single = sim.runSingleLevel(drifted);
        MisorderingResult multi  = sim.runMultiLevel(drifted);

        System.out.println("Single-Level PQ:");
        System.out.println("  " + single);
        if (!single.getMisorderLog().isEmpty()) {
            System.out.println("  Sample misorderings (first 5):");
            List<String> log = single.getMisorderLog();
            for (int i = 0; i < Math.min(5, log.size()); i++) {
                System.out.println(log.get(i));
            }
        }

        System.out.println();
        System.out.println("Multi-Level PQ:");
        System.out.println("  " + multi);
        if (!multi.getMisorderLog().isEmpty()) {
            System.out.println("  Sample misorderings (first 5):");
            List<String> log = multi.getMisorderLog();
            for (int i = 0; i < Math.min(5, log.size()); i++) {
                System.out.println(log.get(i));
            }
        }

        System.out.println();
        System.out.println("  Difference: Multi-Level has "
            + (multi.getMisorderCount() - single.getMisorderCount())
            + " more misorderings than Single-Level.");
        System.out.println();
    }

    private static void printFinalTable(TriageSimulator sim,
                                        List<Patient> patients) {
        System.out.println("=================================================");
        System.out.println("  FINAL SUMMARY TABLE");
        System.out.println("=================================================");
        System.out.printf("%-25s %8s %12s %10s %12s%n",
            "Scenario / PQ Type", "Total", "Misordered", "Rate(%)", "Urgent Late");
        System.out.println(new String(new char[70]).replace('\0', '-'));

        double[] rates = {0.0, 0.15, 0.30};
        long[] seeds   = {100L, 200L, 300L};
        String[] names = {"A-No drift", "B-15% drift", "C-30% drift"};

        for (int sc = 0; sc < 3; sc++) {
            List<Patient> drifted = sim.applyDrift(patients, rates[sc], seeds[sc]);
            MisorderingResult s = sim.runSingleLevel(drifted);
            MisorderingResult m = sim.runMultiLevel(drifted);

            System.out.printf("%-25s %8d %12d %9.1f%% %12d%n",
                names[sc] + " Single",
                s.getTotalPatients(), s.getMisorderCount(),
                s.getMisorderRate(), s.getUrgentMisordered());
            System.out.printf("%-25s %8d %12d %9.1f%% %12d%n",
                names[sc] + " Multi",
                m.getTotalPatients(), m.getMisorderCount(),
                m.getMisorderRate(), m.getUrgentMisordered());
        }

        System.out.println();
        System.out.println("=================================================");
        System.out.println("  ANSWER TO RQ3:");
        System.out.println("=================================================");

        System.out.println();
        System.out.println("  METHOD: Compare extraction order against ideal order");
        System.out.println("  (sorted by actual priority). Count positions where");
        System.out.println("  actual[i].priority > ideal[i].priority as misorderings.");
        System.out.println("  misordering rate = misorderCount / 500 * 100%");
        System.out.println();

        // Use scenario B (15% drift) for the final answer
        List<Patient> driftedB = sim.applyDrift(patients, 0.15, 200L);
        MisorderingResult sB = sim.runSingleLevel(driftedB);
        MisorderingResult mB = sim.runMultiLevel(driftedB);

        System.out.printf("  Single-Level PQ (15%% drift): %d misorderings (%.1f%%)%n",
            sB.getMisorderCount(), sB.getMisorderRate());
        System.out.println("  -> Always extracts the globally minimum priority.");
        System.out.println("  -> No misordering regardless of priority drift.");
        System.out.println();

        System.out.printf("  Multi-Level PQ  (15%% drift): %d misorderings (%.1f%%)%n",
            mB.getMisorderCount(), mB.getMisorderRate());
        System.out.println("  -> Misorderings occur because patients whose conditions");
        System.out.println("     worsen STAY in their original (lower-priority) tier.");
        System.out.println("  -> A now-urgent patient stuck in Semi-Urgent tier is");
        System.out.println("     served AFTER all original Urgent-tier patients.");
        System.out.println();

        if (mB.getMisorderCount() > sB.getMisorderCount()) {
            System.out.println("  CONCLUSION: Single-Level PQ has a LOWER misordering");
            System.out.println("  rate than Multi-Level PQ when priority drift occurs.");
            System.out.println("  Multi-Level PQ trades strict priority accuracy for");
            System.out.println("  organizational benefits (batch processing by severity),");
            System.out.println("  but this causes measurable misordering when patient");
            System.out.println("  conditions change after initial triage.");
        } else {
            System.out.println("  CONCLUSION: Both PQ types show similar misordering");
            System.out.println("  rates on this dataset. Multi-Level PQ organizational");
            System.out.println("  benefits outweigh its marginal misordering risk.");
        }
        System.out.println("=================================================");
    }
}
