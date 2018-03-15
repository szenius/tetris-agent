public class Heuristic {
	public static final int NUM_FEATURES = 21;
	static int[] weights;
	static int weightCounter;

	public static int evaluate(int[][] field, int rowsCleared) {
		weightCounter = 0;
		readWeights();

		// print field
		// for (int i = 0; i < field.length; i++) {
		// 	for (int j = 0; j < field[0].length; j++) {
		// 		System.out.print(field[i][j] + " ");
		// 	}
		// 	System.out.println();
		// }

		int[] colHeights = colHeights(field);
		return rowsCleared + wSumColHeight(colHeights) + wSumColDiff(colHeights) 
				+ wMaxColHeight(colHeights) + wNumHoles(field, colHeights);
	}

	// TODO: read from file instead
	private static void readWeights() {
		weights = new int[NUM_FEATURES];
		for (int i = 0; i < weights.length; i++) {
			weights[i] = 1;
		}
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

	// Returns weighted sum of column heights
	private static int wSumColHeight(int[] colHeights) {
		int weightedSum = 0;

		// iterate through columns, count height for each col
		for (int i = 0; i < colHeights.length; i++) {
			weightedSum += getNextWeight() * colHeights[i];
		}

		return weightedSum;
	}

	// Returns weighted sum of absolute differences between adjacent columns
	private static int wSumColDiff(int[] colHeights) {
		int weightedSum = 0;

		for (int i = 1; i < colHeights.length; i++) {
			weightedSum += getNextWeight() * Math.abs(colHeights[i] - colHeights[i-1]);
		}
		return weightedSum;
	}

	// Returns weighted max col
	private static int wMaxColHeight(int[] colHeights) {
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