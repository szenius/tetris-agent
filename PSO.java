import JSwarm.net.sourceforge.jswarm_pso.Swarm;

public class PSO {
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		Swarm swarm = new Swarm(Swarm.DEFAULT_NUMBER_OF_PARTICLES
				, new TParticle()
				, new TFitnessFunction());
		// Set position (and velocity) constraints. 
		// i.e.: where to look for solutions
		swarm.setMaxPosition(10);
		swarm.setMinPosition(-10);
		// Optimize a few times
		for( int i = 0; i < 100; i++ ) swarm.evolve();
		Long endTime = System.currentTimeMillis();
		// Print en results
		System.out.println(swarm.toStringStats());
		System.out.println("Total run time (ms): " + (endTime - startTime));
	}
}