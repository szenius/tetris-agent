import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
		SimulationPool pool = new SimulationPool(h, positionSets.length, positionSets, 10);
		return pool.startScheduler();
	}
}