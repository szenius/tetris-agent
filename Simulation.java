import java.util.concurrent.Callable;
import java.util.Arrays;

import java.util.logging.Logger;
import java.util.logging.Level;

class Simulation implements Callable<Integer> {
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
		//double sum = 0;
		for (int i = 0; i < numRepetitions; i++) { // compute sum
			results[i] = playGame();
			//sum += results[i];
		}
		Arrays.sort(results);
		int middle = numRepetitions / 2;
		double score = 0;
		if(numRepetitions % 2 == 0) {
			score = (results[middle-1] + results[middle]) / 2;
		} else {
			score = results[middle];
		}
		
		//double mean =  sum / numRepetitions;
		//double sumSqDiff = 0;
		//for (int i = 0; i < numRepetitions; i++) { // compute std dev
		//	sumSqDiff += Math.pow((results[i] - mean), 2);
		//}
		//double score = sum / Math.sqrt(sumSqDiff / numRepetitions) * 1000; // compute final score (= sum / std dev * 1000)
		//System.out.println("Weights: " + Arrays.toString(weightSets) + "\n" +
			//"Results: " + Arrays.toString(results) + " = " + (int) score + "; average = " + (int) mean);
		
		//LOGGER.info("Weights: " + Arrays.toString(weightSets) + " ==> " +
		//	"Results: " + Arrays.toString(results) + " = " + (int) score);
		return (int) score;
    }

	/**
	* This method creates a new game for each given weight
	* @return number of rows cleared in this game using the given weight
	**/
	public int playGame() {
		PlayerSkeleton p = new PlayerSkeleton();
        State s = new State();
        //TFrame f = new TFrame(s);
        while(!s.hasLost()) {
        	try {
            	s.makeMove(p.pickMove(s, s.legalMoves(), weightSets, h));
            	//s.draw();
            	//s.drawNext(0, 0);
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