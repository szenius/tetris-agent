package net.sourceforge.jswarm_pso.zzz;

import net.sourceforge.jswarm_pso.Swarm;

/**
 * Maximize function
 * 		f( x1 , x2 ) =  20.0 + (x1 * x1) + (x2 * x2) - 10.0
 * 
 * With constraint x1+x2 <= 500
 * 
 * Solution is either: [500, 0] or [0, 500]
 * 
 * @author Pablo Cingolani <pcingola@users.sourceforge.net>
 */
public class Example {

	//-------------------------------------------------------------------------
	// Main
	//-------------------------------------------------------------------------
	public static void main(String[] args) {
		System.out.println("Begin: Test 1\n");

		// Create a swarm (using 'MyParticle' as sample particle and 'MyFitnessFunction' as fitness function)
		Swarm swarm = new Swarm(Swarm.DEFAULT_NUMBER_OF_PARTICLES, new MyParticle(), new MyFitnessFunction());

		// Min / Max possition (form 0 to 500)
		swarm.setMaxPosition(500);
		swarm.setMinPosition(0);
		swarm.setInertia(0.95); // Optimization parameters
		swarm.setMaxMinVelocity(1.0);

		int numberOfIterations = 1000;

		// Optimize (and time it)
		for( int i = 0; i < numberOfIterations; i++ )
			swarm.evolve();

		// Print results
		System.out.println(swarm.toStringStats());
		System.out.println("End: Test 1");
	}
}
