import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

class Simulation implements Callable<Integer> {
	private static final boolean USE_MEAN = false;

	private Heuristic h;
	private double[] weightSets;
	private int numRepetitions;

	private Logger LOGGER = Logging.getInstance();

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
			while(result == -1) {
				result = playGame();
			}
			results[i] = result;
			sum += results[i];
		}
		Arrays.sort(results);
		// Compute mean
		double mean = sum / numRepetitions;
		// Compute median
		double median = 0;
		if (numRepetitions%2==0) median = 0.5*(results[numRepetitions/2] + results[(numRepetitions/2)+1]);
		else median = results[numRepetitions/2];

		double score = 0;
		if (USE_MEAN) score = mean;
		else score = median; 
		
		System.out.println("Weights: " + Arrays.toString(weightSets) + "\n" +
			"Results: " + Arrays.toString(results) + "; " +
			"Median (" + !USE_MEAN + ") = " + median + "; Mean (" + USE_MEAN + ") = " + mean);

		return (int) score;
    }

	/**
	* This method creates a new game for each given weight
	* @return number of rows cleared in this game using the given weight
	**/
	public int playGame() {
		PlayerSkeleton p = new PlayerSkeleton();
        State s = new State();
        TFrame f = new TFrame(s);
        while(!s.hasLost()) {
        	try {
            	s.makeMove(p.pickMove(s, s.legalMoves(), weightSets, h));
            	s.draw();
            	s.drawNext(0, 0);
            } catch (Exception e) {
            	e.printStackTrace();
            	//LOGGER.log(Level.SEVERE, "an exception was thrown" , e);
            	return -1;
            }
            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
                //LOGGER.log(Level.SEVERE, "an exception was thrown" , e);
            }
        }
        //f.dispose();
        return s.getRowsCleared();
	}
}