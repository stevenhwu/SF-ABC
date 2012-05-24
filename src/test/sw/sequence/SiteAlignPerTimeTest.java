package test.sw.sequence;

import static org.junit.Assert.assertEquals;

import java.util.List;

import jebl.evolution.alignments.BasicAlignment;
import jebl.evolution.sequences.BasicSequence;
import jebl.evolution.sequences.Sequence;
import jebl.evolution.sequences.SequenceType;
import jebl.evolution.taxa.Taxon;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import sw.simulator.SSC;
import dr.evolution.alignment.SimpleAlignment;

public class SiteAlignPerTimeTest {

	static jebl.evolution.alignments.Alignment jeblAlignment;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		SSC simulator = new SSC(2, 10, 500);
		jeblAlignment = simulator.simulateAlignment(3000, 1e-2);
//		jeblAlignment = 
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testConvertBetweenDrAndJeblAlignment() {
		// jebl to dr
		SimpleAlignment drAlignment = new SimpleAlignment();
		List<Sequence> allSeq = jeblAlignment.getSequenceList();
		for (Sequence seq : allSeq) {
			String actualSeq = seq.getString();
			dr.evolution.sequence.Sequence drSeq = new dr.evolution.sequence.Sequence(actualSeq);
			drAlignment.addSequence(drSeq);
		}
		
		for (Sequence sequence : allSeq) {
			assertEquals(sequence.getString(), 
					drAlignment.removeSequence(0).getSequenceString());
		}
		// dr back to jebl
		BasicAlignment jAlignment = new BasicAlignment();
		List<dr.evolution.sequence.Sequence> allDrSeq = drAlignment.getSequences();
		for (dr.evolution.sequence.Sequence seq : allDrSeq) {
			String actualSeq = seq.getSequenceString();
			Taxon t = Taxon.getTaxon(seq.getTaxon().toString());
			BasicSequence jSeq = new BasicSequence(SequenceType.NUCLEOTIDE, t, actualSeq);
			jAlignment.addSequence(jSeq);
		}
		for (dr.evolution.sequence.Sequence seq : allDrSeq) {
			String actualSeq = seq.getSequenceString();
			Taxon t = Taxon.getTaxon(seq.getTaxon().toString());
			assertEquals(actualSeq, jAlignment.getSequence(t).getString() );
		}
		assertEquals(750, jeblAlignment.getPatternCount());
		assertEquals(750, jeblAlignment.getSiteCount());
		assertEquals(20,  jeblAlignment.getPatternLength());
		
	
	}
	


}
