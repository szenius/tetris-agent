
import java.util.Arrays;

public class PlayerSkeleton {

	//implement this function to have a working system
	public int pickMove(State s, int[][] legalMoves) {
		
		//Step 1. Get a copy of the current board configuration.
		int[][] currentField = copyArray(s.getField());

		//Step 2. Test all possible moves of the piece on the board and choose the move with the best heuristic score 
		//int best = tryPossibleMoves(currentField, legalMoves);

		return 0;
	}

	/**
	* This method test all possible moves of the piece and returns the move with the best heuristic score
	* @param currentField (current board config), legalMoves (current piece to place)
	* @return move with best (highest) heuristic score
	**/
	private int tryPossibleMoves(int[][] currentField, int[][] legalMoves) {
		//legalMoves = all possible moves of the current piece.
		//Step 1. Apply each move (action) to get the change field (board configuration).
		//Step 2. Apply Heuristic.evaluate(s) to get the heuristic score from the move.
		//Step 3. Get the best move.
		int bestScore = 0;
		int bestMove = 0;

		for(int i=0; i<legalMoves.length; i++) {
			//Step 1
			//int [][] updatedField = testMove(legalMoves[i]);
			//Step 2
			//int score = Heuristic.evaluate(updatedField, rowsCleared);
			//System.out.println("Heuristic score = " + Heuristic.evaluate(s));

			//Step 3
			//if(score > bestScore) {
			//	bestScore = score;
			//	bestMove = i;
			//}

		}
		
		return bestMove;
	}

	/**
	* This method gets the change field from testing the possible move of a piece. 
	* @param move
	* @return new board configuration after applying the move (action)
	**/
	private void testMove(int[] move) {
		int orient = move[0];
		int slot = move[1];
	}

	/**
	* This method prints current board configuration
	* @param currentField
	**/
	private void printField(int[][] currentField) {
		System.out.println("========Field==============");
		for(int i=0; i<currentField.length; i++) {
			for(int j=0; j<currentField[i].length; j++) {
				System.out.print(currentField[i][j]);
			}
			System.out.println();
		}
	}

	/**
	* This method makes a copy of an int array.
	* @param originalArray (array to copy)
	* @return newArray (deep copy of the array)
	**/
	private int[] copyArray(int[] originalArray) {
		if(originalArray == null) {
			return null;
		}

		int[] newArray = Arrays.copyOf(originalArray, originalArray.length);
		return newArray;
	}

	/**
	* This method makes a copy of an 2D int array.
	* @param originalArray (array to copy)
	* @return newArray (deep copy of the array)
	**/
	private int[][] copyArray(int[][] originalArray) {
		if(originalArray == null) {
			return null;
		}

		int[][] newArray = new int[originalArray.length][];
		for(int i=0; i<originalArray.length; i++) {
			newArray[i] = Arrays.copyOf(originalArray[i], originalArray[i].length);
		}
		return newArray;
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
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("You have completed "+s.getRowsCleared()+" rows.");
	}
	
}
