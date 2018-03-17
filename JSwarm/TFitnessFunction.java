import net.sourceforge.jswarm_pso.FitnessFunction;
import java.util.Arrays.*;
import java.util.*;

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
	private int playGame(double[] position) {
		// TODO: play the game and return number of rows cleared
		return -1; // stub
	}

	// Return string version of position array as key
	private String generatePositionKey(double[] position) {
		return Arrays.toString(position);
	}
}