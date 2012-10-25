package test.sw.abc;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.apache.commons.math3.random.RandomDataImpl;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import sw.abc.parameter.ParaMu;
import sw.abc.parameter.ParaPopsize;
import sw.abc.parameter.ParaTheta;
import sw.abc.parameter.ParametersCollection;
import sw.math.NormalDistribution;
import sw.math.OneOverDistribution;
import sw.math.Scale;
import sw.math.TruncatedScale;
import sw.math.UniformDistribution;
import sw.math.ZTestDistribution;

public class ParaThetaTest {

	private ParaTheta pTheta;
	private org.apache.commons.math3.distribution.NormalDistribution nd;
	private RandomDataImpl r = new RandomDataImpl();
	private ParametersCollection pc;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		nd = new org.apache.commons.math3.distribution.NormalDistribution(0, 1);
		
		pTheta = new ParaTheta(new NormalDistribution(0, 1));
		pTheta.setInitValue(0);
		ParaMu pMu = new ParaMu(new ZTestDistribution(0) );
		pMu.setInitValue(0);
		pMu.setProposal(new ZTestDistribution(0));

		ParaPopsize pPop = new ParaPopsize(new ZTestDistribution(0) );		
		pPop.setInitValue(0);
		pPop.setProposal(new ZTestDistribution(0) );
		
		pc = new ParametersCollection(new String[]{"mu","popsize"}, pMu, pPop);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetPriorRatio() {
		double initPrior = Math.log(nd.density(0));
		assertEquals(0, pTheta.getPriorRatio(0), 0);
		
		
		for (int i = 0; i < 1e5; i++) {
			double next = r.nextUniform(-5, 5);
			double expected = Math.log(nd.density(next)) - initPrior;
			assertEquals(expected, pTheta.getPriorRatio(next), 0);
		}
		
		for (int i = 0; i < 1e5; i++) {
			double next = r.nextUniform(-5, 5);
			double currentPrior = Math.log(nd.density(next)); 
			double expected =  currentPrior- initPrior;
			assertEquals(expected, pTheta.getPriorRatio(next), 0);
			pTheta.acceptNewValue();
			initPrior = currentPrior;
			
		}
	}

	@Test
	public void testGetPriorRatioParametersCollection() {
		
		double initPrior = Math.log(nd.density(0));
		assertEquals(0, pTheta.getPriorRatio(pc), 0);

		for (int i = 0; i < 5; i++) {
			pc.nextProposals();
			double newTheta = Math.pow(2*(i+1), 2);
			double newLogP = Math.log(nd.density(newTheta));
			double expected = newLogP - initPrior;
			assertEquals(expected, pTheta.getPriorRatio(pc), 0);	
			initPrior = newLogP;
			pTheta.acceptNewValue();
			pc.acceptNewValues();
		}
		
	}

	@Test
	public void testGetProposalRatioParametersCollection() {
		
		for (int i = 0; i < 5; i++) {
			pc.nextProposals();
			double expected = 2*(i)* 2;
			assertEquals(expected, pTheta.getProposalRatio(pc), 0);	
			pTheta.acceptNewValue();
			pc.acceptNewValues();
		}
		
	}

}
