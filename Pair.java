public class Pair {
	private int[][] boardConfig;
	private int rowsClear;

	public Pair(int[][] field, int rowsClear) {
		this.boardConfig = field;
		this.rowsClear = rowsClear;
	}

	public int[][] getField() {
		return this.boardConfig;
	}

	public int getRowsCleared() {
		return rowsClear;
	}
}