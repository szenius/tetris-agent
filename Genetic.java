import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class Genetic {
    public static final int NUM_FEATURES = 22;
    public static final int INDEX_MID = 10;
    public static final int INDEX_VARIANCE = 4;
    public static final int WEIGHT_RANGE = 5;
    public static final int SAMPLE_SIZE = 400;
    public static final int GAME_SIZE = 3;
    public static final double ALLOWABLE_VARIANCE_LIMIT = 1.0;
    public static final double CUT_OFF = 0.5;
    private static final Random RNG = new Random();

    private double[][] weightSet = new double[SAMPLE_SIZE][NUM_FEATURES];
    private EvaluationResult bestSet;
    private int bestScore = 0;
    private int cycle = 0;

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
            if (RNG.nextDouble() > 0.5) {
                weights[i] = WEIGHT_RANGE * RNG.nextDouble();
            } else {
                weights[i] = -1 * WEIGHT_RANGE * RNG.nextDouble();
            }
        }
        return weights;
    }

    public double[] breedRandomHeuristics(double[] h1, double[] h2) {
        double[] newWeights = new double[NUM_FEATURES];
        System.arraycopy(h1, 0, newWeights, 0, INDEX_MID - INDEX_VARIANCE);
        System.arraycopy(h2, INDEX_MID - INDEX_VARIANCE, newWeights, INDEX_MID - INDEX_VARIANCE, NUM_FEATURES - 6);
        return mutate(newWeights);
    }

    public double[] breedDirectHeuristics(double[] h1, double[] h2) {
        double[] newWeights = new double[NUM_FEATURES];
        for (int i = 0; i < NUM_FEATURES; i++) {
            if (RNG.nextDouble() > 0.5) {
                newWeights[i] = h1[i];
            } else {
                newWeights[i] = h2[i];
            }
        }
        return mutate(newWeights);
    }

    private double[] mutate(double[] weights) {
        // Include a second index for mutation if needed
        int index = RNG.nextInt(NUM_FEATURES);
        //int secondIndex = RNG.nextInt(NUM_FEATURES);

        if (RNG.nextDouble() <= 0.5) {
            weights[index] += 0.5 * WEIGHT_RANGE * RNG.nextDouble();
            //weights[secondIndex] += RNG.nextDouble();
        } else {
            weights[index] -= 0.5 * WEIGHT_RANGE * RNG.nextDouble();
            //weights[secondIndex] -= RNG.nextDouble();
        }
        return weights;
    }

    public void runGeneration(double[][] weightSets) {
        EvaluationResult[] evaluations = new EvaluationResult[SAMPLE_SIZE];
        int score;
        int game1, game2, game3;
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            game1 = playGame(weightSets[i]);
            game2 = playGame(weightSets[i]);
            game3 = playGame(weightSets[i]);
            int[] gameScores  = {game1, game2, game3};
            score = calculateSdScore(gameScores);
            evaluations[i] = new EvaluationResult(weightSets[i], score);
        }
        double variance = calculateVariance(evaluations);
        if (variance <= ALLOWABLE_VARIANCE_LIMIT) {
            printBestScorer(evaluations, true);
        } else {
            printBestScorer(evaluations, true);
            breedGeneration(evaluations);
        }
    }

    public int playGame(double[] weightSets) {
        PlayerSkeleton p = new PlayerSkeleton();
        State s = new State();
        while(!s.hasLost()) {
            s.makeMove(p.pickMove(s, s.legalMoves(), weightSets));
        }
        return s.getRowsCleared();
    }

    public void breedGeneration(EvaluationResult[] evaluations) {
        // TODO decide on how many to breed into, or infinitely until average score doesn't seem to change much
        double[][] weightSets = new double[SAMPLE_SIZE][NUM_FEATURES];
        int counter = 0;
        Collections.sort(Arrays.asList(evaluations));
        int startingIndex = (int) (CUT_OFF * SAMPLE_SIZE);
        for (int i = startingIndex; i < SAMPLE_SIZE - 1; i += 2) {
            weightSets[counter] = breedDirectHeuristics(evaluations[i].getWeightSets(), evaluations[i + 1].getWeightSets());
            weightSets[counter + 1] = breedDirectHeuristics(evaluations[i].getWeightSets(), evaluations[i + 1].getWeightSets());
            weightSets[counter + 2] = breedDirectHeuristics(evaluations[i].getWeightSets(), evaluations[i + 1].getWeightSets());
            weightSets[counter + 3] = breedDirectHeuristics(evaluations[i].getWeightSets(), evaluations[i + 1].getWeightSets());
            counter += 4;
        }
        runGeneration(weightSets);
    }

    public void printBestScorer(EvaluationResult[] results, boolean doPrintFull) {
        Collections.sort(Arrays.asList(results));
        System.out.println("Cycle " + cycle);
        if (results[SAMPLE_SIZE - 1].rowsCleared > bestScore) {
            for (int i = 0; i < NUM_FEATURES; i++) {
                System.out.print("Feature " + i + " :" + results[SAMPLE_SIZE - 1].getWeightSets()[i] + " ");
            }
            System.out.println("\nBest score: " + results[SAMPLE_SIZE-1].rowsCleared);
            bestScore = results[SAMPLE_SIZE - 1].rowsCleared;
            bestSet = results[SAMPLE_SIZE - 1];
        }
        cycle++;

    }

    private double calculateVariance(EvaluationResult[] resultSet) {
        double s1 = 0;
        double s2 = 0;
        double sum;
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            s1 += resultSet[i].getCumulativeSum();
        }
        double mean = s1 / (double) SAMPLE_SIZE;
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            sum = resultSet[i].getCumulativeSum() - mean;
            s2 += sum*sum;
        }

        double variance = s2 / (double) (SAMPLE_SIZE - 1);
        return variance;
    }

    private int calculateSdScore(int[] gameScore) {
        double s1 = 0;
        double s2 = 0;
        double sum;
        for (int i = 0; i < GAME_SIZE; i++) {
            s1 += gameScore[i];
        }
        double mean = s1 / (double) GAME_SIZE;
        for (int i = 0; i < GAME_SIZE; i++) {
            sum = gameScore[i] - mean;
            s2 += sum*sum;
        }

        double variance = s2 / (double) (GAME_SIZE - 1);
        return (int) (mean - Math.sqrt(variance));
    }
}
