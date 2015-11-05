package sw.abc.parameter;

public enum ParameterList {
	mu (0),
	popsize (1);
	

    private final double index;   

    private ParameterList(double i) {
        this.index = i;

    }


}
