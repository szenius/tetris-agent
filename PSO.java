import JSwarm.net.sourceforge.jswarm_pso.Swarm;
import java.util.*;

public class PSO {
	public static void main(String[] args) {
		Heuristic h = parseArgs(args);

		if (h == null) System.exit(-1);

		System.out.println("NEW PSO RUN:");
		System.out.println(h.printFeatureInfo());

		long startTime = System.currentTimeMillis();
		run(h);
		Long endTime = System.currentTimeMillis();
		System.out.println("Total run time (s): " + (endTime - startTime)/1e3);
		System.out.println("END PSO RUN");
	}

	/**
	 * Run PSO algorithm using Heuristic h
	 * @param h: Heuristic with set of features based on input args
	 * @return
	**/
	private static void run(Heuristic h) {
		Swarm swarm = new Swarm(200
		, new TParticle(h.getNumFeatures())
		, new TFitnessFunction(h));

    // Set position (and velocity) constraints. 
		// i.e.: where to look for solutions
		swarm.setMaxPosition(10);
		swarm.setMinPosition(-10);

		// Optimize a few times
		for( int i = 0; i < 20; i++ ) {
			long startIt = System.currentTimeMillis();
			swarm.evolve();
			long endIt = System.currentTimeMillis();
			System.out.println("Iteration " + i + ": " + swarm.toStringStats() + " in " + (endIt - startIt)/1e3 + " s");
		}

		// Print en results
		System.out.println(swarm.toStringStats());
	}
  
	private static Heuristic parseArgs(String[] args) {
		if (args.length == 2) {
			return new Heuristic(Boolean.parseBoolean(args[0]), Boolean.parseBoolean(args[1]));
		} else if (args.length == 3) {
			if (args[2].length() != Heuristic.getNumAvailableFeatures()) {
				System.out.println("There should be " + Heuristic.getNumAvailableFeatures() + " bits in total for your third argument.");
				System.exit(-1);
			}
			return new Heuristic(Boolean.parseBoolean(args[0]), Boolean.parseBoolean(args[1]), args[2]);
		}
		System.out.println("Wrong number of arguments.");
		return null;
	}
}