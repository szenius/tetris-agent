import java.util.concurrent.Callable;
import java.util.Arrays;

class Simulation implements Callable<Integer> {
	private static final boolean USE_MEAN = false;

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
			int result = playGame();
			while (result == -1) {
				result = playGame();
			}
			results[i] = result;
			sum += results[i];
		}
		double mean =  sum / numRepetitions;
		Arrays.sort(results);
		double mean = sum / numRepetitions;
		double median = results[numRepetitions/2];
		if (USE_MEAN) score = mean;
		else score = median;
		System.out.println("Weights: " + Arrays.toString(weightSets) + "\n" +
			"Results: " + Arrays.toString(results) + "; " +
			"Median (" + !USE_MEAN + ") = " + median + "; Mean (" + USE_MEAN + ") = " + mean);
		return score;

		// double sumSqDiff = 0;
		// for (int i = 0; i < numRepetitions; i++) { // compute std dev
		// 	sumSqDiff += Math.pow((results[i] - mean), 2);
		// }
		// double score = (mean * 1000) / Math.sqrt(sumSqDiff / numRepetitions); // compute final score (= sum / std dev * 1000)
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