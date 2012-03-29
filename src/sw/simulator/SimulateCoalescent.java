package sw.simulator;

import java.util.Arrays;

public class SimulateCoalescent {


	int noTime;
	int[] noSeq;
	int gap;
	
	public SimulateCoalescent(int noTime, int gap, int noSeq){
		this(noTime, gap);
		this.noSeq = new int[this.noTime];
		for (int i = 0; i < this.noSeq.length; i++) {
			this.noSeq[i] = noSeq;
		}
		System.out.println(Arrays.toString(this.noSeq));
	}
	
	public SimulateCoalescent(int noTime, int gap, int[] noSeq){
		this(noTime, gap);
		this.noSeq = noSeq;
	}
	public SimulateCoalescent(int noTime, int gap){
		this.noTime = noTime;
		this.gap = gap;
		
	}
	
	public void simulateTree() {
		
		
	}

	public void getTree() {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
