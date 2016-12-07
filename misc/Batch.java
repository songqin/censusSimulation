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

			int numNei=Integer.parseInt(args[0]); //numNei
			int neiSize=Integer.parseInt(args[1]); // neiSize
			double hap=Double.parseDouble(args[2]);//hap
			double map=Double.parseDouble(args[3]);//map
			double fap=Double.parseDouble(args[4]);//fap
			double hawp=Double.parseDouble(args[5]);//hawp
			double mawp=Double.parseDouble(args[6]);//mawp
			double fawp=Double.parseDouble(args[7]);//fawp
			int ob=Integer.parseInt(args[8]);//ob
			int n=Integer.parseInt(args[9]);//mcmc rounds
			String pathOfCpt=args[10];//pathOfCpt
			String chaningPara=args[11];//which para to vary. hap, map, etc.
			double chaningVar=0.05;
			// int numNei=1;
			// int neiSize=100;//number of residents per neighborhood
			// double hap=0.05;//honest agent percentage (eligible=1 and reliable=1)
			// double map=0.05;//malicious agent percentage (not reliable(0), but eligible(1))
			// double fap=0.05;//false agent percentage(not eligible (0), reliable(2 ,unknown,inactive))
			// double hawp=0.05;//honest agent witness percentage
			// double mawp=0.05;//malicious agent witness percentage
			// double fawp=0.05;//fake agnet witness percentage
			// int ob=0;//id of the observer			
			// int n=5000;//mcmc round
			// String pathOfCpt="./cpt.txt";
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
			while((hap+map+fap <= 1) && (hawp+mawp+fawp<1)){
				if(chaningPara.equals("hap")){
					hap=chaningVar;
				}
				else if(chaningPara.equals("map")){
					map=chaningVar;
				}
				else if(chaningPara.equals("fap")){
					fap=chaningVar;
				}
				else if(chaningPara.equals("hawp")){
					hawp=chaningVar;
				}
				else if(chaningPara.equals("mawp")){
					mawp=chaningVar;
				}
				else if(chaningPara.equals("fawp")){
					fawp=chaningVar;
				}
				process="java Dcp "+numNei+" "+neiSize+" "+hap+" "+map+" "+fap+" "+hawp+" "+
				mawp+" "+fawp+" "+ob+" "+n+" "+pathOfCpt;
				runProcess(process);
				chaningVar+=0.05;

			}
			 // 1 100 0.3 0.1 0.1 0.1 0.1 0.1 0 7 ./cpt.txt";
			

			String process1="java -cp .:./gson-2.2.2.jar MCMC/MCMC MCMC 10 MCMC/witnessStancesBig.txt MCMC/cptv2.txt none 1 10000";
			String windowsProcess1="java -classpath .;gson-2.2.2.jar MCMC.MCMC MCMC 100 MCMC\\witnessStancesBig.txt MCMC\\cptv2.txt none 1 10000";
//			String process1="java -classpath . MCMC/MCMC MCMC 1 MCMC/witnessStances.txt MCMC/cptv2.txt none ";
			// runProcess(process);
			long endTime   = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			System.out.println("#time in minutes: "+ totalTime/1000.0/60);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
