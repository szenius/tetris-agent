public class HeuristicNew {
	public static final int NUM_FEATURES = 6;
	public static final boolean db = false; // set true to turn debug statements on
    public static final boolean db2 = false; // set true to debug rows eliminated (recommended set db = false)
	double[] weights;
	int weightCounter;
    TempState s;

    public HeuristicNew(TempState s, double[] inputWeights) {
        if(inputWeights == null) {
            this.weights = getDefaultWeights();
        } else {
            this.weights = inputWeights;
        }
        this.s = s;
        this.weightCounter = 0;
    }

    // TODO: either remove or read from file
    private static double[] getDefaultWeights() {
        double[] weights = new double[NUM_FEATURES];
        for (int i = 0; i < weights.length; i++) {
            weights[i] = 1;
        }
        return weights;
    }

	public double evaluate() {
		int[][] field = s.getField();
        if (db) s.printField(s.getField());

        int[] colHeights = colHeights(field);
        return weightedLandingHeight(s) + weightedRowsEliminated(s) + weightedNumRowTranstions(field) + weightedNumColTranstions(field)
                + wNumHoles(field, colHeights) + weightedWellSums(field);
	}

    private double weightedLandingHeight (TempState s) {
	    return getNextWeight() * landingHeight(s);
    }

    // Feature 1
    // Landing Height:
    // The height where the piece is put = the height of the column BEFORE piece is put + (the height of the piece / 2)
    // Also equivalent to height of column AFTER piece is put - (the height of the piece / 2) (?)
    private int landingHeight(TempState s) {

	    int heightPiece = s.getHeightOfPeice();
	    int landingHeight = s.getHeightOfCol(s.getStateSlot()) - heightPiece/2;

        if (db) System.out.println("landingHeight: " + landingHeight);
        return landingHeight;
    }

    // Feature 2
    private double weightedRowsEliminated (TempState s) {
	    int rowsEliminated = s.getRowsCleared() - s.getRowsPrevCleared();

        if (db2 && rowsEliminated > 0) {
            System.out.println(s.getRowsPrevCleared() +" --> " + s.getRowsCleared());
            System.out.println("rowsEliminated: " + rowsEliminated);
        }
        return getNextWeight() * rowsEliminated;
    }

    private double weightedNumRowTranstions(int[][] field) {
        return getNextWeight() * numRowTransitions(field);
    }

    // Feature 3
    // The total number of row transitions.
    // A row transition occurs when an empty cell is adjacent to a filled cell
    // on the same row and vice versa.
	private int numRowTransitions(int[][] field) {
	    int count = 0;
	    int numRows = field.length;
	    int numCols = field[0].length;
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols - 1; j++) {
                if (field[i][j] == 0 && field[i][j+1] != 0
                        || field[i][j] != 0 && field[i][j+1] == 0){
                    count++;
                }
            }
        }
        if (db) System.out.println("numRowTranstions: " + count);
        return count;
    }

    private double weightedNumColTranstions(int[][] field) {
        return getNextWeight() * numColTransitions(field);
    }

    // Feature 4
    // The total number of column transitions.
    // A column transition occurs when an empty cell is adjacent to a filled cell
    // on the same column and vice versa.
    private int numColTransitions(int[][] field) {
        int count = 0;
        int numRows = field.length;
        int numCols = field[0].length;
        for (int j = 0; j < numCols; j++) {
            for (int i = 0; i < numRows - 1; i++) {
                if (field[i][j] == 0 && field[i+1][j] != 0
                        || field[i][j] != 0 && field[i+1][j] == 0){
                    count++;
                }
            }
        }
        if (db) System.out.println("numColTranstions: " + count);
        return count;
    }

    // Returns array of column heights
    private int[] colHeights(int[][] field) {
        int[] colHeights = new int[field[0].length];

        // System.out.print("Col heights: ");

        for (int col = 0; col < field[0].length; col++) {
            int row = field.length - 1;

            while (row >= 0 && field[row][col] == 0) {
                row--;
            }
            colHeights[col] = row + 1;
            // System.out.print(colHeights[col] + " ");
        }
        // System.out.println();

        return colHeights;
    }

    // Feature 5
    // Returns weighted number of holes
    // A hole is an empty cell that has at least one filled cell above it in the same column.
    // Example
    // 100
    // 110
    // 100
    // === is still 1 hole!
    private double wNumHoles(int[][] field, int[] colHeights) {
        int numHoles = 0;

        for (int col = 0; col < colHeights.length; col++) {
            for (int row = colHeights[col] - 1; row >= 0; row--) {
                if (field[row][col] == 0) {
                    numHoles++;
                }
            }
        }

        if (db) System.out.println("numHoles: " + numHoles);
        return getNextWeight() * numHoles;
    }

    // Feature 6
    private double weightedWellSums(int[][] field) {
        return getNextWeight() * wellSums(field);
    }

    // A well is a succession of empty cells such that their left cells and right cells are both filled.
    // Example:
    // |1101100010|
    // |1101101010|
    // |1101101000| | denotes left/right edge border
    // returns 8
    private int wellSums(int[][] field) {
        int count = 0;
        int numRows = field.length;
        int numCols = field[0].length;
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                if (field[i][j] == 0){ // if cell is empty
                        if (j > 0 && j < numCols - 1 && field[i][j - 1] != 0 && field[i][j + 1] != 0) {
                            count += exploreWell(i, j, field);
                        } else if (j == 0 && field[i][j+1] !=0) {
                            count += exploreWellLeftSide(i, field);
                        } else if (j == numCols - 1 && field[i][j-1] != 0){
                            count += exploreWellRightSide(i, field);
                        }
                }

            }
        }

        if (db) System.out.println("Overall Well sum: " + count);
        return count;
    }

    // explore up the rows within the column
    private int exploreWell(int startRow, int startColumn, int[][] field) {
	    // if this empty column is part of an existing well. Don't bother
	    if (startRow != 0
                && field[startRow-1][startColumn] == 0
                && field[startRow-1][startColumn - 1] != 0
                && field[startRow-1][startColumn + 1] != 0) {
            return 0;
        }

	    int count = 0;
	    while(field[startRow][startColumn] == 0
                && field[startRow][startColumn-1] != 0
                && field[startRow][startColumn+1] != 0) {
	        count++;
	        startRow++;
        }
        if (field[startRow][startColumn] != 0) {
            //System.out.println("Not a well!");
            return 0;
        }
        //System.out.println("Normal Well sum: " + sumFromOneToN(count));
	    return sumFromOneToN(count);
    }

    // explore up the rows within the column for wells by the left wall
    private int exploreWellLeftSide(int startRow, int[][] field) {
        // if this empty column is part of an existing well. Don't bother
        if (startRow != 0
                && field[startRow-1][0] == 0
                && field[startRow-1][1] != 0) {
            return 0;
        }

        int count = 0;
        while(field[startRow][0] == 0
                && field[startRow][1] != 0) {
            count++;
            startRow++;
        }
        if (field[startRow][0] != 0) {
            //System.out.println("Not a well!");
            return 0;
        }
        //System.out.println("Left Well sum: " + sumFromOneToN(count));
        return sumFromOneToN(count);
    }

    // explore up the rows within the column for wells by the right wall
    private int exploreWellRightSide(int startRow, int[][] field) {
        int numCols = field[0].length;
	    // if this empty column is part of an existing well. Don't bother
        if (startRow != 0
                && field[startRow-1][numCols - 1] == 0
                && field[startRow-1][numCols - 2] != 0) {
            return 0;
        }

        int count = 0;
        while(field[startRow][numCols - 1] == 0
                && field[startRow][numCols - 2] != 0) {
            count++;
            startRow++;
        }
        if (field[startRow][numCols -1] != 0) {
            //System.out.println("Not a well!");
            return 0;
        }
        //System.out.println("Right Well sum: " + sumFromOneToN(count));
        return sumFromOneToN(count);
    }

    // Given an N returns sum From One to N.
    // eg: Input N. Ouput 1 + 2 + 3 + 4 + 5.
    // Uses Sum of AP.
    private int sumFromOneToN(int N) {
	    return (N*(N+1))/2;
    }

	private double getNextWeight() {
		return weights[weightCounter++];
	}

    public static int getNumFeatures() {
        return NUM_FEATURES;
    }
}