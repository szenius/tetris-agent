import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class GeneticAlgo {
    private static final int NUM_SETS = 400;
    private static final int NUM_WEIGHTS = 22;
    private static final int WEIGHT_RANGE = 10;
    private static final int GAME_SIZE = 3;
    
    private static final double MUTATION_RATE = 0.02;
    private static final double BREED_RATE = 0.6;
    public static final double ALLOWABLE_VARIANCE_LIMIT = 1.0;

    private EvaluationResult highscoreweights;

    private int highscore = 0;
    private int generation = 0;

    double[][] weights = new double[NUM_SETS][NUM_WEIGHTS];
    Random rng = new Random();

    public GeneticAlgo() {
        for (int i = 0 ; i < NUM_SETS; i++) {
            weights[i] = generateWeights();
        }
        runGeneration(weights);
    }

    //Generate random weights for a game
    private double[] generateWeights() {
        double[] result = new double[NUM_WEIGHTS];
        for (int i = 0; i < NUM_WEIGHTS; i++) {
            result[i] = rng.nextDouble() * WEIGHT_RANGE - 5;
        }
        return result;
    }

    //Plays one instance of the game
    public int playGame(double[] weights) {
        PlayerSkeleton p = new PlayerSkeleton();
        State s = new State();
        while(!s.hasLost()) {
            s.makeMove(p.pickMove(s, s.legalMoves(), weights));
        }
        return s.getRowsCleared();
    }

    //Prints the score
    public void printBestScorer(EvaluationResult[] results, boolean doPrintFull) {
        Collections.sort(Arrays.asList(results));
        System.out.println("Generation " + generation);
        if (results[NUM_SETS - 1].rowsCleared > highscore) {
            for (int i = 0; i < NUM_WEIGHTS; i++) {
                System.out.println("Feature " + i + " :" + results[NUM_SETS - 1].getWeightSets()[i] + " ");
            }
            System.out.println("\nBest score: " + results[NUM_SETS-1].rowsCleared);
            highscore = results[NUM_SETS - 1].rowsCleared;
            highscoreweights = results[NUM_SETS - 1];
        }
        generation++;
    }

    //Calculate variance of all the weights
    private double calculateVariance(EvaluationResult[] resultSet) {
        double s1 = 0;
        double s2 = 0;
        double sum;
        for (int i = 0; i < NUM_SETS; i++) {
            s1 += resultSet[i].getCumulativeSum();
        }
        double mean = s1 / (double) NUM_SETS;
        for (int i = 0; i < NUM_SETS; i++) {
            sum = resultSet[i].getCumulativeSum() - mean;
            s2 += sum*sum;
        }

        double variance = s2 / (double) (NUM_SETS - 1);
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

    //Run one generation
    private void runGeneration(double[][] weights) {
        EvaluationResult[] evaluations = new EvaluationResult[NUM_SETS];
        int score;
        int game1, game2, game3;
        for (int i = 0; i < NUM_SETS; i++) {
            game1 = playGame(weights[i]);
            game2 = playGame(weights[i]);
            game3 = playGame(weights[i]);
            int[] gameScores  = {game1, game2, game3};
            score = calculateSdScore(gameScores);
            evaluations[i] = new EvaluationResult(weights[i], score);
        }
        double variance = calculateVariance(evaluations);
        if (variance <= ALLOWABLE_VARIANCE_LIMIT) {
            printBestScorer(evaluations, true);
        } else {
            printBestScorer(evaluations, true);
            runGeneration(breedWeights(evaluations));
        }
    }

    //Breed the two weights by choosing random weights to splice
    private double[][] breedWeights(EvaluationResult[] evaluationResults) {
        double[][] result = new double[NUM_SETS][NUM_WEIGHTS];
        //Sort the evaluation result by rows cleared
        Collections.sort(Arrays.asList(evaluationResults));
        //Decide which child gets which parent's weight
        for (int j = 0; j < NUM_SETS * BREED_RATE; j += 2) {
            for (int i = 0; i < NUM_WEIGHTS; i++) {
                if (rng.nextDouble() > 0.5) {
                    result[j][i] = evaluationResults[j].getWeightSets()[i];
                    result[j+1][i] = evaluationResults[j+1].getWeightSets()[i];
                } else {
                    result[j][i] =  evaluationResults[j+1].getWeightSets()[i];
                    result[j+1][i] =  evaluationResults[j].getWeightSets()[i];
                }
            }
            //Mutate the children
            result[j] = mutateWeights(result[j]);
            result[j+1] = mutateWeights(result[j+1]);
        }
        //Get random new weights to replace the worst performing sets of weights
        for (int k = (int)(NUM_SETS * BREED_RATE); k < NUM_SETS; k++) {
            result[k] = generateWeights();
        }
        return result;
    }

    // Mutate a random weight by a random double from -1 to 1 at MUTATION_RATE chance
    private double[] mutateWeights(double[] weights) {
         if (rng.nextDouble() < MUTATION_RATE) {
             weights[rng.nextInt(NUM_WEIGHTS)] += ((rng.nextDouble() * 2) - 1);
         }
         return weights;
    }




}
