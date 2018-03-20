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

		int totalGamesPlayed = 3;
		int[] arrNumRowsCleared = new int[3];
		int sumNumRowsCleared = 0;
		int meanNumRowsCleared;
		Double standardDeviation = new Double(0);
		int std = 0;
		int fitnessValue;
		for (int i=0; i < totalGamesPlayed; i++){
			int gameScore = playGame(position, i);
			arrNumRowsCleared[i] = gameScore;
			sumNumRowsCleared += gameScore;
		}
		meanNumRowsCleared = sumNumRowsCleared/totalGamesPlayed;
		for (int i=0; i < totalGamesPlayed; i++){
			double sumSquaredDifferences = 0;
			sumSquaredDifferences += Math.pow(arrNumRowsCleared[i] - meanNumRowsCleared, 2);
			standardDeviation = Math.sqrt(sumSquaredDifferences / totalGamesPlayed);
		}
		std = standardDeviation.intValue();
		fitnessValue = meanNumRowsCleared - std;
		System.out.println("For " + generatePositionKey(position) + " STD " + " : " + std);
		System.out.println("For " + generatePositionKey(position) + " MEAN " + " : " + meanNumRowsCleared);
		System.out.println("For " + generatePositionKey(position) + " FITNESS " + " : " + fitnessValue);
		cache.put(positionKey, fitnessValue);
		return fitnessValue;
	}

	// Try to play the game given the combination of weights (given by position)
	// Output the (average? TODO:) number of rows cleared for this set of weights
	// TODO: parallel?
	private int playGame(double[] position, int iteration) {
		State s = new State();
		PlayerSkeleton p = new PlayerSkeleton();
		while(!s.hasLost()) {
			s.makeMove(p.pickMove(s,s.legalMoves(),position));
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("For " + generatePositionKey(position) + " iteration " + (iteration+1) +" : " + s.getRowsCleared());
		return s.getRowsCleared();
	}

	// Return string version of position array as key
	private String generatePositionKey(double[] position) {
		return Arrays.toString(position);
	}
}