//attackerType==8 && nonAttackerType==1
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.io.*;
import java.text.DecimalFormat;

public class BatchA1N1Corrupted{

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

			int numNei=1; //numNei 200000
			int neiSize=10;
			int ob=0;
			String pathOfCpt="./cpt.txt";
			int n =100;//100000 is good, reduced to 1000
			long startTime = System.currentTimeMillis();
			double nonAttackerWitness=0;
			double nonattackerPercentage = Double.parseDouble(args[0]);
			String process="";
			double attackerWitnessUp=1;
			// System.out.println(attackerWitnessUp);
			String filename=
			"attackerWitness"+new DecimalFormat("#0.00").format(attackerWitnessUp);
			process="java DCPCorruptedNeighborhood "+numNei+" "+neiSize+" "+nonattackerPercentage+" "+nonAttackerWitness
			+" "+ob+" "+n+" "+pathOfCpt+" 1 1 "+attackerWitnessUp + " 0 "+filename;
			System.out.println("#process: "+process);
			runProcess(process);
			// while(attackerWitnessUp<=1.01/100){
			// 	String filename=
			// 	"attackerWitness"+new DecimalFormat("#0.000").format(attackerWitnessUp);
			// 	process="java DcpV2 "+numNei+" "+neiSize+" "+nonattackerPercentage+" "+nonAttackerWitness
			// 	+" "+ob+" "+n+" "+pathOfCpt+" 1 1 "+attackerWitnessUp + " 0 "+filename;
			// 	System.out.println("#process: "+process);
			// 	runProcess(process);
			// 	attackerWitnessUp+=0.1/100;//100;//0.05(used)
			// 	System.out.println(attackerWitnessUp);
			// }
			// while((hap+map+fap <= 1) && (hawp+mawp+fawp<1)){
			// 	if(chaningPara.equals("hap")){
			// 		hap=chaningVar;
			// 	}
			// 	else if(chaningPara.equals("map")){
			// 		map=chaningVar;
			// 	}
			// 	else if(chaningPara.equals("fap")){
			// 		fap=chaningVar;
			// 	}
			// 	else if(chaningPara.equals("hawp")){
			// 		hawp=chaningVar;
			// 	}
			// 	else if(chaningPara.equals("mawp")){
			// 		mawp=chaningVar;
			// 	}
			// 	else if(chaningPara.equals("fawp")){
			// 		fawp=chaningVar;
			// 	}
			// 	process="java Dcp "+numNei+" "+neiSize+" "+hap+" "+map+" "+fap+" "+hawp+" "+
			// 	mawp+" "+fawp+" "+ob+" "+n+" "+pathOfCpt;
			// 	runProcess(process);
			// 	chaningVar+=0.05;

			// }


			long endTime   = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			System.out.println("#time in minutes: "+ totalTime/1000.0/60);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}