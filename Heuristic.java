public class Heuristic {
	private static final int TOTAL_NUM_FEATURES = 9;

	// Indices for each feature to keep track of which feature to use
	private static final int INDEX_ROWS_CLEARED = 0;
	private static final int INDEX_COL_HEIGHT = 1;
	private static final int INDEX_COL_HEIGHT_DIFF = 2;
	private static final int INDEX_MAX_COL_HEIGHT = 3;
	private static final int INDEX_NUM_HOLES = 4;
	private static final int INDEX_LANDING_HEIGHT = 5;
	private static final int INDEX_ROW_TRANSITIONS = 6;
	private static final int INDEX_COL_TRANSITIONS = 7;
	private static final int INDEX_WELL_SUMS = 8;
	private static final int[] INDEX_OLD_FEATURES = new int[]{INDEX_ROWS_CLEARED, INDEX_COL_HEIGHT, INDEX_COL_HEIGHT_DIFF,
		INDEX_MAX_COL_HEIGHT, INDEX_NUM_HOLES};
	private static final int[] INDEX_NEW_FEATURES = new int[]{INDEX_ROWS_CLEARED, INDEX_NUM_HOLES, INDEX_LANDING_HEIGHT, 
		INDEX_ROW_TRANSITIONS, INDEX_COL_TRANSITIONS, INDEX_WELL_SUMS};

	// Flags for features
	private char[] featureFlags;
	private int numFeatures;
	private double[] weights;
	private int weightCounter;
	private TempState s;
	private int[] colHeights;

    public Heuristic(boolean useOldFeatures, boolean useNewFeatures) {
        this.weightCounter = 0;
        this.featureFlags = new char[TOTAL_NUM_FEATURES];
        setNumFeatures(useOldFeatures, useNewFeatures);
    }

 	public Heuristic(boolean useOldFeatures, boolean useNewFeatures, String featureBits) {
        this.weightCounter = 0;
        this.featureFlags = featureBits.toCharArray();
        setNumFeatures(useOldFeatures, useNewFeatures);
    }
    
	public double evaluate(TempState s, double[] inputWeights) {
		this.s = s;
		this.weights = inputWeights;
		int[][] field = s.getField();
		this.colHeights = colHeights(field);

		return weightedNumRowsCleared(s.getRowsCleared()) 
				+ weightedSumColHeight(colHeights) 
				+ weightedSumColDiff(colHeights) 
				+ weightedMaxColHeight(colHeights) 
				+ weightedNumHoles(field, colHeights) 
				+ weightedLandingHeight(s) 
				+ weightedNumRowTranstions(field) 
				+ weightedNumColTranstions(field)
                + weightedWellSums(field);
	}


	/** METHODS TO COMPUTE FEATURE SUM **/
	// Returns weighted number of rows cleared by playing this move
	private double weightedNumRowsCleared(int rowsCleared) {
		if (featureFlags[INDEX_ROWS_CLEARED] == '0') {
			return 0;
		}
	    int rowsEliminated = s.getRowsCleared() - s.getRowsPrevCleared();
	    return getNextWeight() * rowsEliminated;
	}

	// Returns weighted sum of column heights
	private double weightedSumColHeight(int[] colHeights) {
		if (featureFlags[INDEX_COL_HEIGHT] == '0') {
			return 0;
		}

		double weightedSum = 0;

		// iterate through columns, count height for each col
		for (int i = 0; i < colHeights.length; i++) {
			weightedSum += getNextWeight() * colHeights[i];
		}

		return weightedSum;
	}

	// Returns weighted sum of absolute differences between adjacent columns
	private double weightedSumColDiff(int[] colHeights) {
		if (featureFlags[INDEX_COL_HEIGHT_DIFF] == '0') {
			return 0;
		}

		double weightedSum = 0;

		for (int i = 1; i < colHeights.length; i++) {
			weightedSum += getNextWeight() * Math.abs(colHeights[i] - colHeights[i-1]);
		}
		return weightedSum;
	}

	// Returns weighted max col
	private double weightedMaxColHeight(int[] colHeights) {
		if (featureFlags[INDEX_MAX_COL_HEIGHT] == '0') {
			return 0;
		}

		int max = 0;

		for (int i = 0; i < colHeights.length; i++) {
			if (colHeights[i] > max) {
				max = colHeights[i];
			}
		}

		return getNextWeight() * max;
	}

	// Returns weighted number of holes
	private double weightedNumHoles(int[][] field, int[] colHeights) {
		if (featureFlags[INDEX_NUM_HOLES] == '0') {
			return 0;
		}

		int numHoles = 0;

		for (int col = 0; col < colHeights.length; col++) {
			for (int row = colHeights[col] - 1; row >= 0; row--) {
				if (field[row][col] == 0) {
					numHoles++;
				}
			}
		}

		return getNextWeight() * numHoles;
	}

    private double weightedLandingHeight (TempState s) {
		if (featureFlags[INDEX_LANDING_HEIGHT] == '0') {
			return 0;
		}

	    return getNextWeight() * landingHeight(s);
    }

    // Landing Height:
    // The height where the piece is put = the height of the column BEFORE piece is put + (the height of the piece / 2)
    // Also equivalent to height of column AFTER piece is put - (the height of the piece / 2) (?)
    private int landingHeight(TempState s) {

	    int heightPiece = s.getHeightOfPeice();
	    int landingHeight = s.getHeightOfCol(s.getStateSlot()) - heightPiece/2;

        return landingHeight;
    }

    private double weightedNumRowTranstions(int[][] field) {
		if (featureFlags[INDEX_ROW_TRANSITIONS] == '0') {
			return 0;
		}

        return getNextWeight() * numRowTransitions(field);
    }

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
        return count;
    }

    private double weightedNumColTranstions(int[][] field) {
		if (featureFlags[INDEX_COL_TRANSITIONS] == '0') {
			return 0;
		}

        return getNextWeight() * numColTransitions(field);
    }

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
        return count;
    }

    private double weightedWellSums(int[][] field) {
		if (featureFlags[INDEX_WELL_SUMS] == '0') {
			return 0;
		}

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
            return 0;
        }
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
            return 0;
        }
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
            return 0;
        }
        return sumFromOneToN(count);
    }

	/** HELPER METHODS **/
	// Returns array of column heights
	private int[] colHeights(int[][] field) {
		int[] colHeights = new int[field[0].length];

		for (int col = 0; col < field[0].length; col++) {
			int row = field.length - 1;

			while (row >= 0 && field[row][col] == 0) {
				row--;
			}
			colHeights[col] = row + 1;
		}

		return colHeights;
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

	private void setFixedFeatures(int[] indices, boolean isUse) {
		if (isUse) {
			for (int i = 0; i < indices.length; i++) {
				featureFlags[indices[i]] = '1';
			}
		}
	}

	public void setNumFeatures(boolean useNewFeatures, boolean useOldFeatures) {
		// Transfer useNewFeatures/useOldFeatures to feature bits
		setFixedFeatures(INDEX_NEW_FEATURES, useNewFeatures);
		setFixedFeatures(INDEX_OLD_FEATURES, useNewFeatures);

		numFeatures = 0;
		for (int i = 0; i < featureFlags.length; i++) {
			if (featureFlags[i] == '1') {
				numFeatures++;
			}
		}
	}

	public int getNumFeatures() {
		return numFeatures;
	}
}