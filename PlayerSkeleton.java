import java.util.Arrays;

public class PlayerSkeleton {

	//implement this function to have a working system
	public int pickMove(State s, int[][] legalMoves) {
		// TODO before submission:
		// Create a Heuristic object with the chosen features and also input the chosen weights

		// e.g. for old feature set:
		// return pickMove(s, legalMoves, new double[]{1.2833875706457385, -1.3177491117863123, 2.406380632946737, 
		// 	-6.409626812577342, -10.0, -10.0, -1.6042204302840832, -10.0, -10.0, -0.10680704480852654, 7.583569921869946, 
		// 	-1.4479169546185844, 3.012421751232105, 1.9260563707132148, -7.002885893784303, 10.0, 10.0, -10.0, -10.0, 
		// 	7.320092035545837, -1.6795947290151112, -10.0}, new Heuristic(true, false));

		// e.g. for new feature set:
		// return pickMove(s, legalMoves, new double[]{10.0, -10.0, -7.631455394594079, 2.1425255943282537, 0.19485158741367403, 
		// 	-4.781899234177633}, new Heuristic(false, true));
		
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
