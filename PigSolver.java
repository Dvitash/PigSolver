import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class PigSolver {
    int goal;
    double epsilon;
    double[][][] p;
    boolean[][][] flip;

    PigSolver(int goal, double epsilon) {
        this.goal = goal;
        this.epsilon = epsilon;
        p = new double[goal][goal][goal];
        flip = new boolean[goal][goal][goal];

        valueIterate();
    }

    // i = your score
    // j = opponent's score
    // k = number of turns
    // p[i][j][k] = probability of winning with i, j, k

    void valueIterate() {
        double maxChange;
        do {
            maxChange = 0.0;
            for (int i = 0; i < goal; i++) // for all i
                for (int j = 0; j < goal; j++) // for all j
                    for (int k = 0; k < goal - i; k++) { // for all k
                        double oldProb = p[i][j][k];

                        double constant = (1.0 / 6.0);
                        double pRoll = constant * (1.0 - pWin(j, i, 0)); // rolling a 1

                        for (int r = 2; r <= 6; r++) {
                            pRoll += constant * pWin(i, j, k + r); // rolling 2–6
                        }

                        double pHold = 1.0 - pWin(j, i + k, 0);
                        p[i][j][k] = Math.max(pRoll, pHold);
                        flip[i][j][k] = pRoll > pHold;
                        double change = Math.abs(p[i][j][k] - oldProb);
                        maxChange = Math.max(maxChange, change);
                    }
        } while (maxChange >= epsilon);
    }

    public double pWin(int i, int j, int k) {
        if (i + k >= goal)
            return 1.0;
        else if (j >= goal)
            return 0.0;
        else
            return p[i][j][k];
    }

    public void outputHoldValues() {
        for (int i = 0; i < goal; i++) {
            for (int j = 0; j < goal; j++) {
                int k = 0;
                while (k < goal - i && flip[i][j][k])
                    k++;
                System.out.print(k + " ");
            }
            System.out.println();
        }
    }

    public void exportPolicyToCSV(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("i,j,k,action");

            for (int i = 0; i < goal; i++) {
                for (int j = 0; j < goal; j++) {
                    for (int k = 0; k < goal - i; k++) {
                        String action = flip[i][j][k] ? "ROLL" : "HOLD";
                        writer.printf("%d,%d,%d,%s%n", i, j, k, action);
                    }
                }
            }
            System.out.println("Policy exported to " + filename);
        } catch (IOException e) {
            System.err.println("Error writing to CSV: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        PigSolver solver = new PigSolver(100, 1e-9);
        // solver.outputHoldValues();
        solver.exportPolicyToCSV("policy.csv");
    }
}
