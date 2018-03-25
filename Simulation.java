import java.util.concurrent.Callable;
import java.util.Arrays;

class Simulation implements Callable<Integer> {
	private Heuristic h;
	private double[] weightSets;
	private int numRepetitions;

	public Simulation() {
		h = new Heuristic(false, true);
		weightSets = null;
		numRepetitions = 1;
	}

	public Simulation(double[] weightSets) {
		h = new Heuristic(false, true);
		this.weightSets = weightSets;
		this.numRepetitions = 1;
	}

	public Simulation(Heuristic h, double[] weightSets, int numRepetitions) {
		this.h = h;
		this.weightSets = weightSets;
		this.numRepetitions = numRepetitions;
	}


	public Integer call() {
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
		double score = (mean * 1000) / (Math.sqrt(sumSqDiff / numRepetitions) * 0.05); // compute final score (= sum / std dev * 1000)
		System.out.println("Weights: " + Arrays.toString(weightSets) + "\n" +
			"Results: " + Arrays.toString(results) + " = " + (int) score + "; average = " + (int) mean);
		// return (int) score;
		return (int) mean;
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
            	s.makeMove(p.pickMove(s, s.legalMoves(), weightSets, h));
            } catch (Exception e) {
            	e.printStackTrace();
            	return -1;
            }
            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return s.getRowsCleared();
	}


}