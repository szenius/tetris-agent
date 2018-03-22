import java.util.concurrent.Callable;

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
			int sum = 0;
			for (int i = 0; i < numRepetitions; i++) {
				sum += playGame();
			}
			return sum/numRepetitions;
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