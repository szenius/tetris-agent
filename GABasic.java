import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.ArrayList;

class GABasic {
    private static final int POPULATION_SIZE = 200; //population size per generation
    private static final int NUM_WEIGHTS = 6; //weights equivalent to feature
    private static final int WEIGHT_RANGE = 10;
    private static final int GA_XGamesPerWeight = 10;
    private static final Random RNG = new Random();
    private static final ArrayList<Integer> randomPool = new ArrayList<Integer>();
    private static final double SAMPLE_SIZE = 0.1;
    private static final double BREED_RATE = 0.3;
    private static final double ELITE_RATE = 0.65;
    private static final double MUTATION_RATE = 0.05; 
    private static final double ALLOWABLE_VARIANCE_LIMIT = 1.0;
    
    private double[][] weightSet = new double[POPULATION_SIZE][NUM_WEIGHTS];
    
    private Heuristic heuristic;
    private EvaluationResult highscoreweights;
    private int highscore = 0;
    private int generation = 0;
    
    private Logger LOGGER = Logging.getInstance();

    public GABasic() {
        this.heuristic = new Heuristic(false, true);
        for(int i=0; i<POPULATION_SIZE; i++) {
            randomPool.add(i);
            weightSet[i] = generateWeights();
        }
    }

    //Generate random weights for a game
    private double[] generateWeights() {
        double[] weights = new double[NUM_WEIGHTS];
        for (int i=0; i<NUM_WEIGHTS; i++) {
            if (RNG.nextDouble() > 0.5) {
                weights[i] = WEIGHT_RANGE * RNG.nextDouble();
            } else {
                weights[i] = -1 * WEIGHT_RANGE * RNG.nextDouble();
            }
        }
        return weights;
    }

    private void runGeneration() {
        runGeneration(this.weightSet);
    }

    //Run one generation
    private void runGeneration(double[][] weights) {
        try {
            EvaluationResult[] evaluations = new EvaluationResult[POPULATION_SIZE];
            
            SimulationPool sp = new SimulationPool(heuristic, POPULATION_SIZE, weights, GA_XGamesPerWeight);
            evaluations = sp.startGAScheduler(evaluations);

            double variance = calculateVariance(evaluations);
            if (variance <= ALLOWABLE_VARIANCE_LIMIT) {
                printBestScorer(evaluations, true);
            } else {
                printBestScorer(evaluations, true);
                runGeneration(breedWeights(evaluations));
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "an exception was thrown", e);
        }
    }

    /*
    private void printWeights(double[][] results) {
        for(int i=0; i<POPULATION_SIZE; i++) {
            for(int j=0; j<NUM_WEIGHTS; j++) {
                System.out.print(results[i][j] + " ");
            }
            System.out.println();
        }
    }*/

    //calculate variance of all weights
    private double calculateVariance(EvaluationResult[] resultSet) {
        double s1 = 0;
        double s2 = 0;
        double sum;
        for (int i = 0; i < POPULATION_SIZE; i++) {
            s1 += resultSet[i].getCumulativeSum();
        }
        double mean = s1 / (double) POPULATION_SIZE;
        for (int i = 0; i < POPULATION_SIZE; i++) {
            sum = resultSet[i].getCumulativeSum() - mean;
            s2 += sum*sum;
        }

        double variance = s2 / (double) (POPULATION_SIZE - 1);
        return variance;
    }

    //Prints the score
    private void printBestScorer(EvaluationResult[] results, boolean doPrintFull) {
        Collections.sort(Arrays.asList(results));
        //System.out.println("Generation " + generation);
        LOGGER.info("Generation " + generation + " has highest score of : " + highscore);
        LOGGER.info("==================================================================");
        if (results[POPULATION_SIZE - 1].rowsCleared > highscore) {
            for (int i = 0; i < NUM_WEIGHTS; i++) {
                //System.out.println("Feature " + i + " :" + results[POPULATION_SIZE - 1].getWeightSets()[i] + " ");
                LOGGER.info("Feature " + i + " :" + results[POPULATION_SIZE - 1].getWeightSets()[i] + " ");
            }
            //System.out.println("\nBest score: " + results[POPULATION_SIZE-1].rowsCleared);
            LOGGER.info("\nBest score: " + results[POPULATION_SIZE-1].rowsCleared);
            highscore = results[POPULATION_SIZE - 1].rowsCleared;
            highscoreweights = results[POPULATION_SIZE - 1];
        }
        generation++;
    }

    //Breed the two weights by choosing random weights to splice
    private double[][] breedWeights(EvaluationResult[] evaluationResults) {
        double[][] result = new double[POPULATION_SIZE][NUM_WEIGHTS];
        
        //Step 1. Select a random sample of 10% of population (Tournament Selection)
        //Step 2. Take the best 2 and crossover.
        //Repeat until we have 30% of the population. 
        for(int i=0; i<POPULATION_SIZE * BREED_RATE; i++) {
            
            //Step 1
            Collections.shuffle(randomPool);
            int p1 = -1;
            int p2 = -1;
            for(int j=0; j<POPULATION_SIZE * SAMPLE_SIZE; j++) {
                int selected = randomPool.get(j);
                
                if (p1 < 0) {
                    p1 = selected;
                    continue;
                }
                if (p2 < 0) {
                    p2 = selected;
                    continue;
                }
                
                if (evaluationResults[selected].rowsCleared > evaluationResults[p1].rowsCleared 
                    && evaluationResults[selected].rowsCleared > evaluationResults[p2].rowsCleared) {
                    p2 = p1;
                    p1 = selected;
                } else if (evaluationResults[selected].rowsCleared > evaluationResults[p2].rowsCleared) {
                    p2 = selected;
                }
            }
            
            //Step 2
            int j = RNG.nextInt(NUM_WEIGHTS); //0-5 crossover point
            //randomly select index. eg. 2 
            // P1 = [0,1,2,3,4,5] , P2 = [A,B,C,D,E,F]
            // C1 = [0,1,C,D,E,5] , C2 = [A,B,2,3,4,F]
            
            int k = 0;
            int half = NUM_WEIGHTS / 2;
            while(k < NUM_WEIGHTS) {
                j %= NUM_WEIGHTS;
                
                if (k < half) {
                    result[i][j] = evaluationResults[p1].getWeightSets()[j]; //Child get from Parent 1
                } else {
                    result[i][j] = evaluationResults[p2].getWeightSets()[j]; //Child get from Parent 2
                }
                j++;
                k++;
            }
        }
        
        //Sort the evaluation result by rows cleared
        Collections.sort(Arrays.asList(evaluationResults));

        //Fill up the rest of the population (t+1) with 65% of the original population (t) --> Elitism
        int u = 0;
        for (int v = (int)(POPULATION_SIZE * BREED_RATE); v<POPULATION_SIZE; v++) {
            for(int w=0; w<NUM_WEIGHTS; w++) {
                result[v][w] = evaluationResults[u].getWeightSets()[w];
            }
            u++;
        }

        //Randomly choose 5% of population and mutate them to create the last 5% of the population.
        for(int m = (int)(POPULATION_SIZE * (BREED_RATE+ELITE_RATE)); m<POPULATION_SIZE; m++) {
            int n = RNG.nextInt(POPULATION_SIZE);
            result[m] = mutateWeights(result[n]);
        }

        return result;
    }

    // Mutate a random weight by a random double from -1 to 1 at MUTATION_RATE chance
    private double[] mutateWeights(double[] weights) {
        weights[RNG.nextInt(NUM_WEIGHTS)] += ((RNG.nextDouble() * 2) - 1);
        return weights;
    }

    public static void main(String[] args) {
        //Pre-processing: Generate random weights for population (t)
        GABasic GA = new GABasic();
        GA.runGeneration();
    }
}