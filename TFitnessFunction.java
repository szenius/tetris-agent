import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import JSwarm.net.sourceforge.jswarm_pso.FitnessFunction;
import JSwarm.net.sourceforge.jswarm_pso.Particle;

public class TFitnessFunction extends FitnessFunction {
	// so that we don't run the game for the same combination of weights over and over
	Map<String, Integer> cache = new HashMap<>(); 

	public int[] evaluateBatch(Particle[] particles) {
		// copy Particle[][] to positionSets[][]
		double[][] positionSets = new double[particles.length][particles[0].getPosition().length];
		for (int i = 0; i < particles.length; i++) {
			double[] position = particles[i].getPosition();
			for (int j = 0; j < position.length; j++) {
				positionSets[i][j] = position[j];
			}
		}

		return playBatchGames(positionSets);
	}

	// Returns the score of this particle's position
	// Particle's position is the combination of weights for the features
	// Score is the number of rows cleared given this combination of weights
	public double evaluate(double[] position) {
		String positionKey = generatePositionKey(position);

		if (cache.containsKey(positionKey)) {
			return cache.get(positionKey);
		}

		int numRowsCleared = playGame(position);
		cache.put(positionKey, numRowsCleared);
		return numRowsCleared;
	}

	public int[] playBatchGames(double[][] positionSets) {
		SimulationPool pool = new SimulationPool(positionSets.length, positionSets, true, true, 10);
		return pool.startScheduler();
	}

	// Try to play the game given the combination of weights (given by position)
	// Output the average number of rows cleared for this set of weights
	// TODO: parallel?
	private int playGame(double[] position) {
		double[][] weightSets = new double[1][position.length];

		for (int i = 0; i < position.length; i++) {
			weightSets[0][i] = position[i];
		}

		SimulationPool pool = new SimulationPool(10, weightSets, false);

		// compute and return average of all results for this set of weights
		int[] results = pool.startScheduler();
		int sum = 0;
		for (int i = 0; i < results.length; i++) {
			sum += results[i];
		}
		int avg = (int) sum / results.length;

		// System.out.println(Arrays.toString(position) + " ||| " + avg);

		return avg;
	}

	// Return string version of position array as key
	private String generatePositionKey(double[] position) {
		return Arrays.toString(position);
	}
}