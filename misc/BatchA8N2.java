//attackerType==8 && nonAttackerType==1
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.io.*;
import java.text.DecimalFormat;

public class BatchA8N2{

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
			int n =10000;
			long startTime = System.currentTimeMillis();
			double nonAttackerWitness=0;
			double nonattackerPercentage = 0;
			 // Double.parseDouble(args[0]);
			String process="";
			String fn="A8N2nonAttacker";
			while(nonattackerPercentage<=1){
				String filename=fn+""+new DecimalFormat("#0.00").format(nonattackerPercentage);
				process="java DcpV2 "+numNei+" "+neiSize+" "+nonattackerPercentage+" "+0
				+" "+ob+" "+n+" "+pathOfCpt+" 8 2 0 0"+" "+filename;
				System.out.println("#process: "+process);
				runProcess(process);
				nonattackerPercentage+=0.1;
			}
			long endTime   = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			System.out.println("#time in minutes: "+ totalTime/1000.0/60);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
