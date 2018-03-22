public class EvaluationResult implements Comparable<EvaluationResult>{
    private double[] weightSets;
    public int rowsCleared;

    public EvaluationResult(double[] weightSets, int rowsCleared) {
        this.weightSets = weightSets;
        this.rowsCleared = rowsCleared;
    }

    public double getCumulativeSum() {
        double sum = 0;
        for (int i = 0; i < weightSets.length; i++) {
            sum += weightSets[i];
        }
        return sum;
    }

    public double[] getWeightSets() {
        return weightSets;
    }

    public int compareTo(EvaluationResult other) {
        return Integer.compare(rowsCleared, other.rowsCleared);
    }
}
