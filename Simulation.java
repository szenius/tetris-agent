import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

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
		ArrayList<Integer> r = new ArrayList<>();
		double sum = 0;
		System.out.print("[");
		for (int i = 0; i < numRepetitions; i++) { // compute sum
			results[i] = playGame();
			sum += results[i];
			r.add((int) results[i]);
			System.out.print(results[i] + ", ");
		}
		System.out.print("]\n");
		Collections.sort((r));
		return numRepetitions % 2 == 0 ? (int) (r.get(numRepetitions/2 + 1) + r.get(numRepetitions/2))/2 : r.get(numRepetitions/2);
		//return (int) results[0];
    }

	/**
	* This method creates a new game for each given weight
	* @return number of rows cleared in this game using the given weight
	**/
	public int playGame() {
		PlayerSkeleton p = new PlayerSkeleton();
        State s = new State();
        //new TFrame(s);
        while(!s.hasLost()) {
        	try {
            	s.makeMove(p.pickMove(s, s.legalMoves(), weightSets, h));
            	//s.draw();
            	//s.drawNext(0,0);
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