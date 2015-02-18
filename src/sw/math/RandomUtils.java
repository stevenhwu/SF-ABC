package sw.math;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;


public class RandomUtils {

	private static final MersenneTwisterFast rmt = MersenneTwisterFast.DEFAULT_INSTANCE;

	private static RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister());

	public static double nextUnif() {
		return nextDouble();
		
	}
	
	public static double nextLogDouble() {
		return Math.log(nextDouble());
		
	}
	
	public static double nextDouble() {
		return random.nextUniform(0, 1);
	}
	
	public static double nextDouble2() {
		synchronized (rmt) {
			return rmt.nextDouble();
		}
	}

	public static double nextGaussian(double mu, double sigma) {
		return random.nextGaussian(mu, sigma);
	}

	public static double nextUniform(double lower, double upper) {
		return random.nextUniform(lower, upper);
	}
	
}
