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
        return s.getRowsCleared();
	}


}