public class PlayerSkeleton {

	//implement this function to have a working system
	public int pickMove(State s, int[][] legalMoves) {
		//Test all possible moves of the piece on the board and choose the move with the best heuristic score 
		int best = tryPossibleMoves(s, legalMoves);

		return best;
	}

	/**
	* This method test all possible moves of the piece and returns the move with the best heuristic score
	* @param s (current state), legalMoves (possible moves for current piece)
	* @return move with best (highest) heuristic score
	**/
	private int tryPossibleMoves(State s, int[][] legalMoves) {
		//legalMoves = all possible moves of the current piece.
		//Step 1. Apply each move (action) to get the change field (board configuration).
		//Step 2. Apply Heuristic.evaluate(s) to get the heuristic score from the move.
		//Step 3. Get the best move.
		int bestScore = 0;
		int bestMove = 0;

		for(int i=0; i<legalMoves.length; i++) {
			TempState ts = new TempState(s);
			//Step 1
			int orient = legalMoves[i][0];
			int slot = legalMoves[i][1];
			boolean possible = ts.makeMove(orient, slot);
			if(!possible) {
				continue;
			} 
			//Step 2
			int score = Heuristic.evaluate(ts);

			//Step 3
			if(score > bestScore) {
				bestScore = score;
				bestMove = i;
			}
		}
		return bestMove;
	}
	
	public static void main(String[] args) {
		State s = new State();
		new TFrame(s);
		PlayerSkeleton p = new PlayerSkeleton();
		while(!s.hasLost()) {
			s.makeMove(p.pickMove(s,s.legalMoves()));
			s.draw();
			s.drawNext(0,0);
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("You have completed "+s.getRowsCleared()+" rows.");
	}
	
}
