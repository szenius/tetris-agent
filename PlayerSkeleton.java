import java.util.Arrays;

public class PlayerSkeleton {

	//implement this function to have a working system
	public int pickMove(State s, int[][] legalMoves) {
		return pickMove(s, legalMoves, null, null);
	}

	public int pickMove(State s, int[][] legalMoves, double[] weights) {
		//Test all possible moves of the piece on the board and choose the move with the best heuristic score 
		int best = tryPossibleMoves(s, legalMoves, weights, null);

		return best;
	}

	public int pickMove(State s, int[][] legalMoves, double[] weights, Heuristic h) {
		//Test all possible moves of the piece on the board and choose the move with the best heuristic score 
		int best = tryPossibleMoves(s, legalMoves, weights, h);

		return best;
	}

	/**
	* This method test all possible moves of the piece and returns the move with the best heuristic score
	* @param s (current state), legalMoves (possible moves for current piece)
	* @return move with best (highest) heuristic score
	**/
	private int tryPossibleMoves(State s, int[][] legalMoves, double[] weights, Heuristic h) {

		// By default use new set of features
		if (h == null) {
			h = new Heuristic(false, true);
		}

		//legalMoves = all possible moves of the current piece.
		//Step 1. Apply each move (action) to get the change field (board configuration).
		//Step 2. Apply Heuristic.evaluate(s) to get the heuristic score from the move.
		//Step 3. Get the best move.
		double bestScore = Double.NEGATIVE_INFINITY;
		int bestMove = 0;

		for(int i=0; i<legalMoves.length; i++) {
			TempState ts = new TempState(s);
			int prevCleared = s.getRowsCleared();
			//Step 1
			int orient = legalMoves[i][0];
			int slot = legalMoves[i][1];
			boolean possible = ts.makeMove(orient, slot);
			if(!possible) {
				continue;
			} 
			//Step 2
            ts.setPrevCleared(prevCleared);
			ts.setOrientAndSlot(orient, slot);
			Evaluator e = new Evaluator(h, ts, weights);
			double score = e.evaluate(); 

			//Step 3
			if(score > bestScore) {
				bestScore = score;
				bestMove = i;
			}
		}
		return bestMove;
	}
	
	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		State s = new State();
		new TFrame(s);
		PlayerSkeleton p = new PlayerSkeleton();
		// double[] weights = new double[]{-7.98667, -2.92195, -2.24357, -10.0, -10.0, -4.81467};
		// double[] weights = {-0.4944712922445228, 0.14814799954876456, -0.9250354407343329, -1.210228068000284, -0.6363751749245313, -0.3071493143829803,
		// 		0.4062421734216026, 0.4053785630482645, 0.7203562102658518, 0.6458317403226688, 0.33541924448498417,
		// 		0.156339060819642, 1.2098194382476808, -0.12143020086913736, 1.5266470763709517, 0.5760539680303807,
		// 		0.11214199088469634, -0.2072277305271486, 1.2884998585410108, 0.9616177885473564, 0.13438465406024958, 1.1912072595739716};
		while(!s.hasLost()) {
			s.makeMove(p.pickMove(s, s.legalMoves()));
			s.draw();
			s.drawNext(0,0);
			try {
				Thread.sleep(0);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("You have completed "+s.getRowsCleared()+" rows in " + (end - start)/1e3 + " seconds.");
		// System.out.println("You have completed "+s.getRowsCleared()+" rows.");
	}
	/*
	public static void main(String[] args) {
		Genetic gen = new Genetic();
		//System.out.println("You have completed "+s.getRowsCleared()+" rows.");
	}
	*/
	// public static void main(String[] args) {
	// 	System.out.println("Started");
	// 	GeneticAlgo gen = new GeneticAlgo();
	// 	//System.out.println("You have completed "+s.getRowsCleared()+" rows.");
	// 	System.out.println("Done");
	// }

}
