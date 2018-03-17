package net.sourceforge.jswarm_pso.zzz;

import net.sourceforge.jswarm_pso.FitnessFunction;

/**
 * Sample Fitness function
 * 		f( x1 , x2 ) =  20.0 +(x1 * x1) + (x2 * x2) - 10.0
 */
class MyFitnessFunction extends FitnessFunction {

	public double penaltyFactor = 1e6;

	@Override
	public double evaluate(double position[]) {
		double x1 = position[0];
		double x2 = position[1];

		// Penalize if (x1+x2) > 500
		double penalty = 0;
		double sumX = (x1 + x2) - 500;
		if( sumX > 0 ) penalty = penaltyFactor * sumX;

		double y = 20.0 + (x1 * x1) + (x2 * x2) - 10.0 - penalty;
		return y;
	}
}
