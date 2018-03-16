
import java.util.Arrays;

public class PlayerSkeleton {

	private int[][] pWidth;
	private int[][] pHeight;
	private int[][][] pBottom;
	private int[][][] pTop;

	//implement this function to have a working system
	public int pickMove(State s, int[][] legalMoves) {
		
		//Step 1. Get a copy of the current board configuration.
		int[][] currentField = copyArray(s.getField());

		//Step 2. Test all possible moves of the piece on the board and choose the move with the best heuristic score 
		int best = tryPossibleMoves(currentField, legalMoves, s);

		return best;
	}

	/**
	* This method test all possible moves of the piece and returns the move with the best heuristic score
	* @param currentField (current board config), legalMoves (possible moves for current piece), s (current board configuration)
	* @return move with best (highest) heuristic score
	**/
	private int tryPossibleMoves(int[][] currentField, int[][] legalMoves, State s) {
		//legalMoves = all possible moves of the current piece.
		//Step 1. Apply each move (action) to get the change field (board configuration).
		//Step 2. Apply Heuristic.evaluate(s) to get the heuristic score from the move.
		//Step 3. Get the best move.
		int bestScore = 0;
		int bestMove = 0;

		for(int i=0; i<legalMoves.length; i++) {
			//Step 1
			int [][] field = copyArray(currentField);
			int[] top = copyArray(s.getTop());
			Pair results = testMove(legalMoves[i], field, s.getNextPiece(), top);
			//Step 2
			if(results == null) {
				continue;
			}
			int score = Heuristic.evaluate(results.getField(), results.getRowsCleared());
			//System.out.println("Heuristic score = " + Heuristic.evaluate(s));

			//Step 3
			if(score > bestScore) {
				bestScore = score;
				bestMove = i;
			}

		}
		return bestMove;
	}

	/**
	* This method gets the change field from testing the possible move of a piece. 
	* @param move (action), field (current board configuration), currentPiece (current piece to place), top (?)
	* @return new board configuration after applying the move (action)
	**/
	private Pair testMove(int[] move, int[][] field, int currentPiece, int[] top) {
		int orient = move[0];
		int slot = move[1];
		int row = 21;
		int col = 10;

		//height if the first column makes contact
		int height = top[slot]-pBottom[currentPiece][orient][0];
		//for each column beyond the first in the piece
		for(int c = 1; c < pWidth[currentPiece][orient];c++) {
			height = Math.max(height,top[slot+c]-pBottom[currentPiece][orient][c]);
		}
		
		//Check if move is valid
		if(height+pHeight[currentPiece][orient] >= row) {
			return null;
		}

		//for each column in the piece - fill in the appropriate blocks
		for(int i = 0; i < pWidth[currentPiece][orient]; i++) {
			//from bottom to top of brick
			for(int h = height+pBottom[currentPiece][orient][i]; h < height+pTop[currentPiece][orient][i]; h++) {
				field[h][i+slot] = 1;
			}
		}
		
		//adjust top
		for(int c = 0; c < pWidth[currentPiece][orient]; c++) {
			top[slot+c]=height+pTop[currentPiece][orient][c];
		}
		
		int rowsCleared = 0;
		
		//check for full rows - starting at the top
		for(int r = height+pHeight[currentPiece][orient]-1; r >= height; r--) {
			//check all columns in the row
			boolean full = true;
			for(int c = 0; c < col; c++) {
				if(field[r][c] == 0) {
					full = false;
					break;
				}
			}
			//if the row was full - remove it and slide above stuff down
			if(full) {
				rowsCleared++;
				//for each column
				for(int c = 0; c < col; c++) {

					//slide down all bricks
					for(int i = r; i < top[c]; i++) {
						field[i][c] = field[i+1][c];
					}
					//lower the top
					top[c]--;
					while(top[c]>=1 && field[top[c]-1][c]==0) {
						top[c]--;
					}
				}
			}
		}

		return new Pair(field, rowsCleared);
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
	* This method gets the information about a piece's shape from state s.
	* @Param s (current board configuration)
	**/
	private void getPieceInformation(State s) {
		pBottom = s.getpBottom();
		pTop = s.getpTop();
		pHeight = s.getpHeight();
		pWidth = s.getpWidth();
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

	/**
	* This method makes a copy of an 3D int array.
	* @param originalArray (array to copy)
	* @return newArray (deep copy of the array)
	**/
	private int[][][] copyArray(int[][][] originalArray) {
		if(originalArray == null) {
			return null;
		}

		int[][][] newArray = new int[originalArray.length][][];
		for(int i=0; i<originalArray.length; i++) {
			newArray[i] = new int[originalArray[i].length][];
			for(int j=0; j<originalArray[i].length; j++) {
				newArray[i][j] = Arrays.copyOf(originalArray[i][j], originalArray[i][j].length);
			}
		}
		return newArray;
	}
	
	public static void main(String[] args) {
		State s = new State();
		new TFrame(s);
		PlayerSkeleton p = new PlayerSkeleton();
		p.getPieceInformation(s);
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
