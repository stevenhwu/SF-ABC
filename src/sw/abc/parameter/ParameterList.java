package sw.abc.parameter;

public enum ParameterList {
	Mu (0),
	Theta (1);
	
	

    private final double index;   // in kilograms

    private ParameterList(double i) {
        this.index = i;

    }
    private double index()   { return index; }


    // universal gravitational constant  (m3 kg-1 s-2)
    public static final double G = 6.67300E-11;

    double surfaceGravity() {
        return G * index;
    }
    double surfaceWeight(double otherMass) {
        return otherMass * surfaceGravity();
    }
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage:  java Planet <earth_weight>");
            System.exit(-1);
        }
        double earthWeight = Double.parseDouble(args[0]);
        double mass = earthWeight;///EARTH.surfaceGravity();
        for (ParameterList p : ParameterList.values())
           System.out.printf("Your weight on %s is %f%n",
                             p, p.surfaceWeight(mass));
    }
//	public enum Day {
//	    SUNDAY, MONDAY, TUESDAY, WEDNESDAY, 
//	    THURSDAY, FRIDAY, SATURDAY 
//	}
}
