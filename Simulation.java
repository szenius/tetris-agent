import java.util.concurrent.Callable;
import java.util.Arrays;

import java.util.logging.Logger;
import java.util.logging.Level;

class Simulation implements Callable<Integer> {
	private Heuristic h;
	private double[] weightSets;
	private int numRepetitions;

	private int[][][] legalMovesFor8Col = new int[7][][];
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
			results[i] = playGame();
			sum += results[i];
		}
		double mean =  sum / numRepetitions;
		double sumSqDiff = 0;
		for (int i = 0; i < numRepetitions; i++) { // compute std dev
			sumSqDiff += Math.pow((results[i] - mean), 2);
		}
		double score = sum / Math.sqrt(sumSqDiff / numRepetitions) * 1000; // compute final score (= sum / std dev * 1000)
		//System.out.println("Weights: " + Arrays.toString(weightSets) + "\n" +
			//"Results: " + Arrays.toString(results) + " = " + (int) score + "; average = " + (int) mean);
		//LOGGER.info("Weights: " + Arrays.toString(weightSets) + "\n" +
		//	"Results: " + Arrays.toString(results) + " = " + (int) score + "; average = " + (int) mean);
		return (int) score;
    }

	/**
	* This method creates a new game for each given weight
	* @return number of rows cleared in this game using the given weight
	**/
	public int playGame() {
		PlayerSkeleton p = new PlayerSkeleton();
        State s = new State();
        //generateLegalMoves(s);
        //TFrame f = new TFrame(s);
        while(!s.hasLost()) {
        	try {
            	s.makeMove(p.pickMove(s, s.legalMoves(), weightSets, h));
            	//int bestMove = p.pickMove(s, legalMovesFor8Col[s.getNextPiece()], weightSets, h);
            	//s.makeMove(legalMovesFor8Col[s.getNextPiece()][bestMove]);
            	//s.draw();
            	//s.drawNext(0, 0);
            } catch (Exception e) {
            	e.printStackTrace();
            	LOGGER.log(Level.SEVERE, "an exception was thrown" , e);
            	return -1;
            }
            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
                LOGGER.log(Level.SEVERE, "an exception was thrown" , e);
            }
        }
        //f.dispose();
        return s.getRowsCleared();
	}

	public void generateLegalMoves(State s) {
		int orient = 0;
		int slot = 1;
		int col = 8;
		int[][] pWidth = s.getpWidth();
		int[] pOrients = s.getpOrients();

		//for each piece type
		for(int i = 0; i < 7; i++) {
			//figure number of legal moves
			int n = 0;
			for(int j = 0; j < pOrients[i]; j++) {
				//number of locations in this orientation
				n += col+1-pWidth[i][j];
			}
			//allocate space
			legalMovesFor8Col[i] = new int[n][2];
			//for each orientation
			n = 0;
			for(int j = 0; j < pOrients[i]; j++) {
				//for each slot
				for(int k = 0; k < col+1-pWidth[i][j];k++) {
					legalMovesFor8Col[i][n][orient] = j;
					legalMovesFor8Col[i][n][slot] = k;
					n++;
				}
			}
		}
	}

}