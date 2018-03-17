import java.util.Random;

public class Genetic {
    public static final int NUM_FEATURES = 21;
    public static final int INDEX_MID = 10;
    public static final int INDEX_VARIANCE = 4;
    public static final int SAMPLE_SIZE = 1000;
    private static final Random RNG = new Random();

    private double[][] weightSet = new double[SAMPLE_SIZE][];

    // TODO read heuristic from file, store them for use, then breed.
    public Genetic(String filename) {
        // TODO import file

        runGeneration(weightSet);
    }

    /**
     * Creates a new Genetic object with no initial data-set.
     * The initial data-set will consist of 1000-randomly selected set of weights.
     */
    public Genetic() {
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            weightSet[i] = createRandomWeights();
        }
        runGeneration(weightSet);
    }

    private double[] createRandomWeights() {
        double[] weights = new double[NUM_FEATURES];
        for (int i = 0; i < NUM_FEATURES; i++) {
            weights[i] = RNG.nextDouble();
        }
        return weights;
    }

    public int[] breedHeuristics(int[] h1, int[] h2) {
        int[] newWeights = new int[NUM_FEATURES];
        System.arraycopy(h1, 0, newWeights, 0, INDEX_MID - INDEX_VARIANCE);
        System.arraycopy(h2, INDEX_MID - INDEX_VARIANCE, newWeights, INDEX_MID - INDEX_VARIANCE, NUM_FEATURES - 6);
        return mutate(newWeights);
    }

    private int[] mutate(int[] weights) {
        // Include a second index for mutation if needed
        int index = RNG.nextInt(NUM_FEATURES);
        //int secondIndex = RNG.nextInt(NUM_FEATURES);

        if (RNG.nextDouble() <= 0.5) {
            weights[index] += RNG.nextDouble();
            //weights[secondIndex] += RNG.nextDouble();
        } else {
            weights[index] -= RNG.nextDouble();
            //weights[secondIndex] -= RNG.nextDouble();
        }
        return weights;
    }

    public int[] runGeneration(double[][] weightSets) {
        Heuristic heuristic = new Heuristic();
        int[] evaluations = new int[SAMPLE_SIZE];
        State s = new State();
        PlayerSkeleton p = new PlayerSkeleton();
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            heuristic.weights = weightSets[i];

            // This section runs the game and logs the score, along with the weight-sets being used.
            s = new State();
            while(!s.hasLost()) {
                s.makeMove(p.pickMove(s,s.legalMoves()));
                s.draw();
                s.drawNext(0,0);
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            evaluations[i] = s.getRowsCleared();
        }
        return evaluations;
    }

    public int[][] breedGeneration(int[][] evaluations) {
        // TODO decide on how many to breed into, or infinitely until average score doesn't seem to change much
    }
}
