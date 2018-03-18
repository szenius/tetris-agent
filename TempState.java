import java.util.Arrays;

public class TempState extends State{
	public static final int COLS = 10;
	public static final int ROWS = 21;

	private int[] top;
	private int[][] field;
	private int[][] pWidth;
	private int[][] pHeight;
	private int[][][] pBottom;
	private int[][][] pTop;
	private int cleared;
	private int nextPiece;
	private int thisRoundCleared;
	private int stateOrient;
	private int stateSlot;

	public TempState(State s) {
		this.pBottom = s.getpBottom();
		this.pTop = s.getpTop();
		this.pHeight = s.getpHeight();
		this.pWidth = s.getpWidth();
		this.cleared = s.getRowsCleared();
		this.nextPiece = s.getNextPiece();
		this.top = copyArray(s.getTop());
		this.field = copyArray(s.getField());
	}

	public int[][] getField() {
		return this.field;
	}

	public int getRowsCleared() {
		return cleared;
	}

	public int getStateSlot () {
	    return stateSlot;
    }

    public int getHeightOfPeice() {
	    return pHeight[nextPiece][stateOrient];
    }

    public int getHeightOfCol(int slot) {
	    return top[slot];
    }

	//returns false if you lose - true otherwise
	public boolean makeMove(int orient, int slot) {
        //height if the first column makes contact
		int height = top[slot]-pBottom[nextPiece][orient][0];
		//for each column beyond the first in the piece
		for(int c = 1; c < pWidth[nextPiece][orient];c++) {
			height = Math.max(height,top[slot+c]-pBottom[nextPiece][orient][c]);
		}
		
		//check if game ended
		if(height+pHeight[nextPiece][orient] >= ROWS) {
			//lost = true;
			return false;
		}

		
		//for each column in the piece - fill in the appropriate blocks
		for(int i = 0; i < pWidth[nextPiece][orient]; i++) {
			
			//from bottom to top of brick
			for(int h = height+pBottom[nextPiece][orient][i]; h < height+pTop[nextPiece][orient][i]; h++) {
				field[h][i+slot] = 1;
			}
		}
		
		//adjust top
		for(int c = 0; c < pWidth[nextPiece][orient]; c++) {
			top[slot+c]=height+pTop[nextPiece][orient][c];
		}
		
		int rowsCleared = 0;
		
		//check for full rows - starting at the top
		for(int r = height+pHeight[nextPiece][orient]-1; r >= height; r--) {
			//check all columns in the row
			boolean full = true;
			for(int c = 0; c < COLS; c++) {
				if(field[r][c] == 0) {
					full = false;
					break;
				}
			}
			//if the row was full - remove it and slide above stuff down
			if(full) {
				rowsCleared++;				//for each column
				cleared++;
				for(int c = 0; c < COLS; c++) {

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

		thisRoundCleared = rowsCleared;

		return true;
	}

	public void putOrientAndSlot(int orient, int slot){
	    this.stateOrient = orient;
	    this.stateSlot = slot;
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
}