import java.util.Arrays;
import java.util.concurrent.Callable;

class Simulation implements Callable<Integer> {
	private double[] weightSets;

	public Simulation() {
		weightSets = null;
	}

	public Simulation(double[] weightSets) {
		this.weightSets = weightSets;
	}


	public Integer call() {
		return playGame();
    }

	/**
	* This method creates a new game for each given weight
	* @return number of rows cleared in this game using the given weight
	**/
	public int playGame() {
		PlayerSkeleton p = new PlayerSkeleton();
        State s = new State();
        while(!s.hasLost()) {
            s.makeMove(p.pickMove(s, s.legalMoves(), weightSets));
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
		System.out.println("For " + generatePositionKey(weightSets) + ": " + s.getRowsCleared());
        return s.getRowsCleared();
	}

	// Return string version of array of weights as String or key for use in Maps
	private String generatePositionKey(double[] position) {
		return Arrays.toString(position);
	}

}