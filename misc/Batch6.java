//attackerType==8 && nonAttackerType==1
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Batch6{

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
			int n =1000;
			long startTime = System.currentTimeMillis();
			double nonattackerPercentage=0;
			double nonAttackerWitness=0.1;
			String process="";
			while(nonattackerPercentage<=1){
				process="java DcpV2 "+numNei+" "+neiSize+" "+nonattackerPercentage+" "+nonAttackerWitness
				+" "+ob+" "+n+" "+pathOfCpt+" 8 1 0 0";
				System.out.println("#process: "+process);
				runProcess(process);
				nonattackerPercentage+=0.1;
			}
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
