import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.ArrayList;

import java.io.FileReader;
import java.io.BufferedReader;

class GABasic {
    private static final int POPULATION_SIZE = 200; //population size per generation
    private static final int NUM_WEIGHTS = 6; //weights equivalent to feature
    private static final int WEIGHT_RANGE = 10;
    private static final int GA_XGamesPerWeight = 10;
    private static final Random RNG = new Random();
    private static final ArrayList<Integer> randomPool = new ArrayList<Integer>();
    private static final double SAMPLE_SIZE = 0.1;
    private static final double BREED_RATE = 0.5;
    private static final double ELITE_RATE = 0.5;
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
        //override the first few random weights with good weights that is stored in file
        readWeightsFromFile();
    }
    
    private void readWeightsFromFile() {
        try {
            FileReader fr = new FileReader("GoodWeights.txt");
            BufferedReader br = new BufferedReader(fr);
            
            String currentline;
            int i = 0;
            while((currentline = br.readLine()) != null) {
                String[] weights = currentline.split("\\s+");
                for(int j=0; j<NUM_WEIGHTS; j++) {
                    weightSet[i][j] = Double.parseDouble(weights[j]);
                }
                i++;
            }
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "an exception was thrown", e);
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
    
    private String convertTime(long elapsedTime) {
        long second = (elapsedTime / 1000) % 60;
        long minute = (elapsedTime / (1000*60)) % 60;
        long hour = (elapsedTime / (1000*60*60)) % 24;
        
        return String.format("%02d:%02d:%02d:%d", hour, minute, second, elapsedTime);
    }

    //Run one generation
    private void runGeneration(double[][] weights) {
        try {
            long startTime = System.currentTimeMillis();
            EvaluationResult[] evaluations = new EvaluationResult[POPULATION_SIZE];
            
            SimulationPool sp = new SimulationPool(heuristic, POPULATION_SIZE, weights, GA_XGamesPerWeight);
            evaluations = sp.startGAScheduler(evaluations);
            long stopTime = System.currentTimeMillis();

            long elapsedTime = stopTime - startTime;
            String runTime = convertTime(elapsedTime);
            
            double variance = calculateVariance(evaluations);
            if (variance <= ALLOWABLE_VARIANCE_LIMIT) {
                printBestScorer(evaluations, true, runTime);
            } else {
                printBestScorer(evaluations, true, runTime);
                runGeneration(breedWeights(evaluations));
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "an exception was thrown", e);
        }
    }

    /*private void printWeights(double[][] results) {
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
    private void printBestScorer(EvaluationResult[] results, boolean doPrintFull, String runTime) {
        Collections.sort(Arrays.asList(results));
        String bestScoreWeights = "";
        if (results[POPULATION_SIZE - 1].rowsCleared > highscore) {
            for (int i = 0; i < NUM_WEIGHTS; i++) {
                bestScoreWeights += "Feature " + i + " : " + results[POPULATION_SIZE - 1].getWeightSets()[i] + " , ";
            }
            highscore = results[POPULATION_SIZE - 1].rowsCleared;
            highscoreweights = results[POPULATION_SIZE - 1];
        }
        LOGGER.info("==================================================================");
        LOGGER.info("Generation " + generation + " ran for " + runTime + " and the highest score is " + highscore);
        LOGGER.info("Weights = " + bestScoreWeights);
        LOGGER.info("==================================================================");
        generation++;
    }

    //Breed the two weights by choosing random weights to splice
    private double[][] breedWeights(EvaluationResult[] evaluationResults) {
        double[][] result = new double[POPULATION_SIZE][NUM_WEIGHTS];
        
        //Step 1. Select a random sample of 10% of population (t)(Tournament Selection)
        //Step 2. Take the best 2 and crossover.
        //Step 3. Mutate the child with a chance of 5%.
        //Repeat until we have 50% of the population (t+1). 
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
            // C = [A,B,2,3,4,F]
            
            int k = 0;
            int point = NUM_WEIGHTS / 2;
            //Determine how many to take from parent 1
            if(evaluationResults[p1].rowsCleared > evaluationResults[p2].rowsCleared) {
                //parent 1 has a better score. Take more from parent 1.
                point += RNG.nextInt(3); //Adds either 0 or 1 or 2 more. 
            }
            
            while(k < NUM_WEIGHTS) {
                j %= NUM_WEIGHTS;
                
                if (k < point) {
                    result[i][j] = evaluationResults[p1].getWeightSets()[j]; //Child get from Parent 1
                } else {
                    result[i][j] = evaluationResults[p2].getWeightSets()[j]; //Child get from Parent 2
                }
                j++;
                k++;
            }
            
            //Step 3
            result[i] = mutateWeights(result[i]);
        }
        
        //Sort the evaluation result by rows cleared
        Collections.sort(Arrays.asList(evaluationResults));
        
        //Fill up the rest of the population (t+1) with 50% of the original population (t) --> Elitism
        for (int v = (int)(POPULATION_SIZE * BREED_RATE); v<POPULATION_SIZE; v++) {
            for(int w=0; w<NUM_WEIGHTS; w++) {
                result[v][w] = evaluationResults[v].getWeightSets()[w];
            }
        }
        
        return result;
    }

    private double[] mutate(double[] weights) {
        // Include a second index for mutation if needed
        int index = RNG.nextInt(NUM_WEIGHTS);

        if (RNG.nextDouble() <= 0.5) {
            weights[index] += WEIGHT_RANGE * RNG.nextDouble();
        } else {
            weights[index] -= WEIGHT_RANGE * RNG.nextDouble();
        }
        return weights;
    }
    
    // Mutate a random weight by a random double from -1 to 1 at MUTATION_RATE chance
    private double[] mutateWeights(double[] weights) {
        if (RNG.nextDouble() < MUTATION_RATE) {
            weights[RNG.nextInt(NUM_WEIGHTS)] += ((RNG.nextDouble() * 2) - 1);
        }
        return weights;
    }

    private void randomGenerator() {
        int upper = 5;
        int lower = -5;
        //double result = RNG.nextDouble() * (upper - lower) + lower; //Gives me [lower, upper)
        int result = RNG.nextInt(2) * 2 - 1; //Gives me -1 or 1
        System.out.println(result);
    }
    
    public static void main(String[] args) {
        //Pre-processing: Generate random weights for population (t)
        GABasic GA = new GABasic();
        GA.runGeneration();
    }
}