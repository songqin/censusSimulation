import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Batch {

	private static void printLines(String name, InputStream ins)
			throws Exception {
		String line = null;
		BufferedReader in = new BufferedReader(new InputStreamReader(ins));
		while ((line = in.readLine()) != null) {
			System.out.println(name + " " + line);
		}
	}

	private static void runProcess(String command) throws Exception {
		Process pro = Runtime.getRuntime().exec(command);
		printLines("",pro.getInputStream());
//		printLines(command + " stdout:", pro.getInputStream());
//		printLines(command + " stderr:", pro.getErrorStream());
		pro.waitFor();
		// System.out.println(command + " exitValue():  " + pro.exitValue());
	}

	public static void main(String[] args) {
		try {
			int numNei=1;
			int neiSize=10000;//number of residents per neighborhood
			double hap=0.01;//honest agent percentage (eligible=1 and reliable=1)
			//5%, 10%,15%,20% 4^5=
			double fap=0.01;//false agent percentage(not eligible (0), reliable(2 ,unknown,inactive))
			double map=0.01;//malicious agent percentage (not reliable(0), but eligible(1))
			double mawp=0.01;//malicious agent witness percentage
			double hawp=0.01;//honest agent witness percentage
			double fawp=0.01;//fake agnet witness percentage
			int ob=0;//id of the observer			
			int n=10000;//mcmc round
			String pathOfCpt="./cpt.txt";
			long startTime = System.currentTimeMillis();
//			runProcess("javac Main.java");

//				#para:i number of witnesser/reviewers 
//			for(int i=2;i<=21;i++){
//				String process0="java -classpath . MCMC/printmybn "+i;
//				runProcess(process0);
//				//on windows OSopen, path should be MCMC\\mybn.txt
//			}
			//1000000 to 100
			//size of witnessStance: 3095927
			// 1 10000  runtime:
			//10001 20001	runtime:
			//
			//
			// String process="java -cp .:./gson-2.2.2.jar MCMC/MCMC MCMC 10 MCMC/witnessStancesBig.txt MCMC/cptv2.txt none ";
//			310 processes
//			for(int i=1;i<3095927;i+=10001){
//				if((i+10000)>3095927)
//					System.out.println(process+i+" "+ 3095927);
//				else
//					System.out.println(process+i+" "+ (i+10000));
//			}

			String process="";
			for(int i=1;i<=1;i++){
				// n=i;
				process="java Dcp "+numNei+" "+neiSize+" "+hap+" "+fap+" "+map+" "+mawp+" "+
				hawp+" "+fawp+" "+ob+" "+n+" "+pathOfCpt+" "+n;
				runProcess(process);

			}
			 // 1 100 0.3 0.1 0.1 0.1 0.1 0.1 0 7 ./cpt.txt";
			

			String process1="java -cp .:./gson-2.2.2.jar MCMC/MCMC MCMC 10 MCMC/witnessStancesBig.txt MCMC/cptv2.txt none 1 10000";
			String windowsProcess1="java -classpath .;gson-2.2.2.jar MCMC.MCMC MCMC 100 MCMC\\witnessStancesBig.txt MCMC\\cptv2.txt none 1 10000";
//			String process1="java -classpath . MCMC/MCMC MCMC 1 MCMC/witnessStances.txt MCMC/cptv2.txt none ";
			// runProcess(process);
			long endTime   = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			// System.out.println("time in minutes: "+ totalTime/1000.0/60);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
