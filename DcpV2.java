//decentralized census problem
import java.util.*;
import java.io.*;

public class DcpV2{
	int fast=1;
	int printLess=1;
	int numNei;//number of neighborhoods in DCP
	int neiSize;//number of residents per neighborhood
	int p;//population
	double nonAttackerPercentage;//honest agent percentage (eligible=1 and reliable=1)
	double attackerPercentage;//malicious agent percentage (not reliable(0), but eligible(1))
	int ob;//id of the observer
	int nha=0;//number of honest agents
	int nfa=0;//number of false agents
	int nma=0;//number of malicious agents
	int nea=0;//number of eligible agents
	int nra=0;//number of reliable agents
	int nnea=0;//number of not-eligible agents
	int nnra=0;//number of not-reliable agents	
	// note: n - nha-nfa-nma is inactive aligible agents
	String pathOfCpt;//the file path of the cpt learned from big data
	final public static int RW = 1;
	final public static int CS = 2;
	final public static int T = 1;
	final public static int F = 0;
	double prior[];
	double CPT_CS[][]; // CPT[cs_a,rw_b] Conditional Probability Table for eligibility
	// HashMap<String, Integer> witnessMapCS;
	HashMap<String, List<String>> reviewerToProductAndVote;//e.g., id1->("id2 0")
	HashMap<String, List<String>> productToReviewerAndVote;//

	HashMap<String, Integer> stateMapRW ;
	HashMap<String, Integer> stateMapCS ;
	HashMap<String, Boolean> fixedMapRW ;
	HashMap<String, Boolean> fixedMapCS ;
	//vector for counting reliable
	HashMap<String, Integer> NRWMapT;
	//vector for counting unreliable
	HashMap<String, Integer> NRWMapF;
	//vector for counting eligible
	HashMap<String, Integer> NCSMapT;
	//vector for counting ineligible
	HashMap<String, Integer> NCSMapF ;
	
	HashMap<String, Agent> idToAgents ;
	int fixedCS = 0;	
	int fixedRW = 0;	
	int mcmcRounds=0;
	int attackerType;
	int nonAttackerType;
	// int c;//parameter passed from Batch class
	static Random rnd = new Random(0);
	// load()
	// init() 
	// mcmc()
	ArrayList<Neighborhood> neighborhoods;
	public static float random(float max) {
		float result = rnd.nextFloat() * max;
		return result;
	}	
	//constructor
	//n:a reference number
	DcpV2(int numNei, int rNum, double attackerPercentage, double nonAttackerPercentage,  Integer ob, int mcmcRounds, String pathOfCpt, 
		int attackerType, int nonAttackerType){
		this.numNei = numNei;
		this.neiSize = (int)(rNum*(attackerPercentage+nonAttackerPercentage));
		this.p = numNei * neiSize;
		this.attackerPercentage = attackerPercentage;
		this.nonAttackerPercentage = nonAttackerPercentage;
		this.ob=ob;
		this.pathOfCpt = pathOfCpt;
		this.mcmcRounds = mcmcRounds;
		this.attackerType=attackerType;
		this.nonAttackerType=nonAttackerType;
		idToAgents = new HashMap<String, Agent>();
		readCPT();//load cpt
		// printCPT();
		reviewerToProductAndVote=new HashMap<String, List<String>>();
		productToReviewerAndVote=new HashMap<String, List<String>>();

		stateMapRW = new HashMap<String, Integer>();
		stateMapCS = new HashMap<String, Integer>();
		fixedMapRW = new HashMap<String, Boolean>();
		fixedMapCS = new HashMap<String, Boolean>();
		NRWMapT = new HashMap<String, Integer>();
		//vector for counting not-helpful 
		NRWMapF = new HashMap<String, Integer>();
		//vector for counting good quality 
		NCSMapT = new HashMap<String, Integer>();
		//vector for counting bad quality
		NCSMapF = new HashMap<String, Integer>();
		neighborhoods = new ArrayList<Neighborhood>();
		for(int i=1; i<= numNei;i++){
			// 1 100
			// 101 200
			// 201 300
			Neighborhood n = new Neighborhood(i, neiSize, attackerPercentage, nonAttackerPercentage, this.prior[CS], this.prior[RW],
				attackerType, nonAttackerType
			 );
			neighborhoods.add(n);
		}
		// printAllAgents();




		// for(Neighborhood n: neighborhoods){
		// 	HashMap<String, Agent> m= n.idToAgents;
		// 	for(String k: m.keySet()){
		// 		idToAgents.put(k, m.get(k));
		// 		Agent a = m.get(k);
		// 		if(a.role==1) nha++;
		// 		else if(m.get(k).role==2) nfa++;
		// 		else if(m.get(k).role==3) nma++;
		// 		if(a.role!=2) nea++;
		// 		if(a.role!=3) nra++;
		// 		if(a.role==2) nnea++;
		// 		if(a.role==3) nnra++;
		// 	}
		// }

		// printNeighborhoodAndAgents();
		populateToyWitnessStances();
		// populateWitnessStances();
		// printWitness();
		mcmc(mcmcRounds);
		// printNeighborhoodAndAgents();
		printAllAgents();
		// printTprFpr(mcmcRounds);
		
		// printOneAgent(1);
		// test();


	}
	void printCPT(){
		System.out.println("#CPT_CS[T][T] "+CPT_CS[T][T]);
		System.out.println("#CPT_CS[T][F] "+CPT_CS[T][F]);
		System.out.println("#CPT_CS[F][T] "+CPT_CS[F][T]);
		System.out.println("#CPT_CS[F][F] "+CPT_CS[F][F]);
		System.out.println("#prior RW:"+prior[RW]);		
		System.out.println("#prior CS:"+prior[CS]);	
		System.out.println("#honest agents:"+nha+" " + nha*1.0/p*100+"%");
		System.out.println("#fake agents:"+nfa+" "  +nfa*1.0/p*100+"%");
		System.out.println("#malicios agents:"+nma+" " +nma*1.0/p*100+"%");
		System.out.println("#inactive agents:"+(p-nha-nfa-nma)+" " +(p-nha-nfa-nma)*1.0/p*100+"%");
	}

	void printOneAgent(String id){
		//role=1 honest agents (eligible=1, reliable=1), 
		//role=2 fake agents (reliable= 1 -
		//(want to use reliability to hide the fact of being fake), eligible=0)
		//role=3 malicious agents (reliable=0, eligible=1)
		//role=0 inactive agents (eligible=1 , reliable = 1)		
		// System.out.println("Computing agent's statistics");
		for(Agent a: idToAgents.values()){
			if(a.id.equals(id))
			{
				System.out.println("#"+
							a.id+" "+
							a.role+" "+
							a.e_binary+" "+
							a.r_binary+" "+
							a.e_prob+" "+
							a.r_prob+" "+
							a.csUpvotedCount+" "+
							a.csDownvotedCount+" "+
							a.csUpvoteCount+" "+
							a.csDownvoteCount
							);
			}
		}	
	}
	void printAllAgents(){
		//role=1 honest agents (eligible=1, reliable=1), 
		//role=2 fake agents (reliable= 1 -
		//(want to use reliability to hide the fact of being fake), eligible=0)
		//role=3 malicious agents (reliable=0, eligible=1)
		//role=0 inactive agents (eligible=1 , reliable = 1)		
		// System.out.println("Computing agent's statistics");
		System.out.println();
		for(Agent a: idToAgents.values()){
			System.out.println("#"+
				a.id+" "+
				a.role+" "+
				a.e_binary+" "+
				a.r_binary+" "+
				a.e_prob+" "+
				a.r_prob+" "+
				a.csUpvotedCount+" "+
				a.csDownvotedCount+" "+
				a.csUpvoteCount+" "+
				a.csDownvoteCount
				);
		}	
	}
	// void printNeighborhoodAndAgents(){
	// 	for(Neighborhood n: neighborhoods){
	// 		System.out.println("#neighborhood: "+n.id);
	// 		HashMap<String, Agent> m = n.idToAgents;
	// 		Collection<Agent> agents = m.values();
	// 		System.out.println("#id role e_binary r_binary e_prob r_prob");
	// 		for (Agent a : agents) {
	// 			System.out.println("#"+
	// 				a.id+" "+
	// 				a.role+" "+
	// 				a.e_binary+" "+
	// 				a.r_binary+" "+
	// 				a.e_prob+" "+
	// 				a.r_prob
	// 				);
	// 		}
	// 	}
	// }
	void printTprFpr(int round){
		double tCS=0;//expectation of CS being true
		double fCS=0;
		double tRW=0;
		double fRW=0;
		double l=0;
		double k=0;
		// System.out.println("id, role, e_binary,r_binary,e_prob, r_prob");
		for(Agent a: idToAgents.values()){
			if(a.e_binary==1)
				tCS+=a.e_prob;
			else if (a.e_binary==-1){
				tCS+=0;
			}
			else{
				fCS+=a.e_prob;
			}

			if(a.r_binary==1)
				tRW+=a.r_prob;
			else if (a.r_binary==-1){
				tRW+=0;
			}
			else
				fRW+=a.r_prob;
			l+=a.e_prob;
			k+=a.e_prob;
		}	
		System.out.println("#expectation: CS(true), CS(false), RW(true), RW(false)");
		System.out.println("#"+tCS+" "+fCS+" "+tRW+" "+fRW);
		System.out.println("#-----------------------");
		System.out.println("#"+"nea nnea nra nnra");
		System.out.println("#"+nea+" "+ nnea+" " +nra+" "+ nnra );
		System.out.println("#-----------------------");
		System.out.println("#tpr(CS) fpr(CS) tpr(RW) fpr(RW)");
		System.out.println(round+" "+tCS*1.0/nea+" "+fCS*1.0/nnea+" "+tRW*1.0/nra+" "+fRW*1.0/nnra);
		System.out.println("#-----------------------");
		System.out.println(l + " " +k);
	}
	void printWitness(){
		System.out.println("#--------------------------------");
		System.out.println("#reviewerToProductAndVote");
		for(String key:reviewerToProductAndVote.keySet()){
			System.out.println("#"+key+" "+reviewerToProductAndVote.get(key));
		}
		System.out.println("#--------------------------------");
		System.out.println("#productToReviewerAndVote:");
		for(String key:productToReviewerAndVote.keySet()){
			System.out.println("#"+key+" "+productToReviewerAndVote.get(key));
		}
		System.out.println("#--------------------------------");
		System.out.println("#NRWMapT:");
		for(String key:NRWMapT.keySet()){
			System.out.println("#"+key+" "+NRWMapT.get(key));
		}
		System.out.println("#--------------------------------");
		System.out.println("#NCSMapT:");
		for(String key:NCSMapT.keySet()){
			System.out.println("#"+key+" "+NCSMapT.get(key));
		}
		System.out.println("#--------------------------------");
		System.out.println("#stateMapCS:");
		for(String key:stateMapCS.keySet()){
			System.out.println("#"+key+" "+stateMapCS.get(key));
		}
		System.out.println("#--------------------------------");
		System.out.println("#stateMapRW:");
		for(String key:stateMapRW.keySet()){
			System.out.println("#"+key+" "+stateMapRW.get(key));
		}
		System.out.println("#--------------------------------");		
	}

	void readCPT(){
		//populate the CPT and priors from what's learned from big data
		Scanner s = null;
		BufferedReader in = null;		
		try {
			in = new BufferedReader(new FileReader(this.pathOfCpt));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		s = new Scanner(in);
		CPT_CS = new double[2][2];
		prior = new double[3];
//		CPT_RW = new double[2][2];
//		CPT_self_CS = new double[2][2];
//		CPT_self_RW = new double[2];
//		CPT_observer_self_RW = new double[2];
//		CPT_observer_self_CS = new double[2][2];
//		CPT_observer_RW = new double[2][2];
//		CPT_observer_CS = new double[2][2];
		String line = s.nextLine();// Read the name of CPT
		// System.out.println(line);
		s.nextLine();// Read the headers of Table
		CPT_CS[T][T] = Double.parseDouble(s.nextLine().split(" ")[2]);
		CPT_CS[T][F] = Double.parseDouble(s.nextLine().split(" ")[2]);
		CPT_CS[F][T] = Double.parseDouble(s.nextLine().split(" ")[2]);
		CPT_CS[F][F] = Double.parseDouble(s.nextLine().split(" ")[2]);

		s.nextLine();// Read the name of CPT: Priors
		s.nextLine();// Read the headers of CPT
		String p = s.nextLine();
		prior[RW] = Double.parseDouble(p.split(" ")[0]);
		prior[CS] = Double.parseDouble(p.split(" ")[1]);
	}

	void populateToyWitnessStances(){
		// witnessMapCS = new HashMap<String, Integer>()
		for(int i=2;i<=10;i++){
			String aid=1+"_"+i;
			String bid="1_1";
			Integer vote=1;
			if(reviewerToProductAndVote.containsKey(aid)){
				reviewerToProductAndVote.get(aid).add(bid+" "+vote);
			}
			else{
				List<String> l = new ArrayList<String>();
				l.add(bid+" "+vote);
				reviewerToProductAndVote.put(aid, l);
			}
			if(productToReviewerAndVote.containsKey(bid)){
				productToReviewerAndVote.get(bid).add(aid+" "+vote);
			}
			else{
				List<String> l = new ArrayList<String>();
				l.add(aid+" "+vote);
				productToReviewerAndVote.put(bid, l);
			}
			// witnessMapCS.put(i+" " +1, 1);
		}
		// for(int i=2;i<=10;i++){
		// 	String aid=1+"_"+1+"";
		// 	String bid=1+"_"+i+"";
		// 	Integer vote=0;
		// 	if(reviewerToProductAndVote.containsKey(aid)){
		// 		reviewerToProductAndVote.get(aid).add(bid+" "+vote);
		// 	}
		// 	else{
		// 		List<String> l = new ArrayList<String>();
		// 		l.add(bid+" "+vote);
		// 		reviewerToProductAndVote.put(aid, l);
		// 	}
		// 	if(productToReviewerAndVote.containsKey(bid)){
		// 		productToReviewerAndVote.get(bid).add(aid+" "+vote);
		// 	}
		// 	else{
		// 		List<String> l = new ArrayList<String>();
		// 		l.add(aid+" "+vote);
		// 		productToReviewerAndVote.put(bid, l);
		// 	}
		// 	// witnessMapCS.put(1+" " +i, 0);
		// }

	}

	void populateWitnessStances(){
		// witnessMapCS = new HashMap<String, Integer>();
		// stateMapRW = new HashMap<String, Integer>();
		// stateMapCS = new HashMap<String, Integer>();
		// fixedMapRW = new HashMap<String, Boolean>();
		// fixedMapCS = new HashMap<String, Boolean>();
		// NRWMapT = new HashMap<String, Integer>();
		//vector for counting not-helpful 
		// NRWMapF = new HashMap<String, Integer>();
		//vector for counting good quality 
		// NCSMapT = new HashMap<String, Integer>();
		// vector for counting bad quality
		// NCSMapF = new HashMap<String, Integer>();
		for(Neighborhood n: neighborhoods){
			HashMap<String, Agent> m = n.neighbors;
			Collection<Agent> agents1 = m.values();
			Collection<Agent> agents2 = m.values();
			Collection<Agent> agents = m.values();
			if(attackerType==8 && nonAttackerType==2){//inactive 	
			}
		}
		// if(ob!=0){
		// 	for(String k: witnessMapCS.keySet()){
		// 		Integer product = Integer.parseInt(k.toString().split(" ")[1]);
		// 		if (witnessMapCS.containsKey(ob+" "+product)) {
		// 			fixedMapCS.put(product, true);
		// 			this.fixedCS++;
		// 			stateMapCS.put(product, witnessMapCS.get(ob+" "+product));
		// 		}
		// 	}
		// 	if (fixedMapRW.get(ob) == false) {
		// 		fixedMapRW.put(ob, true);
		// 		fixedRW++;
		// 		stateMapRW.put(ob, 1);
		// 	}
		// }		

	}
	// Run GIBBS-ASK(X, e, bn, N) for x rounds
	public void mcmc(int x) {
		for (int i = 0; i < x; i++) {
			// System.out.println("MCMC Round "+i);
			for(String k: NRWMapT.keySet()){
				if(!fixedMapRW.get(k)){
					mcmc_A_RW(k);
				}
			}
			for(String k: NCSMapT.keySet()){
				if(!fixedMapCS.get(k)){
					mcmc_A_CS(k);		
				}
			}
		}
		for(String k: NRWMapT.keySet()){
			int countT = NRWMapT.get(k);
			int countF = NRWMapF.get(k);
			idToAgents.get(k).r_prob= countT*1.0/(countT+countF);
			// rwMap.put(k, countT*1.0/(countT+countF));
		}
		for(String k: NCSMapT.keySet()){
			int countT = NCSMapT.get(k);
			int countF = NCSMapF.get(k);
			idToAgents.get(k).e_prob= countT*1.0/(countT+countF);
		}
		
	}	
	//inference of reliability random-var of a 
	public void mcmc_A_RW(String a) {
		double alpha_T = 1 * prior[RW];
		double alpha_F = 1 * (1 - prior[RW]);
		//if in file
		// if(rwMap!=null && rwMap.containsKey(a)){
		// 	double rw = rwMap.get(a);
		// 	alpha_T = 1 * rw;
		// 	alpha_F = 1 * (1 - rw);
		// }
		// double rw = idToAgents.get(a).r_prob;
		// double alpha_T = 1 * rw;
		// double alpha_F = 1 * (1 - rw);
		double[][] CPT_crt_CS = CPT_CS;
		if(reviewerToProductAndVote.get(a)!=null){
			List<String> list = reviewerToProductAndVote.get(a);
			for(int i=0; i<list.size(); i++) {
			   String o = list.get(i);
			   String product = o.split(" ")[0];
			   String vote=o.split(" ")[1];
				if(vote.equals("1")){
					int state_product = stateMapCS.get(product);
					alpha_T *= CPT_crt_CS[T][state_product];
					alpha_F *= CPT_crt_CS[F][state_product];
				}
				else{
					int state_product = stateMapCS.get(product);//efficiency
					alpha_T *= 1 - CPT_crt_CS[T][state_product];
					alpha_F *= 1 - CPT_crt_CS[F][state_product];
				}
			}			
		}
			
		stateMapRW.put(a, sample(alpha_T / (alpha_T + alpha_F)));
		if(stateMapRW.get(a)==1){
			int c=NRWMapT.get(a);
			c++;
			NRWMapT.put(a, c);
		}
		else{
			int c=NRWMapF.get(a);
			c++;
			NRWMapF.put(a, c);
		}
	}
	//inference of eligibility random-var of a
	public void mcmc_A_CS(String a) {
		double alpha_T = 1 * prior[CS];
		double alpha_F = 1 * (1 - prior[CS]);
		double[][] CPT_crt_CS = CPT_CS;
		if(productToReviewerAndVote.get(a)!=null){
			List<String> list = productToReviewerAndVote.get(a);
			for(int i=0; i<list.size(); i++) {
			   String o = list.get(i);
			   // System.out.println("o:"+o);
			   String reviewer = o.split(" ")[0];
			   String vote=o.split(" ")[1];
			   // System.out.println(vote);
			   // System.out.println(vote.equals("1"));
				if(vote.equals("1")){
					int state_reviewer = stateMapRW.get(reviewer);
					alpha_T *= CPT_crt_CS[T][state_reviewer];
					alpha_F *= CPT_crt_CS[F][state_reviewer];
				}
				else{
					int state_reviewer = stateMapRW.get(reviewer);
					alpha_T *= 1 - CPT_crt_CS[T][state_reviewer];
					alpha_F *= 1 - CPT_crt_CS[F][state_reviewer];
				}		   
			}
		}
		stateMapCS.put(a, sample(alpha_T / (alpha_T + alpha_F)));
		if(stateMapCS.get(a)==1){
			int c=NCSMapT.get(a);
			c++;
			NCSMapT.put(a, c);
		}
		else{
			int c=NCSMapF.get(a);
			c++;
			NCSMapF.put(a, c);
		}
	}
	public int sample(double d) {
		float v = random(1.0f);
		if (v < d)
			return T;
		return F;
	}	
	//constructor
	class Neighborhood{//Could be Buggy, review code ! 

		HashMap<String, Agent> neighbors;

		Neighborhood(int i, int neiSize, double nAttacker, double nNonAttacker, double priorCS, double priorRW,
			int attackerType, int nonAttackerType
		){
			neighbors = new HashMap<String, Agent>();
			int j=1;
			for(;j<=nAttacker;j++){
				String id = i+"_"+j;
				Agent attacker = new Agent(id,priorCS, priorRW);
				if(attackerType==8){
					attacker.role="attacker_8";
					attacker.e_binary=0;
					attacker.r_binary=-1;
					nnea++;
				}
				else{}
				neighbors.put(id, attacker);
				idToAgents.put(id, attacker);
				stateMapRW.put(id, (int) Math.floor(random(2)));//0 or 1
				stateMapCS.put(id, (int) Math.floor(random(2)));//0 or 1
				if(!fixedMapRW.containsKey(id))
					fixedMapRW.put(id, false);
				if(!fixedMapCS.containsKey(id))
					fixedMapCS.put(id, false);
				NRWMapT.put(id, 0);
				NRWMapF.put(id, 0);
				NCSMapT.put(id, 0);
				NCSMapF.put(id, 0);					
			}
			for(;j<=neiSize;j++){
				String id = i+"_"+j;
				Agent nonAttacker = new Agent(id,priorCS, priorRW);
				if(nonAttackerType==2){
					nonAttacker.role="nonAttacker_2";
					nonAttacker.e_binary=1;
					nonAttacker.r_binary=-1;
					nea++;			
				}
				neighbors.put(id, nonAttacker);
				idToAgents.put(id, nonAttacker);
				stateMapRW.put(id, (int) Math.floor(random(2)));//0 or 1
				stateMapCS.put(id, (int) Math.floor(random(2)));//0 or 1
				if(!fixedMapRW.containsKey(id))
					fixedMapRW.put(id, false);
				if(!fixedMapCS.containsKey(id))
					fixedMapCS.put(id, false);
				NRWMapT.put(id, 0);
				NRWMapF.put(id, 0);
				NCSMapT.put(id, 0);
				NCSMapF.put(id, 0);					
			}
		}
	}
	//constructor
	class Agent{
		String id;
		String role;
		double e_prob;//ramdom var for eligibility
		double r_prob;//random var for reliability
		int e_binary;//groud truth for eligibility 1/0
		int r_binary;//ground truth for reliability 1/0
		int csDownvoteCount;//numver of votes of the current agent casted to others
		int csUpvoteCount;
		int csDownvotedCount;//number of votes from others
		int csUpvotedCount;
		Agent(String id, double priorCS, double priorRW){//inactive agent by default
			this.id = id;
			this.role = "N/A";
			this.e_binary=-1;//0-ineligible, 1-eligible, -1-N/A
			this.r_binary=-1;//0-reliable, 1-not reliable, -1-N/A
			this.e_prob=priorCS;
			this.r_prob=priorRW;
			this.csDownvoteCount=0;
			this.csUpvoteCount=0;
			this.csDownvotedCount=0;
			this.csUpvotedCount=0;
		}
	}

	public static void main(String[] args){
		if(args.length<=0) {
			System.out.println("Not Enough Arguments for DCP");
			return;
		}
		DcpV2 dcp = new DcpV2(
			//int numNei, int neiSize, double attackerPercentage, double nonAttackerPercentage,  Integer ob, int mcmcRounds, String pathOfCpt 
			Integer.parseInt(args[0]), //numNei
			Integer.parseInt(args[1]), // neiSize
			Double.parseDouble(args[2]),//attackerPercentage
			Double.parseDouble(args[3]),//nonAttackerPercentage
			Integer.parseInt(args[4]),//ob
			Integer.parseInt(args[5]),//mcmc rounds
			args[6],//pathOfCpt
			Integer.parseInt(args[7]), //attackerType
			Integer.parseInt(args[8]) //nonAttackerType
			);
	}
}
