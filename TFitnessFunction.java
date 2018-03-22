import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import JSwarm.net.sourceforge.jswarm_pso.FitnessFunction;

public class TFitnessFunction extends FitnessFunction {
	// so that we don't run the game for the same combination of weights over and over
	Map<String, Integer> cache = new HashMap<>(); 

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

	// Try to play the game given the combination of weights (given by position)
	// Output the (average? TODO:) number of rows cleared for this set of weights
	// TODO: parallel?
	private int playGame(double[] position) {
		double[][] weightSets = new double[1][position.length];

		for (int i = 0; i < position.length; i++) {
			weightSets[0][i] = position[i];
		}

		SimulationPool pool = new SimulationPool(500, weightSets, false);
		return pool.startScheduler()[0];
/*		State s = new State();
		PlayerSkeleton p = new PlayerSkeleton();
		while(!s.hasLost()) {
			s.makeMove(p.pickMove(s,s.legalMoves(),position));
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("For " + generatePositionKey(position) + ": " + s.getRowsCleared());
		return s.getRowsCleared();
*/	
	}

	// Return string version of position array as key
	private String generatePositionKey(double[] position) {
		return Arrays.toString(position);
	}
}