import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Batch1 {

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

			int neiSize=Integer.parseInt(args[0]); //numNei
			// int neiSize=Integer.parseInt(args[1]); // neiSize
			// double hap=Double.parseDouble(args[2]);//hap
			// double map=Double.parseDouble(args[3]);//map
			// double fap=Double.parseDouble(args[4]);//fap
			// double hawp=Double.parseDouble(args[5]);//hawp
			// double mawp=Double.parseDouble(args[6]);//mawp
			// double fawp=Double.parseDouble(args[7]);//fawp
			// int ob=Integer.parseInt(args[8]);//ob
			// int n=Integer.parseInt(args[9]);//mcmc rounds
			// String pathOfCpt=args[10];//pathOfCpt
			// String chaningPara=args[11];//which para to vary. hap, map, etc.
			// double chaningVar=0.05;
			int dummy=1;
			int numNei=1;
			// int neiSize=10;//number of residents per neighborhood
			double hap=0.05;//honest agent percentage (eligible=1 and reliable=1)
			double map=0.05;//malicious agent percentage (not reliable(0), but eligible(1))
			double fap=0.05;//false agent percentage(not eligible (0), reliable(2 ,unknown,inactive))
			double hawp=0.05;//honest agent witness percentage
			double mawp=0.05;//malicious agent witness percentage
			double fawp=0.05;//fake agnet witness percentage
			int ob=0;//id of the observer			
			int n=1000;//mcmc round
			String pathOfCpt="./cpt.txt";
			long startTime = System.currentTimeMillis();
			String process="";
			while(dummy<=neiSize){
				process="java Dcp "+numNei+" "+neiSize+" "+hap+" "+map+" "+fap+" "+hawp+" "+
				mawp+" "+fawp+" "+ob+" "+n+" "+pathOfCpt+" "+dummy;
				runProcess(process);
				dummy++;
			}
			long endTime   = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			System.out.println("#time in minutes: "+ totalTime/1000.0/60);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
