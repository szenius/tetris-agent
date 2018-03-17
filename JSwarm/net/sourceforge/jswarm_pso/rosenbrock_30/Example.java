package net.sourceforge.jswarm_pso.rosenbrock_30;

import net.sourceforge.jswarm_pso.Neighborhood;
import net.sourceforge.jswarm_pso.Neighborhood1D;
import net.sourceforge.jswarm_pso.Swarm;

/**
 * Minize Rosenbrock function. http://mathworld.wolfram.com/RosenbrockFunction.html
 * 
 * 	General form
 * 
 * 		f( x1 , x2 ) = \sum_{i=1}^{n} { 100 (x_{i+1} - x_i^2)^2 + (1 - x_i)^2 }
 * 		
 * @author Alvaro Jaramillo Duque <aduque@inescporto.pt>
 */
public class Example {

	//-------------------------------------------------------------------------
	// Main
	//-------------------------------------------------------------------------
	public static void main(String[] args) {
		System.out.println("Begin: Example Rosenbrock 30\n");

		// Create a swarm (using 'MyParticle' as sample particle and 'MyFitnessFunction' as fitness function)
		Swarm swarm = new Swarm(1000, new MyParticle(), new MyFitnessFunction());
		// SwarmRepulsive swarm = new SwarmRepulsive(100, new MyParticle(), new MyFitnessFunction());
		//		swarm.setOtherParticleIncrement(0.1);
		//		swarm.setRandomIncrement(0.1);

		// Use neighborhood
		Neighborhood neigh = new Neighborhood1D(Swarm.DEFAULT_NUMBER_OF_PARTICLES / 5, true);
		swarm.setNeighborhood(neigh);
		swarm.setNeighborhoodIncrement(0.9);

		// Set position (and velocity) constraints. I.e.: where to look for solutions
		swarm.setInertia(0.9);
		swarm.setGlobalIncrement(0.9);
		swarm.setParticleIncrement(0.9);

		swarm.setMaxPosition(100);
		swarm.setMinPosition(0);
		swarm.setMaxMinVelocity(0.1);

		int numberOfIterations = 50000;

		for (int i = 0; i < numberOfIterations; i++) {
			swarm.evolve();
			if (i % 1000 == 0) {
				int idxBest = swarm.getBestParticleIndex();
				System.out.println("Iteration: " + i + "\tBest particle (" + idxBest + "):\t" + swarm.getParticle(idxBest).toString());
			}
		}

		// Print en results
		System.out.println(swarm.toStringStats());
		// System.out.println(swarm);
		System.out.println("End: Example Rosenbrock 30");
	}
}
