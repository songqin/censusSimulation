//attackerType==8 && nonAttackerType==1
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.io.*;
import java.text.DecimalFormat;

public class BatchPDCP{

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
	//java BatchPDCP
	public static void main(String[] args) {
		try {

			int numNei=1; //numNei 200000
			int neiSize=10000;
			int ob=0;
			String pathOfCpt="./cpt.txt";
			int n =100;//100000 is good, reduced to 1000
			long startTime = System.currentTimeMillis();
			double nonAttackerWitness=0.05/100;
			int nAttackersPerNei = 2;
			// Integer.parseInt(args[0]);//number of attackers per neighborhood
			int nHacs = 0;//number of hacs change from 0.10,20.30
			String process="";
			int nMCMC=100;
			int type=2;
			while(nAttackersPerNei<=6){//5*6=30
				while(nHacs<=30){
					//PDCPExp1, PDCPWWWExp2,PDCPExp3, PDCPWWWExp4
					//WWW_E1_, WWW_E2_, WWW_E4_
					String filename = "WWW_E3_"+nHacs+"_"+nAttackersPerNei;
					process="java PDCPExp3 100 100 "+nAttackersPerNei+" "+nHacs+" "+nMCMC+" "+type+" "
					+filename;
					System.out.println("#process: "+process);
					runProcess(process);
					nHacs+=10;
				}
				nHacs=0;
				nAttackersPerNei+=2;
			}
			long endTime   = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			System.out.println("#time in minutes: "+ totalTime/1000.0/60);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
