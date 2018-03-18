public class Heuristic {
	public static final int NUM_FEATURES = 22;

	static double[] weights;
	static int weightCounter;

	public static double evaluate(TempState s, double[] inputWeights) {
		weightCounter = 0;

		if (inputWeights == null) {
			weights = getDefaultWeights();
		} else {
			weights = inputWeights;
		}

		int[][] field = s.getField();

		int[] colHeights = colHeights(field);

		return wNumRolesCleared(s.getRowsCleared()) + wSumColHeight(colHeights) + wSumColDiff(colHeights) 
				+ wMaxColHeight(colHeights) + wNumHoles(field, colHeights);
	}

	public static double evaluate(TempState s) {
		return evaluate(s, getDefaultWeights());
	}

	// TODO: either remove or read from file
	private static double[] getDefaultWeights() {
		weights = new double[NUM_FEATURES];
		for (int i = 0; i < weights.length; i++) {
			weights[i] = 1;
		}
		return weights;
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

	// Returns weighted number of rows cleared by playing this move
	private static double wNumRolesCleared(int rowsCleared) {
		return getNextWeight() * rowsCleared;
	}

	// Returns weighted sum of column heights
	private static double wSumColHeight(int[] colHeights) {
		double weightedSum = 0;

		// iterate through columns, count height for each col
		for (int i = 0; i < colHeights.length; i++) {
			weightedSum += getNextWeight() * colHeights[i];
		}

		return weightedSum;
	}

	// Returns weighted sum of absolute differences between adjacent columns
	private static double wSumColDiff(int[] colHeights) {
		double weightedSum = 0;

		for (int i = 1; i < colHeights.length; i++) {
			weightedSum += getNextWeight() * Math.abs(colHeights[i] - colHeights[i-1]);
		}
		return weightedSum;
	}

	// Returns weighted max col
	private static double wMaxColHeight(int[] colHeights) {
		int max = 0;

		for (int i = 0; i < colHeights.length; i++) {
			if (colHeights[i] > max) {
				max = colHeights[i];
			}
		}

		// System.out.println("Max col height = " + max);

		return getNextWeight() * max;
	}

	// Returns weighted number of holes
	private static double wNumHoles(int[][] field, int[] colHeights) {
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

	private static double getNextWeight() {
		return weights[weightCounter++];
	}

	public static int getNumFeatures() {
		return NUM_FEATURES;
	}
}