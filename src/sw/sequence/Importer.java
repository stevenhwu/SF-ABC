package sw.sequence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.apache.commons.lang.StringUtils;

import sw.main.Setup;

import dr.evolution.alignment.Alignment;
import dr.evolution.alignment.SimpleAlignment;
import dr.evolution.sequence.Sequence;

public class Importer {

	File aliFile;
	private int noSeq;

	@Deprecated
	public Importer(File aliFile) {
		this.aliFile = aliFile;
	}

	@Deprecated
	public Importer(String f) {
		aliFile = new File(f);
	}

	
	public Importer(Setup s) {
//		aliFile = s.getfAliFile();
		noSeq = s.getNoTotalSeq();
	}

	public Importer(String f, Setup s) {
		aliFile = new File(f);
		noSeq = s.getNoTotalSeq();
	}

	public Importer(String f, int noS) {
		aliFile = new File(f);
		noSeq = noS;
	}
	
	public void updateAliFile(String f){
		aliFile = new File(f);
	}

	public Alignment importAlignment() throws Exception {

//		SimpleAlignment sa = null;
//		String line = null;
		// try {

		BufferedReader br = new BufferedReader(new FileReader(aliFile));
		SimpleAlignment sa = readSeq(br);
//		while ((line = br.readLine().trim()) != null) {
//			if (line.equals("matrix")) {
//				break;
//			}
//		}
//
//		sa = new SimpleAlignment();
//		while ((line = br.readLine()) != null) {
//			if (line.contains(";")) {
//				break;
//			}
//
//			String t = br.readLine();
//			if (!StringUtils.isAsciiPrintable(t)) {
//				br.close();
//				return null;
//			}
//			Sequence s = new Sequence(t);// br.readLine());
//			sa.addSequence(s);
//
//			if (sa.getSequenceCount() == noSeq) {
//				break;
//			}
//
//		}
		

//		if (sa.getSequenceCount() != noSeq) {
//			System.out.println(sa.getSequenceCount() + "\t" + noSeq
//					+ " return null");
//			return null;
//		}

		// } catch (Exception e) {
		// e.printStackTrace();
		// System.out.println("line: "+line+"\t"+sa.toString());
		// sa = null;
		//
		// }
		br.close();
		return sa;
	}
	
	private SimpleAlignment readSeq(BufferedReader br) throws Exception{
		
		SimpleAlignment sa = new SimpleAlignment();
		String line = null;
		
		while ((line = br.readLine().trim()) != null) {
			if (line.equals("matrix")) {
				break;
			}
		}


		while ((line = br.readLine()) != null) {
			if (line.contains(";")) {
				break;
			}

			String t = br.readLine();
			if (!StringUtils.isAsciiPrintable(t)) {
//				br.close();
				return null;
			}
			//Sequence s = ;// br.readLine());
			sa.addSequence(new Sequence(t));

			if (sa.getSequenceCount() == noSeq) {
				break;
			}

		}
//		br.close();

		if (sa.getSequenceCount() != noSeq) {
			System.out.println(sa.getSequenceCount() + "\t" + noSeq
					+ " return null");
			return null;
		}
		return sa;



	}
}