package sw.zold;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.commons.io.filefilter.FileFilterUtils;

import sw.main.Setup;

@SuppressWarnings("unused")
public class OldMain {
	
	private static double[] readBrLn(Setup setting) throws IOException {
		int noSeqPerTime = setting.getNoSeqPerTime();
		double[] record = new double[noSeqPerTime * setting.getNoTime() - 1];

		BufferedReader in = new BufferedReader(new FileReader(
				setting.getWorkingDir() + "jt.log"));
		String line;
		in.readLine();
		int[] logInfo = new int[4];

		int noBranch = noSeqPerTime + 1;
		int count = 0;
		while ((line = in.readLine()) != null) {
			StringTokenizer st = new StringTokenizer(line);
			for (int j = 0; j < logInfo.length; j++) {
				logInfo[j] = Integer.parseInt(st.nextToken());
			}
			if (logInfo[3] != noBranch) {
				int diff = noBranch - logInfo[3];
				noBranch = logInfo[3];
				for (int j = 0; j < diff; j++) {
					record[count] = logInfo[1];
					count++;
				}

			}
		}

		return record;
	}

	private static void cleanUpFiles(String dataDir) {

		File dir = new File(dataDir);
		String[] suffix = new String[] { "bat", "gen", "trees", "csv", "paup",
				"par" };
		for (int i = 0; i < suffix.length; i++) {
			FileFilter filter = FileFilterUtils.suffixFileFilter(suffix[i]);
			File[] allFiles = dir.listFiles(filter);
			for (File file : allFiles) {
				file.delete();
			}
		}
	}

}
