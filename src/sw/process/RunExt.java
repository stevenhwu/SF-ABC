package sw.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

public class RunExt {


	static final String workDir = System.getProperty("user.dir");
	static final String fileSep = System.getProperty("file.separator");



	File dataDir;
	String[] command;
	private String progName;

	public RunExt(File dataDir) {
		setDir(dataDir);
	}

	public RunExt(File dataDir, String progName, String parFile, String noRep) {
		setDir(dataDir);
		setPar(progName, parFile, noRep);
	}

	public RunExt(String dataDirName) {
		setDir(dataDirName);
	}

	public RunExt(String dataDirName, String progName, String parFile, String noRep) {
		setDir(dataDirName);
		setPar(progName, parFile, noRep);
	}

	public RunExt() {
		setDir(workDir);
	}
	@Deprecated
	public void setPar(String parFile) {
		setPar("./BCC", parFile, "1");
	}
	@Deprecated
	public void setPar(String parFile, String noRep) {
		setPar("./BCC", parFile, noRep);
	}
	public void setPar(String progName, String parFile, String noRep) {
		setPar(progName, parFile, noRep, "-p", "-f");
	}
	
	public void setPar(String progName, String parFile, String noRep, String...  switchPar ) {

		this.progName = progName;
		File fProg = new File(dataDir.getAbsoluteFile()+fileSep+progName);
		if(!fProg.canExecute()){
			System.out.println("Set program \""+ progName +"\" executable: "+fProg.setExecutable(true));			
		}

		command = new String[switchPar.length+3];
		command[0] = "./"+progName;
		for (int i = 0; i < switchPar.length; i++) {
			command[i+1] = switchPar[i];
		}
		command[switchPar.length+1] = parFile;
		command[switchPar.length+2] = noRep;

	}


	public String[] getCommand() {
		return command;
	}

	public void changeParFile(String parFile) {
		if(command.length>3){
			command[command.length-2] = parFile;
		}
	}

	public void changeRep(String noRep) {
		if(command.length>3){
			command[command.length-1] = noRep; 
		}
	}

	private void setDir(File userDir) {
		this.dataDir = userDir;
		if (!dataDir.exists()) {
			try {
				dataDir.mkdirs();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void setDir(String dataDirName) {
		dataDir = new File(workDir+ fileSep+dataDirName+fileSep);
		setDir(dataDir);
	}

	public void run() {

		try {
			
			ProcessBuilder pb = new ProcessBuilder(command);  

			pb = pb.directory(dataDir);  
//			File temp = pb.directory();  
//			String currentWorkingDirectory = "Current working directory: " + temp.toString();
//			System.out.println(currentWorkingDirectory);
//			
			Process p =pb.start();
			p.waitFor();
		       
			
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
		    String line;
//		    System.out.println("&&&&& start &&&&&");
//		    while ((line = br.readLine()) != null) {
//		    	System.out.println(line);
//		    }
//		     
//		    br = new BufferedReader(new InputStreamReader(p.getErrorStream()));
//		    while ((line = br.readLine()) != null) {
//		    	System.out.println(line);
//		    }
//		       System.out.println("&&&&& end &&&&&");
        	  p.getOutputStream().close();
        	  p.getErrorStream().close();
        	  p.getInputStream().close();
        	  
//			p.destroy();
//			System.out.println(System.currentTimeMillis());
//			p.waitFor();

//			System.out.println(System.currentTimeMillis());
//			System.out.println(System.nanoTime());
//			System.out.println(System.nanoTime());
//			System.out.println(System.currentTimeMillis());
		}  
		catch (Exception e) {  
			e.printStackTrace();
		}  
	}

//	public void runCheck(){
//		try {
////			p.waitFor();
////			p.destroy();
//		} catch (InterruptedException e) {

//			e.printStackTrace();
//		}
//	}
	public void testLock(String testFile) {

		try {

			String[] command2 = new String[]{"lsof", testFile};
			ProcessBuilder pbTeste = new ProcessBuilder(command2);  
			pbTeste = pbTeste.directory(dataDir);  
			Process pTest = pbTeste.start(); 
			String line;
			//			InputStream is = ;
			//			InputStreamReader isr = ;
			BufferedReader br = new BufferedReader(new InputStreamReader(pTest.getInputStream()));
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			} 
			br.close();

		}  
		catch (Exception e) {  
			e.printStackTrace();
		}  

	}


}


//                RandomAccessFile fis = new RandomAccessFile(result, "rw");
//System.out.println("access");
//                System.out.println(fis.length());
//                
////                fis.getChannel().close();
//                System.out.println(fis.getChannel().isOpen());
//                System.out.println(fis.getChannel().size());
//                
//                long t = 1;
//                
//                System.out.println(fis.getChannel().size());
//                FileLock fl = fis.getChannel().lock();
//                System.out.println("Test FileLock");
//                System.out.println(fl.isShared());
//                
//                System.out.println(fl.isValid());
//                
//                OutputStream stdin = p.getOutputStream ();
//                InputStream stderr = p.getErrorStream ();
//                InputStream stdout = p.getInputStream ();
//            
//                // "write" the parms into stdin
//                line = "param1" + "\n";   
//                stdin.write(line.getBytes() );
//                stdin.flush();
//
//                line = "param2" + "\n";
//                stdin.write(line.getBytes() );
//                stdin.flush();
//
//                line = "param3" + "\n";
//                stdin.write(line.getBytes() );
//                stdin.flush();
//
//                stdin.close();
//                
//                // clean up if any output in stdout
//                BufferedReader brCleanUp = 
//                  new BufferedReader (new InputStreamReader (stdout));
//                while ((line = brCleanUp.readLine ()) != null) {
//                  System.out.println ("[Stdout] " + line);
//                }
//                brCleanUp.close();
//                
//                // clean up if any output in stderr
//                brCleanUp = 
//                  new BufferedReader (new InputStreamReader (stderr));
//                while ((line = brCleanUp.readLine ()) != null) {
//                  System.out.println ("[Stderr] " + line);
//                }
//                brCleanUp.close();

