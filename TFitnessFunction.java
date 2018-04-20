import java.util.Arrays;

import JSwarm.net.sourceforge.jswarm_pso.FitnessFunction;
import JSwarm.net.sourceforge.jswarm_pso.Particle;

public class TFitnessFunction extends FitnessFunction {
	Heuristic h;

	public TFitnessFunction(Heuristic h) {
		this.h = h;
	}

	public double evaluate(double[] position) {
		return -1;
	}

	public int[] evaluateBatch(Particle[] particles) {
		// copy Particle[][] to positionSets[][]
		double[][] positionSets = new double[particles.length][particles[0].getPosition().length];
		for (int i = 0; i < particles.length; i++) {
			double[] position = particles[i].getPosition();
			for (int j = 0; j < position.length; j++) {
				positionSets[i][j] = position[j];
			}
		}
		return playGames(positionSets);
	}

	public int[] playGames(double[][] positionSets) {
		Heuristic heuristics = h;
		int numParticles = positionSets.length;
		double[][] weightSets = positionSets;
		int numRepititions = 10;
		int[] particleResults = new int[numParticles];


		// for each particle run 10 games.
		for (int i=0; i < numParticles; i++) {
			int particleScore = particleEvaluation(heuristics, weightSets[i], numRepititions);
			particleResults[i] = particleScore;
		}

		return particleResults;
	}

	// Play 10 games and return evaluations
	public Integer particleEvaluation(Heuristic h, double[] weightSets, int numRepetitions){
		boolean USE_MEAN = false;
		double[] results = new double[numRepetitions];
		double sum = 0;
		for (int i = 0; i < numRepetitions; i++) { // compute sum
			int result = playGame(h, weightSets);
			while (result == -1) {
				result = playGame(h, weightSets);
			}
			results[i] = result;
			sum += results[i];
		}
		Arrays.sort(results);
		double mean = sum / numRepetitions;
		double median = 0;
		if (numRepetitions%2==0) median = 0.5*(results[numRepetitions/2] + results[(numRepetitions/2)+1]);
		else median = results[numRepetitions/2];
		double score = 0;
		if (USE_MEAN) score = mean;
		else score = median;
		System.out.println("Weights: " + Arrays.toString(weightSets) + "\n" +
				"Results: " + Arrays.toString(results) + "; " +
				"Median (" + !USE_MEAN + ") = " + median + "; Mean (" + USE_MEAN + ") = " + mean);
		return (int) score;
	}


	/**
	 * This method creates a new game for each given weight
	 * @return number of rows cleared in this game using the given weight
	 **/
	public int playGame(Heuristic h, double[] weightSets) {
		PlayerSkeleton p = new PlayerSkeleton();
		State s = new State();
		while(!s.hasLost()) {
			try {
				s.makeMove(p.pickMove(s, s.legalMoves(), weightSets, h));
			} catch (Exception e) {
				e.printStackTrace();
				return -1;
			}
			try {
				Thread.sleep(0);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return s.getRowsCleared();
	}
}