import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Genetic {
    public static final int NUM_FEATURES = 6;
    public static final int INDEX_MID = 10;
    public static final int INDEX_VARIANCE = 4;
    public static final int WEIGHT_RANGE = 10;
    public static final int SAMPLE_SIZE = 200;
    public static final int GAME_SIZE = 10;
    public static final double ALLOWABLE_VARIANCE_LIMIT = 20.0;
    public static final double CUT_OFF = 0.25;
    public static final double CHILDREN_TO_BREED = 8;
    private static final Random RNG = new Random();

    private double[][] weightSet = new double[SAMPLE_SIZE][NUM_FEATURES];
    private EvaluationResult bestSet;
    private int bestScore = 0;
    private int cycle = 0;
    private long timeStart;
    private long timeEnd;

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

    public Genetic(String filename) {
        try {
			/*
			 * Converts the text in the file into a string,
			 * before processing.
			 */
			double[][] initialWeights = new double[10][NUM_FEATURES];
            BufferedReader input = new BufferedReader(new FileReader(filename));
            String currentLine= input.readLine();
            int counter = 0;
            String[] splitted;
            while (currentLine != null){
                splitted = currentLine.split(" ");
                initialWeights[counter] = Arrays.stream(splitted)
                        .mapToDouble(Double::parseDouble)
                        .toArray();;
                currentLine= input.readLine();
                counter++;
            }
            input.close();
            for (int i = 0; i < SAMPLE_SIZE; i++) {
                weightSet[i] = breedDirectHeuristics(initialWeights[RNG.nextInt(10)], initialWeights[RNG.nextInt(10)]);
            }
            runGeneration(weightSet);
        } catch (FileNotFoundException fnfe) {

        } catch (IOException ie) {

        }
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
            weights[index] += WEIGHT_RANGE * RNG.nextDouble();
            //weights[secondIndex] += RNG.nextDouble();
        } else {
            weights[index] -= WEIGHT_RANGE * RNG.nextDouble();
            //weights[secondIndex] -= RNG.nextDouble();
        }
        return weights;
    }

    public void runGeneration(double[][] weightSets) {
        EvaluationResult[] evaluations = new EvaluationResult[SAMPLE_SIZE];
        int score = 0;
        int[] gameScores = new int[GAME_SIZE];
        timeStart = System.currentTimeMillis();
        ExecutorService executor = Executors.newFixedThreadPool(3); //Threadpool size = ?
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            ArrayList<Future<Integer>> results = new ArrayList<Future<Integer>>();
            Simulation game = new Simulation(new Heuristic(false, true), weightSets[i], 10);
            Future<Integer> future = executor.submit(game);
/*            for (int j = 0; j < GAME_SIZE; j++) {
                Future<Integer> future = executor.submit(game); //Add Thread to be executed by thread pool
                results.add(future);
            }*/
            //score = getMedian(results);
            try {
                score = future.get();
            } catch (InterruptedException | ExecutionException e) {
                assert false: "Something wrong happened!";
            }
            System.out.println("[" + cycle + "], " + score + ", " + Arrays.toString(weightSets[i]));
            evaluations[i] = new EvaluationResult(weightSets[i], score);
        }
        double variance = calculateVariance(evaluations);
        timeEnd = System.currentTimeMillis();
/*        if (variance <= ALLOWABLE_VARIANCE_LIMIT) {
            printBestScorer(evaluations, true);
        } else {
            printBestScorer(evaluations, true);
            breedGeneration(evaluations);
        }*/
        if (cycle <= 25) {
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
        double[][] weightSets = new double[SAMPLE_SIZE][NUM_FEATURES];
        int counter = 0;
        Collections.sort(Arrays.asList(evaluations));
        int startingIndex = (int) (CUT_OFF * SAMPLE_SIZE);
        for (int i = SAMPLE_SIZE - startingIndex - 1; i < SAMPLE_SIZE - 1; i += 2) {
            for (int j = 0; j < CHILDREN_TO_BREED; j++) {
                //System.out.println(counter + j);
                weightSets[counter + j] = breedDirectHeuristics(evaluations[i].getWeightSets(), evaluations[i + 1].getWeightSets());
            }
            counter += CHILDREN_TO_BREED;
        }
        runGeneration(weightSets);
    }

    public void printBestScorer(EvaluationResult[] results, boolean doPrintFull) {
        Collections.sort(Arrays.asList(results));
        System.out.println();
        if (results[SAMPLE_SIZE - 1].rowsCleared > bestScore) {
            for (int i = 0; i < NUM_FEATURES; i++) {
                System.out.print(results[SAMPLE_SIZE - 1].getWeightSets()[i] + ", ");
            }
            System.out.println("\nCycle " + cycle + " Best score: " + results[SAMPLE_SIZE-1].rowsCleared + " Time taken: " + ((timeEnd - timeStart) / 1e3) + " seconds");
            bestScore = results[SAMPLE_SIZE - 1].rowsCleared;
            bestSet = results[SAMPLE_SIZE - 1];
            // TODO output to file
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
        //System.out.println(variance);
        return variance;
    }

    private int calculateSdScore(ArrayList<Future<Integer>> gameScore) {
        double s1 = 0;
        double s2 = 0;
        double sum;
        try {
            for (int i = 0; i < GAME_SIZE; i++) {
                s1 += gameScore.get(i).get();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        double mean = s1 / (double) GAME_SIZE;

        try {
            for (int i = 0; i < GAME_SIZE; i++) {
                sum = gameScore.get(i).get() - mean;
                s2 += sum*sum;
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        double variance = s2 / (double) (GAME_SIZE - 1);
        return (int) (mean);
    }

    private int getMedian(ArrayList<Future<Integer>> gameScore) {
        ArrayList<Integer> results = new ArrayList<>();
        try {
            for (int i = 0; i < GAME_SIZE; i++) {
                results.add(gameScore.get(i).get());
            }
        } catch (InterruptedException | ExecutionException e) {
            assert false: "Something wrong happened!";
        }
        Collections.sort((results));
        return GAME_SIZE % 2 == 0 ? (int) (results.get(GAME_SIZE/2 + 1) + results.get(GAME_SIZE/2))/2 : results.get(GAME_SIZE/2);
    }
}
