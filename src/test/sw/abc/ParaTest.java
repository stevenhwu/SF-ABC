package test.sw.abc;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import sw.abc.parameter.ParaMu;
import sw.abc.parameter.ParaPopsize;
import sw.abc.parameter.Parameters;
import sw.math.UniformDistribution;
import sw.math.ZTestDistribution;

public class ParaTest {

	
	@Before
	public void setUp() throws Exception {
	
	}

	@Test
	public final void testGetValue() {
		
		ParaMu pMu = new ParaMu(new UniformDistribution(0.01, 0.05));

		double value;
		boolean isTrue;
		for (int i = 0; i < 1000; i++) {
			value = pMu.nextPrior();
			isTrue = value>0.01 && value<0.05;
		
			assertTrue( isTrue);
		}
	}

	@Test
	public final void testNextProposal() {

		ArrayList<Parameters> allPar = new ArrayList<Parameters>();
		ParaMu pMu = new ParaMu(new ZTestDistribution(0));
		pMu.setProposal(new ZTestDistribution(0));
		
		ParaPopsize pTheta = new ParaPopsize(new ZTestDistribution(1000));
		pTheta.setProposal(new ZTestDistribution(1000));
		
		allPar.add(pMu);
		allPar.add(pTheta);

		double value;
		boolean isTrue = false;
		for (int i = 0; i < 1000; i++) {
			for (int j = 0; j < 2; j++) {
				value = allPar.get(j).nextProposal();
				if (allPar.get(j) instanceof ParaMu) {
					isTrue = value == 1+i;
				}
				else if(allPar.get(j) instanceof ParaPopsize){
					isTrue = value == 1001+i;
				}
				System.out.println(i+"\t"+value+"\t"+isTrue);
				assertTrue( isTrue);
				allPar.get(j).acceptNewValue();
			}

		}
		
	}
	@Test
	public final void testNextPriorFix() {
		

		ArrayList<Parameters> allPar = new ArrayList<Parameters>();
		ParaMu pMu = new ParaMu(new ZTestDistribution(0));
		pMu.setPrior(new ZTestDistribution(1));
		
		ParaPopsize pTheta = new ParaPopsize(new ZTestDistribution(0));
		pTheta.setPrior(new ZTestDistribution(1000));
		allPar.add(pMu);
		allPar.add(pTheta);

		double value;
		boolean isTrue = false;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 2; j++) {
				value = allPar.get(j).nextPrior();
				if (allPar.get(j) instanceof ParaMu) {
					isTrue = value == 1;
				}
				else if(allPar.get(j) instanceof ParaPopsize){
					isTrue = value == 1000;
				}
//				System.out.println(i+"\t"+value+"\t"+isTrue);
				assertTrue( isTrue);
			}

		}
		
	}
	
	
	
	@Test
	public final void testNextPrior() {
		
		ArrayList<Parameters> allPar = new ArrayList<Parameters>();
		ParaMu pMu = new ParaMu(new UniformDistribution(0.01, 0.05));
		ParaPopsize pTheta = new ParaPopsize(new UniformDistribution(1000, 5000));
		allPar.add(pMu);
		allPar.add(pTheta);

		double value;
		boolean isTrue = false;
		for (int i = 0; i < 1000; i++) {
			for (int j = 0; j < 2; j++) {
				value = allPar.get(j).nextPrior();
				if (allPar.get(j) instanceof ParaMu) {
					isTrue = value>0.01 && value<0.05;
				}
				else if(allPar.get(j) instanceof ParaPopsize){
					isTrue = value>1000 && value<5000;
				}
//				System.out.println(i+"\t"+value+"\t"+isTrue);
				assertTrue( isTrue);
			}

		}
		
		
	}

}
