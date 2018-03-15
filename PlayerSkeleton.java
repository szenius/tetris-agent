
import java.util.Arrays;

public class PlayerSkeleton {

	//implement this function to have a working system
	public int pickMove(State s, int[][] legalMoves) {
		
		//Step 1. Get a copy of the current board configuration.
		int[][] currentField = copyArray(s.getField());

		//Step 2. Test all possible moves of the piece on the board and choose the move with the best heuristic score 
		tryPossibleMoves(currentField, legalMoves);
		//System.out.println("Heuristic score = " + Heuristic.evaluate(s) + " --> ");

		return 0;
	}

	/**
	* This method test all possible moves of the piece and returns the move with the best heuristic score
	* @param currentField (current board config), legalMoves (current piece to place)
	* @return move with best (highest) heuristic score
	**/
	private void tryPossibleMoves(int[][] currentField, int[][] legalMoves) {
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
			newArray[i] = Arrays.copyOf(originalArray[i], originalArray.length);
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
