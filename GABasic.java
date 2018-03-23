import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

class GABasic {
	private static final int POPULATION_SIZE = 10; //population size per generation
    private static final int NUM_WEIGHTS = 6; //weights equaivalent to feature
    private static final int WEIGHT_RANGE = 10;
    private static final int GAME_SIZE = 3;
    private static final Random RNG = new Random();
    private static final double BREED_RATE = 0.2;
    private static final double MUTATION_RATE = 0.02;
    private static final double ALLOWABLE_VARIANCE_LIMIT = 1.0;

    private double[][] weightSet = new double[POPULATION_SIZE][NUM_WEIGHTS];

    private EvaluationResult highscoreweights;
    private int highscore = 0;
    private int generation = 0;

	public GABasic() {
		for(int i=0; i<POPULATION_SIZE; i++) {
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
        EvaluationResult[] evaluations = new EvaluationResult[POPULATION_SIZE];
        int score;
        int game1, game2, game3;
        for (int i = 0; i < POPULATION_SIZE; i++) {
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

    public int playGame(double[] weightSets) {
        PlayerSkeleton p = new PlayerSkeleton();
        State s = new State();
        while(!s.hasLost()) {
            s.makeMove(p.pickMove(s, s.legalMoves(), weightSets));
        }
        return s.getRowsCleared();
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

    //calculate SD for weights
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

    //Prints the score
    private void printBestScorer(EvaluationResult[] results, boolean doPrintFull) {
        Collections.sort(Arrays.asList(results));
        System.out.println("Generation " + generation);
        if (results[POPULATION_SIZE - 1].rowsCleared > highscore) {
            for (int i = 0; i < NUM_WEIGHTS; i++) {
                System.out.println("Feature " + i + " :" + results[POPULATION_SIZE - 1].getWeightSets()[i] + " ");
            }
            System.out.println("\nBest score: " + results[POPULATION_SIZE-1].rowsCleared);
            highscore = results[POPULATION_SIZE - 1].rowsCleared;
            highscoreweights = results[POPULATION_SIZE - 1];
        }
        generation++;
    }

    //Breed the two weights by choosing random weights to splice
    private double[][] breedWeights(EvaluationResult[] evaluationResults) {
        double[][] result = new double[POPULATION_SIZE][NUM_WEIGHTS];
        //Sort the evaluation result by rows cleared
        Collections.sort(Arrays.asList(evaluationResults));
        
        //Breed the top 20% best performing of the population. 
        for (int i=0; i<POPULATION_SIZE * BREED_RATE; i+=2) {
            int j = RNG.nextInt(NUM_WEIGHTS); //0-5
            //randomly select index. eg. 2 
           	// P1 = [0,1,2,3,4,5] , P2 = [A,B,C,D,E,F]
            // C1 = [0,1,C,D,E,5] , C2 = [A,B,2,3,4,F]

            int k = 0;
            int half = NUM_WEIGHTS / 2;
            while(k < NUM_WEIGHTS) {
            	j %= NUM_WEIGHTS;
            	
            	if(k < half) {
            		result[i][j] = evaluationResults[i].getWeightSets()[j]; //Child 1 get from Parent 1
            		result[i+1][j] = evaluationResults[i+1].getWeightSets()[j]; //Child 2 get from Parent 2
            	} else {
            		result[i][j] = evaluationResults[i+1].getWeightSets()[j]; //Child 1 get from Parent 2
            		result[i+1][j] = evaluationResults[i].getWeightSets()[j]; //Child 2 get from Parent 1
           	 	}
           	 	j++;
            	k++;
            }
        }

        //Fill up the rest of the population (t+1) with 80% of the original population (t)
        int u = 0;
        for (int v = (int)(POPULATION_SIZE * BREED_RATE); v<POPULATION_SIZE; v++) {
            for(int w=0; w<NUM_WEIGHTS; w++) {
            	result[v][w] = evaluationResults[u].getWeightSets()[w];
            }
            u++;
        }

        //Randomly choose 0.02% of population and mutate them.
        int mutates = (int)(POPULATION_SIZE * MUTATION_RATE);
        for(int m=0; m<mutates; m++) {
        	result[m] = mutateWeights(result[m]);
        }

        return result;
    }

    // Mutate a random weight by a random double from -1 to 1 at MUTATION_RATE chance
    private double[] mutateWeights(double[] weights) {
         if (RNG.nextDouble() < MUTATION_RATE) {
             weights[RNG.nextInt(NUM_WEIGHTS)] += ((RNG.nextDouble() * 2) - 1);
         }
         return weights;
    }

	public static void main(String[] args) {
		//Pre-processing: Generate random weights for population (t)
		GABasic GA = new GABasic();
		GA.runGeneration();
	}
}