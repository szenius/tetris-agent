import JSwarm.net.sourceforge.jswarm_pso.Swarm;
import java.util.*;

public class PSO {
	public static void main(String[] args) {
		Heuristic h = null;
		if (args.length == 2) {
			h = new Heuristic(Boolean.parseBoolean(args[0]), Boolean.parseBoolean(args[1]));
		} else if (args.length == 3) {
			if (args[2].length() != Heuristic.getNumAvailableFeatures()) {
				System.out.println("There should be " + h.getNumAvailableFeatures() + " bits in total for your third argument.");
				System.exit(-1);
			}
			h = new Heuristic(Boolean.parseBoolean(args[0]), Boolean.parseBoolean(args[1]), args[2]);
		} else {
			System.out.println("Wrong number of arguments.");
			System.exit(-1);
		}
		long startTime = System.currentTimeMillis();

		Swarm swarm = new Swarm(200
				, new TParticle(h.getNumFeatures())
				, new TFitnessFunction(h));
		// Set position (and velocity) constraints. 
		// i.e.: where to look for solutions
		swarm.setMaxPosition(10);
		swarm.setMinPosition(-10);
		// swarm.setMaxMinVelocity(0.1);
		// Optimize a few times
		for( int i = 0; i < 20; i++ ) {
			long startIt = System.currentTimeMillis();
			swarm.evolve();
			long endIt = System.currentTimeMillis();
			System.out.println("Iteration " + i + ": " + swarm.toStringStats() + " in " + (endIt/startIt)/1e3 + " s");
		}
		Long endTime = System.currentTimeMillis();
		// Print en results
		System.out.println(swarm.toStringStats());
		System.out.println("Total run time (s): " + (endTime - startTime)/1e3);
	}
}