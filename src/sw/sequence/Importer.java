package sw.sequence;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystem;

import org.apache.commons.io.FileUtils;
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
		updateAliFile(s.getAlignmentFile());
	}

	public Importer(String f, Setup s) {
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
		try {
			return sa;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;		



	}

	public void testingOutPut() throws IOException {

		System.out.println(aliFile.toString());
		System.out.println(aliFile.getParent());
		File f = new File(aliFile.getParent()+File.separatorChar+"Error_Input_"+System.currentTimeMillis());
		FileUtils.copyFile(aliFile, f);
		
		
		
		
	}
}