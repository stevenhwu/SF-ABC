package sw.abc.stat.data;

public class StatArray {

	double[] stats;
	String name;
	

	public StatArray(String string) {
		this.name = string;
	}
	
	public double[] getStats() {
		return stats;
	}

	public void setStats(double[] stats) {
		this.stats = stats;
	}

	public String getName() {
		return name;
	}



	
}
