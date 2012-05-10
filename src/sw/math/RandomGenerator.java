package sw.math;

import org.apache.commons.math.random.RandomDataImpl;


public class RandomGenerator {

	private static final MersenneTwisterFast random = MersenneTwisterFast.DEFAULT_INSTANCE;

	static RandomDataImpl r = new RandomDataImpl();

	public static double nextUnif() {
		return r.nextUniform(0, 1);
		
	}
	
	public static double nextLogDouble() {
		return Math.log(nextDouble());
		
	}
	
	public static double nextDouble() {
		synchronized (random) {
			return random.nextDouble();
		}
	}
	
}
