import JSwarm.net.sourceforge.jswarm_pso.Swarm;

public class PSO {
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		Swarm swarm = new Swarm(200
				, new TParticle()
				, new TFitnessFunction());
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