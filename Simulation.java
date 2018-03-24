import java.util.concurrent.Callable;
import java.util.Arrays;

class Simulation implements Callable<Integer> {
	private double[] weightSets;
	private boolean findAverage;
	private int numRepetitions;

	public Simulation() {
		weightSets = null;
		findAverage = false;
		numRepetitions = 1;
	}

	public Simulation(double[] weightSets) {
		this.weightSets = weightSets;
		this.findAverage = false;
		this.numRepetitions = 1;
	}

	public Simulation(double[] weightSets, boolean findAverage, int numRepetitions) {
		this.weightSets = weightSets;
		this.findAverage = findAverage;
		this.numRepetitions = numRepetitions;
	}


	public Integer call() {
		if (!findAverage) {
			return playGame();
		} else {
			double[] results = new double[numRepetitions];
			double sum = 0;
			for (int i = 0; i < numRepetitions; i++) { // compute sum
				results[i] = playGame();
				sum += results[i];
			}
			double mean =  sum / numRepetitions;
			double sumSqDiff = 0;
			for (int i = 0; i < numRepetitions; i++) { // compute std dev
				sumSqDiff += Math.pow((results[i] - mean), 2);
			}
			double score = sum / Math.sqrt(sumSqDiff); // compute final score (= sum / std dev)
			System.out.println("Weights: " + Arrays.toString(weightSets) + "\n" +
				"Results: " + Arrays.toString(results) + " = " + (int) score);
			return (int) score;
		}
    }

	/**
	* This method creates a new game for each given weight
	* @return number of rows cleared in this game using the given weight
	**/
	public int playGame() {
		PlayerSkeleton p = new PlayerSkeleton();
        State s = new State();
        while(!s.hasLost()) {
        	try {
            	s.makeMove(p.pickMove(s, s.legalMoves(), weightSets));
            } catch (Exception e) {
            	e.printStackTrace();
            	return -1;
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return s.getRowsCleared();
	}


}