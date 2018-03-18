public class HeuristicNew {
	public static final int NUM_FEATURES = 6;
	static int[] weights;
	static int weightCounter;

	public static int evaluate(TempState s) {
		weightCounter = 0;
		readWeights();
		int[][] field = s.getField();

        System.out.println("============================");
        // print field
         for (int i = 0; i < field.length; i++) {
         	for (int j = 0; j < field[0].length; j++) {
         		System.out.print(field[i][j] + " ");
         	}
         	System.out.println();
         }
        System.out.println();
        wellSums(field);
        System.out.println("============================");

		//return s.getRowsCleared() + wSumColHeight(colHeights) + wSumColDiff(colHeights)
		//		+ wMaxColHeight(colHeights) + wNumHoles(field, colHeights);

        int[] colHeights = colHeights(field);
        return weightedLandingHeight(s) + weightedRowsEliminated(s.getRowsCleared()) + weightedNumRowTranstions(field) + weightedNumColTranstions(field)
                + wNumHoles(field, colHeights) + weightedWellSums(field);
	}

	// TODO: This method
    private static int weightedLandingHeight (TempState s) {
	    return getNextWeight() * landingHeight(s);
    }

    // TODO: This method
    // Landing Height:
    // The height where the piece is put =
    // the height of the column + (the height of the piece / 2)
    private static int landingHeight(TempState s) {

	    int heightColumn = 0;
	    int heightPiece = 0;
	    int landingHeight = heightColumn + heightPiece / 2;

	    return landingHeight;
    }


    private static int weightedRowsEliminated (int rowsCleared) {
	    return getNextWeight() * rowsCleared;
    }

    // TODO: read from file instead
	private static void readWeights() {
		weights = new int[NUM_FEATURES];
		for (int i = 0; i < weights.length; i++) {
			weights[i] = 1;
		}
	}

    private static int weightedNumRowTranstions(int[][] field) {
        return getNextWeight() * numRowTransitions(field);
    }

    // The total number of row transitions.
    // A row transition occurs when an empty cell is adjacent to a filled cell
    // on the same row and vice versa.
	private static int numRowTransitions(int[][] field) {
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
        return count;
    }

    private static int weightedNumColTranstions(int[][] field) {
        return getNextWeight() * numColTransitions(field);
    }

    // The total number of column transitions.
    // A column transition occurs when an empty cell is adjacent to a filled cell
    // on the same column and vice versa.
    private static int numColTransitions(int[][] field) {
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
        return count;
    }

    private static int weightedWellSums(int[][] field) {
        return getNextWeight() * numColTransitions(field);
    }

    // A well is a succession of empty cells such that their left cells and right cells are both filled.
    // Example:
    // 1101100010
    // 1101101010
    // 1101101000
    // returns 8
    private static int wellSums(int[][] field) {
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
        //System.out.println("Overall Well sum: " + count);
        return count;
    }

    // explore up the rows within the column
    private static int exploreWell(int startRow, int startColumn, int[][] field) {
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
        //System.out.println("Normal Well sum: " + count);
	    return count;
    }

    // explore up the rows within the column for wells by the left wall
    private static int exploreWellLeftSide(int startRow, int[][] field) {
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
        //System.out.println("Left Well sum: " + count);
        return count;
    }

    // explore up the rows within the column for wells by the right wall
    private static int exploreWellRightSide(int startRow, int[][] field) {
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
        //System.out.println("Right Well sum: " + count);
        return count;
    }

    // Returns array of column heights
	private static int[] colHeights(int[][] field) {
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

    // Returns weighted number of holes
    private static int wNumHoles(int[][] field, int[] colHeights) {
        int numHoles = 0;

        for (int col = 0; col < colHeights.length; col++) {
            for (int row = colHeights[col] - 1; row >= 0; row--) {
                if (field[row][col] == 0) {
                    numHoles++;
                }
            }
        }

        // System.out.println("Number of holes = " + numHoles);

        return getNextWeight() * numHoles;
    }

	private static int getNextWeight() {
		return weights[weightCounter++];
	}
}