/*******************************************************************************
 * MainAnalysisResultHPD.java
 * 
 * This file is part of BIDE-2D
 * 
 * Copyright (C) 2012 Steven Wu
 * 
 * BIDE-2D is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * BIDE-2D is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with BIDE-2D.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package sw.hpd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.util.MathUtils;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;

public class MainAnalysisResultHPD {
	
	static String[] NAMES = {"mu", "popsize", "theta"};
	public static void main(String[] args) {
	
		
		String cwd = System.getProperty("user.dir")+File.separator;
		cwd = "/home/sw167/workspace/SF-ABC/Simulations/ABC_0621/TemplateFiles/";

		String[] filenameTamplate = {"CPo3Ti40S400_", ".pau_summary.log"};
		calHPD(cwd, filenameTamplate, 0, 99);

	}

	private static void calHPD(String cwd, String[] filenameTamplate, int start, int end) {
		
		try {
			FileWriter fout = new FileWriter(cwd+"ABC_result.tab");
			fout.write("data\tmuLowel\tmuUpper\tmuTF\tpopLower\tpopUpper\tpopTF\tthetaLower\tthetaUpper\tthetaTF\n");
			
			int count[] = new int[NAMES.length];
			Arrays.fill(count, 0);
			for (int i = start; i <= end; i++) {
				fout.write(""+i);
				String filename = filenameTamplate[0]+i+filenameTamplate[1];
				count = readLogFile(cwd, filename, count, fout);
			}
			System.out.println(Arrays.toString(count));
			
			fout.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	private static int[] readLogFile(String cwd, String logFileName, int[] count, FileWriter fout) {

		String logFile = cwd+logFileName;
		System.out.println("File:\t" + logFile);
		try {

			BufferedReader in = new BufferedReader(new FileReader(logFile));
//			FileWriter fout = new FileWriter(logFile+"_result.tab");
			String input = in.readLine();
			String name = in.readLine();//.split("\t");
			
			StringTokenizer token = new StringTokenizer(name);

			int noParamInLogFile = token.countTokens()-1;
			ArrayList<Double>[] dists = new ArrayList[NAMES.length];
			for (int i = 0; i < dists.length; i++) {
				dists[i] = new ArrayList<Double>();
			}
			
			while ((input = in.readLine()) != null) {
				token = new StringTokenizer(input);
				token.nextToken(); //skip noIte

				double theta = 1;
				for (int i = 0; i < noParamInLogFile; i++) {
					double temp = Double.parseDouble(token.nextToken());
					theta *= temp;
					dists[i].add(temp)	;
				}
				dists[noParamInLogFile].add(theta);  

			}
			
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < dists.length; i++) {
				double[] values = Doubles.toArray( dists[i]);
				
				double burnin = 0.1;
				int start = (int) (values.length * burnin);
				double[] newValues = Arrays.copyOfRange(values, start, values.length);
				
				TraceDistribution td = new TraceDistribution(newValues, 0.95);
				double hpdlower = td.getLowerHPD();
				double hpdupper = td.getUpperHPD();
				double ess = td.getESS();
				
				boolean isGood = checkHPD(i, hpdlower, hpdupper); 
				if(isGood){
					count[i]++;
				}
				sb.append(ess).append("\t").append(hpdlower).append("\t").append(hpdupper).
					append("\t").append(isGood).append("\t");
			
			}
			
			sb.append("\n");
			fout.write(sb.toString());
//			fout.flush();
			
			in.close();
//			fout.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}

	private static boolean checkHPD(int index, double hpdlower, double hpdupper) {
//		index == 0 mu
//		index == 1 popsize
//		index == 2 theta

		boolean isGood = false;
		switch(index){
			case 0:
				isGood = hpdlower < 1e-5 & hpdupper > 1e-5; 
//				System.out.println(index +"\t"+ hpdlower +"\t"+ (hpdlower < 1e-5) +"\t"+  hpdupper +"\t"+  (hpdupper > 1e-5) +"\t"+ isGood);
				break;
			case 1:
				isGood = hpdlower < 3000 & hpdupper > 3000;
//				System.out.println(index +"\t"+ hpdlower +"\t"+ (hpdlower < 3000) +"\t"+  hpdupper +"\t"+  (hpdupper > 3000) +"\t"+ isGood);
				break;
			case 2:
				isGood = hpdlower < 0.03 & hpdupper > 0.03;
//				System.out.println(index +"\t"+ hpdlower +"\t"+ (hpdlower < 3e-2) +"\t"+  hpdupper +"\t"+  (hpdupper > 3e-2) +"\t"+ isGood);
				break;
		}

		return isGood;
				
		
	}

}
