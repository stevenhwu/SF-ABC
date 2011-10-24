package sw.math;

import org.apache.commons.math.random.RandomDataImpl;

public class RandomGenerator {

	static RandomDataImpl r = new RandomDataImpl();

	public static double nextUnif() {
		return r.nextUniform(0, 1);
		
	}
}
