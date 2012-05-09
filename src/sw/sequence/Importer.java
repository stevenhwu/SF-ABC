package sw.sequence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import sw.main.Main;
import sw.zold.OldSetup;
import dr.evolution.alignment.Alignment;
import dr.evolution.alignment.SimpleAlignment;
import dr.evolution.sequence.Sequence;

public class Importer {

	File aliFile;
	private int noSeq;


	public Importer(String f) {
		aliFile = new File(f);
	}

	@Deprecated
	public Importer(OldSetup s) {
//		aliFile = s.getfAliFile();
		noSeq = s.getNoTotalSeq();
		updateAliFile(s.getAlignmentFile());
	}
	
	@Deprecated
	public Importer(String f, OldSetup s) {
//		aliFile = new File(f);
		updateAliFile(f);
		noSeq = s.getNoTotalSeq();
	}

	public Importer(String f, int noS) {
		updateAliFile(f);
//		aliFile = new File(f);
		noSeq = noS;
	}
	
	private void updateAliFile(String f){
		aliFile = new File(f);
	}

	public Alignment importAlignment() throws IOException   {

		BufferedReader br = new BufferedReader(new FileReader(aliFile));
		SimpleAlignment sa = readSeq(br);
		br.close();
		
		return sa;
	}
	
	private SimpleAlignment readSeq(BufferedReader br) throws IOException {
		
		SimpleAlignment sa = new SimpleAlignment();
		String line = null;
		boolean isStartSeq = false;
		boolean isEndSeq = false;
		try {
			while ((line = br.readLine().trim()) != null) {
				if (line.equals("matrix")) {
					isStartSeq = true;
					br.readLine();
				}
				
				if (isStartSeq && !isEndSeq){
					if (line.contains(";")) {
						break;
					}
					
					String t = br.readLine();
					if (!StringUtils.isAsciiPrintable(t)) {
						// br.close();
						return null;
					}
					// Sequence s = ;// br.readLine());
					sa.addSequence(new Sequence(t));
					if (sa.getSequenceCount() == noSeq) {
						break;
					}
				}
				
			}
//
//			while ((line = br.readLine()) != null) {
//				if (line.contains(";")) {
//					break;
//				}
//
//				String t = br.readLine();
//				if (!StringUtils.isAsciiPrintable(t)) {
//					// br.close();
//					return null;
//				}
//				// Sequence s = ;// br.readLine());
//				sa.addSequence(new Sequence(t));
//
//				if (sa.getSequenceCount() == noSeq) {
//					break;
//				}
//
//			}
			// br.close();

			if (sa.getSequenceCount() != noSeq) {
				System.out.println(sa.getSequenceCount() + "\t" + noSeq
						+ " return null");
				return null;
			}

	
			return sa;
		} catch (Exception e) {
	
			e.printStackTrace();
//			System.out.println(aliFile.toString());
//			String s = aliFile.getParent();
//			s = s.substring(s.lastIndexOf(File.separatorChar)+1);
//			File f = new File(Main.USERDIR+File.separatorChar+System.currentTimeMillis()+"_"+s);
//			System.out.println(f.toString());
//			FileUtils.copyFile(aliFile, f);
//			System.out.println("br:\t"+br.hashCode());
//			System.out.println(br.ready());	
//			System.out.println(br.readLine());	
//			System.out.println("SA:\t"+sa.getSiteCount());
			
		}
		return null;		



	}

	@Deprecated
	private SimpleAlignment readSeqBackup(BufferedReader br) throws IOException {
		
		SimpleAlignment sa = new SimpleAlignment();
		String line = null;
		try {
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
					// br.close();
					return null;
				}
				// Sequence s = ;// br.readLine());
				sa.addSequence(new Sequence(t));

				if (sa.getSequenceCount() == noSeq) {
					break;
				}

			}
			// br.close();

			if (sa.getSequenceCount() != noSeq) {
				System.out.println(sa.getSequenceCount() + "\t" + noSeq
						+ " return null");
				return null;
			}

	
			return sa;
		} catch (Exception e) {
	
			e.printStackTrace();
			System.out.println(aliFile.toString());
			String s = aliFile.getParent();
			s = s.substring(s.lastIndexOf(File.separatorChar)+1);
			File f = new File(Main.USERDIR+File.separatorChar+System.currentTimeMillis()+"_"+s);
			System.out.println(f.toString());
			FileUtils.copyFile(aliFile, f);
			System.out.println("br:\t"+br.hashCode());
			System.out.println(br.ready());	
			System.out.println(br.readLine());	
			System.out.println("SA:\t"+sa.getSiteCount());
			
		}
		return null;		



	}

	
	public void testingOutPut(String userDir) throws IOException {

		System.out.println(aliFile.toString());
		System.out.println(aliFile.getParent());
		String p = aliFile.getParent();
		p = p.substring(p.indexOf('_'),p.length());
		System.out.println(p);
		File f = new File(userDir+File.separatorChar+"Error_Input_"+p+"_"+System.currentTimeMillis());
		FileUtils.copyFile(aliFile, f);
		
		
		
		
	}
}