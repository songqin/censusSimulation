//attackerType==8 && nonAttackerType==1
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.io.*;
import java.text.DecimalFormat;

public class BatchA8N2v2{

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

			int numNei=1; //numNei
			int neiSize=100;
			int ob=0;
			String pathOfCpt="./cpt.txt";
			int n =10000;//100000 is good
			long startTime = System.currentTimeMillis();
			double nonAttackerWitness=0;
			double nonattackerPercentage = Double.parseDouble(args[0]);
			String process="";
			int attackerType=8;//a1n2
			int nonAttackerType=2;
			double attackerwitnessDown=0;
			double attackerWitnessUp=0;
			String filename="nonAttackerWitness"+new DecimalFormat("#0.00").format(nonAttackerWitness)+
			"attackerWitness"+new DecimalFormat("#0.00").format(attackerWitnessUp);
			process="java DcpV2 "+numNei+" "+neiSize+" "+nonattackerPercentage+" "+nonAttackerWitness
			+" "+ob+" "+n+" "+pathOfCpt+" "+attackerType+" "+nonAttackerType+" "+attackerWitnessUp +" "+attackerwitnessDown +" "+filename;
			System.out.println("#process: "+process);
			runProcess(process);
			long endTime   = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			System.out.println("#time in minutes: "+ totalTime/1000.0/60);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
