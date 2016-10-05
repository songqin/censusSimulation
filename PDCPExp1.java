
/*
Order of creating agents
1 Active Agents:
	HOA
	MA
2 Inactive Agents: 
*/
import java.util.*;
import java.io.*;
import java.math.*;
/*
100 Agents per Neighborhood
How many I witness = Power Law
Attacker 1: 
*/
public class PDCPExp1{
	static PowerLaw pl;
	int fast=1;
	int printLess=1;
	int numNei;//number of neighborhoods in DCP
	int neiSize;//number of residents per neighborhood
	int p;//population
	BigDecimal hoaPercentage;//honest agent percentage (eligible=1 and reliable=1)
	BigDecimal maPercentage;//malicious agent percentage 
	BigDecimal iaPercentage;//inactive agent
	int nAttacker,nHOA, nIA;
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
	String fileName;
	final public static int RW = 1;
	final public static int CS = 2;
	final public static int T = 1;
	final public static int F = 0;
	double prior[];
	double CPT_CS[][]; // CPT[cs_a,rw_b] Conditional Probability Table for eligibility
	double CPT_RW[][];
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
	// BigDecimal attackerWitnessUp;
	// BigDecimal attackerWitnessDown;
	// BigDecimal nonAttackerWitness;
	int attackerDownCount=0;
	int attackerUpCount=0;
	int nonAttackerDownCount=0;
	int nonAttackerUpCount=0;
	static Random rnd;
	// int c;//parameter passed from Batch class
	// static Random rnd = new Random(0);
	// load()
	// init() 
	// mcmc()
	ArrayList<Neighborhood> neighborhoods;
	public static float random(float max) {
		float result = rnd.nextFloat() * max;
		return result;
	}	
	//constructor
	//hoa: honest agents
	// ma: malicious agents
	// (int numNei, int neiSize, BigDecimal hoaPercentage, BigDecimal maPercentage, int mcmcRounds,  
	// 	int attackerType,  String fileName)
	PDCPExp1(int numNei, int neiSize, int nAttacker, int nHOA, 
		 int mcmcRounds,  int attackerType,  String fileName){
		rnd = new Random(0);
		// pl = new PowerLaw(new Random());
		this.numNei = numNei;
		// this.hoaPercentage = hoaPercentage;
		// this.maPercentage = maPercentage;
		// this.iaPercentage = BigDecimal.ONE.subtract(hoaPercentage).subtract(maPercentage);	
		this.neiSize = neiSize;
		// this.nAttacker=(maPercentage.multiply(new BigDecimal(neiSize))).intValue();
		this.nAttacker = nAttacker;
		this.nHOA = nHOA;
		this.nIA = neiSize-nAttacker-nHOA;
		// this.nHOA=(hoaPercentage.multiply(new BigDecimal(neiSize))).intValue();		
		// this.nIA = (iaPercentage.multiply(new BigDecimal(neiSize))).intValue();		
		System.out.println("nAttacker:" + nAttacker);
		System.out.println("nHOA:" + nHOA);		
		System.out.println("nIA: "+nIA);		
		this.ob=ob;
		this.pathOfCpt = "./cpt.txt";
		this.mcmcRounds = mcmcRounds;
		this.attackerType=attackerType;
		// this.nonAttackerType=nonAttackerType;
		// this.attackerWitnessUp=attackerWitnessUp;
		// this.attackerWitnessDown=attackerWitnessDown;
		// this.nonAttackerWitness=nonAttackerWitness;
		this.fileName = fileName;
		idToAgents = new HashMap<String, Agent>();
		readCPT();//load cpt
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
			Neighborhood n = new Neighborhood(i, neiSize, nAttacker, nHOA, nIA, this.prior[CS], this.prior[RW],
				attackerType
			 );
			neighborhoods.add(n);
		}
		// System.out.println("populate witness");
		// populateWitnessStances();
		// // printWitness();
		// printCPT();

		// System.out.println("mcmc");
		// mcmc(mcmcRounds);
		// printAllAgents();
		// printTprFpr(1);
		// System.out.println(	" attackerDownCount:"+attackerDownCount +
		// 	 " \nattakcerUpCount:"+attackerUpCount +
		// 	 " \nnonAttackerDownCount:"+nonAttackerDownCount+
		// 	 " \nnonAttackerUpCount:"+nonAttackerUpCount+
		// 	 " \nnonAttackerTotalWitCount:"+(nonAttackerUpCount+nonAttackerDownCount)
		// 	 );
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
		System.out.println("#CPT_RW[T][T] "+CPT_RW[T][T]);
		System.out.println("#CPT_RW[T][F] "+CPT_RW[T][F]);
		System.out.println("#CPT_RW[F][T] "+CPT_RW[F][T]);
		System.out.println("#CPT_RW[F][F] "+CPT_RW[F][F]);
		System.out.println("#prior RW:"+prior[RW]);		
		System.out.println("#prior CS:"+prior[CS]);	
		System.out.println("#honest agents:"+nha+" " + nha*1.0/p*100+"%");
		System.out.println("#fake agents:"+nfa+" "  +nfa*1.0/p*100+"%");
		System.out.println("#malicios agents:"+nma+" " +nma*1.0/p*100+"%");
		System.out.println("#inactive agents:"+(p-nha-nfa-nma)+" " +(p-nha-nfa-nma)*1.0/p*100+"%");
		System.out.println("#eligible  agents:"+nea);
		System.out.println("#ineligible  agents:"+nnea);
		System.out.println("#reliable  agents:"+nra);
		System.out.println("#unreliable  agents:"+nnra);
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
							a.nVotedUpByOthers+" "+
							a.nVotedDownByOthers+" "+
							a.nVoteUpOthers+" "+
							a.nVoteDownOthers
							);
			}
		}	
	}
	void printAgent(Agent a){
		System.out.println("agent_id, role,    eligible (CS), reliable(RW), csUpvoted, csDownvoted, csUpvote, csDownvote");
		System.out.println("#"+
			a.id+"     "+
			a.role+"       "+
			a.e_binary+"("+a.e_prob+")    "+
			a.r_binary+"("+a.r_prob+")    "+
			a.nVotedUpByOthers+"     "+
			a.nVotedDownByOthers+"      |"+
			a.nVoteUpOthers+"     "+
			a.nVoteDownOthers
			);		
	}
	void printAllAgents(){
		//role=1 honest agents (eligible=1, reliable=1), 
		//role=2 fake agents (reliable= 1 -
		//(want to use reliability to hide the fact of being fake), eligible=0)
		//role=3 malicious agents (reliable=0, eligible=1)
		//role=0 inactive agents (eligible=1 , reliable = 1)		
		// System.out.println("Computing agent's statistics");
		System.out.println("agent_id, role,    eligible (CS), reliable(RW), csUpvoted, csDownvoted, csUpvote, csDownvote");
			int hoa=0;
			int ia=0;
			int ma=0;
		for(Agent a: idToAgents.values()){

			if(a.role.equals("hoa"))
				{hoa++;}
			else if(a.role.equals("ia")) {ia++;}
			else ma++;
			System.out.println("#"+
				a.id+"     "+
				a.role+"       "+
				a.e_binary+"("+a.e_prob+")    "+
				a.r_binary+"("+a.r_prob+")    "+
				a.nVotedUpByOthers+"     "+
				a.nVotedDownByOthers+"      |"+
				a.nVoteUpOthers+"     "+
				a.nVoteDownOthers
				);
		}
		System.out.println(hoa+" "+ia+" "+ma);
		System.out.println("#--------------------------------");
	}

	void printTprFpr(int f){
		FileOutputStream fop = null;
		File file;
		try {

			file = new File("./plots/"+fileName);
			fop = new FileOutputStream(file);
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
					
		} catch (IOException e) {
			e.printStackTrace();
		}			
		for(double cutoff=0;cutoff<=1;cutoff+=0.01){

			double tCS=0;//expectation of CS being true
			double fCS=0;
			double tRW=0;
			double fRW=0;
			// int tp1=0;
			// int fp1=0;
			// int tp2=0;
			// int fp2=0;
			for(Agent a: idToAgents.values()){
				if((a.e_binary==1) && (a.e_prob>=cutoff)){
					tCS++;
				}
				if((a.e_binary==0) && (a.e_prob>=cutoff)){
					fCS++;
				}
				if((a.e_binary==1) && (a.e_prob>=cutoff)){
					tRW++;
				}
				if((a.e_binary==0) && (a.e_prob>=cutoff)){
					fRW++;
				}
			}
			//tpr, fpr, tpr , fpr
			String content = tCS*1.0/nea+" "+fCS*1.0/nnea+" "+tRW*1.0/nra+" "+fRW*1.0/nnra+" "+cutoff+"\n";
			try {
				byte[] contentInBytes = content.getBytes();
				fop.write(contentInBytes);


			} catch (IOException e) {
				e.printStackTrace();
			}			
			// for(Agent a: idToAgents.values()){
			// 	if(a.e_binary==1)
			// 		tCS+=a.e_prob;
			// 	else if (a.e_binary==-1){
			// 		tCS+=0;
			// 	}
			// 	else{
			// 		fCS+=a.e_prob;
			// 	}

			// 	if(a.r_binary==1)
			// 		tRW+=a.r_prob;
			// 	else if (a.r_binary==-1){
			// 		tRW+=0;
			// 	}
			// 	else
			// 		fRW+=a.r_prob;
			// }

		}
		// if(f==1)
		// {		
		// 	System.out.println("#expectation: CS(true), CS(false), RW(true), RW(false)");
		// 	System.out.println("#"+tCS+" "+fCS+" "+tRW+" "+fRW);
		// 	System.out.println("#-----------------------");
		// 	System.out.println("#"+"nea nnea nra nnra");
		// 	System.out.println("#"+nea+" "+ nnea+" " +nra+" "+ nnra );
		// 	System.out.println("#-----------------------");
		// 	System.out.println("#tpr(CS) fpr(CS) tpr(RW) fpr(RW)");
		// 	System.out.println(tCS*1.0/nea+" "+fCS*1.0/nnea+" "+tRW*1.0/nra+" "+fRW*1.0/nnra);
		// 	System.out.println("#-----------------------");
		// }		
		try {
			String zerozero="0.0 0.0 0.0 0.0"+"\n";
			byte[] cc = zerozero.getBytes();
			fop.write(cc);	
			fop.flush();
			fop.close();

		} catch (IOException e) {
			e.printStackTrace();
		}	


				// System.out.println("Done");
		// System.out.println(l + " " +k);
	}
	void printWitness(){
		System.out.println("#--------------------------------");
		System.out.println("nAttacker:"+nAttacker);
		System.out.println("nHOA:"+nHOA);
		System.out.println("nIA:"+nIA);
		System.out.println("#--------------------------------");
		System.out.println("#reviewerToProductAndVote");
		int attackerDown=0;
		int attakcerUp=0;
		int nonAttackerDown=0;
		int nonAttackerUp=0;
		for(String key:reviewerToProductAndVote.keySet()){
			System.out.println("#"+key+" "+reviewerToProductAndVote.get(key));
			// System.out.println(reviewerToProductAndVote.get(key).size());
			// c+=reviewerToProductAndVote.get(key).size();
		}
		// System.out.println("c:"+c);
		System.out.println("#--------------------------------");
		// System.out.println("#productToReviewerAndVote:");
		// for(String key:productToReviewerAndVote.keySet()){
		// 	System.out.println("#"+key+" "+productToReviewerAndVote.get(key));
		// }
		// System.out.println("#--------------------------------");
		// System.out.println("#NRWMapT:");
		// for(String key:NRWMapT.keySet()){
		// 	System.out.println("#"+key+" "+NRWMapT.get(key));
		// }
		// System.out.println("#--------------------------------");
		// System.out.println("#NCSMapT:");
		// for(String key:NCSMapT.keySet()){
		// 	System.out.println("#"+key+" "+NCSMapT.get(key));
		// }
		// System.out.println("#--------------------------------");
		// System.out.println("#stateMapCS:");
		// for(String key:stateMapCS.keySet()){
		// 	System.out.println("#"+key+" "+stateMapCS.get(key));
		// }
		// System.out.println("#--------------------------------");
		// System.out.println("#stateMapRW:");
		// for(String key:stateMapRW.keySet()){
		// 	System.out.println("#"+key+" "+stateMapRW.get(key));
		// }
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
		CPT_RW = new double[2][2];
		prior = new double[3];
//		CPT_RW = new double[2][2];
//		CPT_self_CS = new double[2][2];
//		CPT_self_RW = new double[2];
//		CPT_observer_self_RW = new double[2];
//		CPT_observer_self_CS = new double[2][2];
//		CPT_observer_RW = new double[2][2];
//		CPT_observer_CS = new double[2][2];
		String line = s.nextLine();// Read the name of CPT: Name of Table: CPT_CS
		s.nextLine();// Read the headers of CS Table :Headers of Table: RW_A CS_B  P(W_AB_Psi)
		CPT_CS[T][T] = Double.parseDouble(s.nextLine().split(" ")[2]);
		CPT_CS[T][F] = Double.parseDouble(s.nextLine().split(" ")[2]);
		CPT_CS[F][T] = Double.parseDouble(s.nextLine().split(" ")[2]);
		CPT_CS[F][F] = Double.parseDouble(s.nextLine().split(" ")[2]);

		s.nextLine();// Read the name of CPT: Priors
		s.nextLine();// Read the headers of CPT
		String p = s.nextLine();
		prior[RW] = Double.parseDouble(p.split(" ")[0]);
		prior[CS] = Double.parseDouble(p.split(" ")[1]);
		s.nextLine();// Read the name of RW CPT: Name of Table: CPT_RW
		s.nextLine();// Read the headers of CS Table : Headers of Table: RW_A RW_B  P(W_AB_Phi)
		CPT_RW[T][T] = Double.parseDouble(s.nextLine().split(" ")[2]);
		CPT_RW[T][F] = Double.parseDouble(s.nextLine().split(" ")[2]);
		CPT_RW[F][T] = Double.parseDouble(s.nextLine().split(" ")[2]);
		CPT_RW[F][F] = Double.parseDouble(s.nextLine().split(" ")[2]);		
	}

	void populateToyWitnessStances(){
		// #1_93 [1_76 1, 1_75 1, 1_71 1, 1_86 1, 1_84 1, 1_82 1, 1_99 1, 1_95 1]

		for(int i=2;i<=10;i++){
			String aid=1+"_"+1;
			String bid="1_"+i;
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
		}
	}

	void populateWitnessStances(){
		for(Neighborhood n: neighborhoods){
			HashMap<String, Agent> m = n.neighbors;
			List<Agent> list = new ArrayList<Agent>(m.values());
			// for(Agent a:list){
			// 	System.out.println(a.id);
			// }
			// System.out.println();
			// System.out.println();
			// System.out.println();
			// System.out.println();			
			Collections.shuffle(list);
			// for(Agent a:list){
			// 	for(Agent b:list){
			// 		System.out.println(a.id+" "+ b.id);
			// 	}
				
			// }
			// List keys = new ArrayList(m.keySet());
			// for(Object s: keys){
			// 	System.out.println(m.get(s).id);
			// }

			// Collections.shuffle(keys);
			// for(Object s: keys){
			// 	System.out.println(m.get(s).id);
			// }
			// Collection<Agent> agents1 = m.values();
			// Collection<Agent> agents2 = m.values();
			// Collection<Agent> agents = m.values();
			//todo
		// int count[]=new int[100];
  //       for (int i = 0; i < 100; i++) {//e.g., 100 agents
  //           // System.out.println("Zipf: " + p.zipf(100));
  //           int x=pl.zipf(100);//e.g., number of witnesses  = 0-99
  //           count[x]++;
  //       }
  //       double sum=0;
  //       for(int i=0;i<100;i++){
  //           System.out.println(i+" "+count[i]/100.0);
  //           sum+=count[i]/100.0;
  //       }	
  //       System.out.println("sum:"+sum);

        // for (Agent a : agents1) {
        // 	System.out.println(a.id);
        // }
        
        // for (Agent a : agents1) {
        // 	System.out.println(a.id);
        // }
        if(attackerType==1){
			// BigDecimal w = //attackerWitnessUp;
			for(Agent a:list){
				pl = new PowerLaw(new Random());
				for(Agent b:list){
					BigDecimal k = new BigDecimal(pl.zipf(100)/99.0);//99 witness stances
					System.out.println(a.id+" "+ b.id);
					if(a.id!=b.id){
						String aid=a.id;
						String bid=b.id;
						Integer vote=-1;
						// Integer csVote=-1;	
						// Integer rwVote=-1;
						// BigDecimal r=new BigDecimal(Math.random());
						if(a.role.equals("hoa")){
							BigDecimal r=new BigDecimal(Math.random());
							if(r.compareTo(k)==-1){//< less than
								//todo: witness cs rw, mcmc. ob for odcp
								if(b.role.equals("hoa")){
									vote=1;
								}
								else if(b.role.equals("ia")){
									vote=1;
									// a.nVoteUpOthers++;
									// b.nVotedUpByOthers++;	
									// this.nonAttackerUpCount++;									
								}
								else {
									vote=0;
									// a.nVoteDownOthers++;
									// b.nVotedDownByOthers++;	
									// this.nonAttackerDownCount++;									
								}									
							}
						}
						else if(a.role.equals("attacker_1")){//attacker_1
							if(b.role.equals("attacker_1")){
								BigDecimal r=new BigDecimal(Math.random());
								if(r.compareTo(k)==-1){
									vote=1;
									// a.nVoteUpOthers++;
									// b.nVotedUpByOthers++;		
									// this.attackerUpCount++;									
								}
							}
							// else{
							// 	vote=0;
							// 	a.nVoteDownOthers++;
							// 	b.nVotedDownByOthers++;										
							// }
						}
						if(vote!=-1){
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
						}							
					}
				}					
				}
				
			}			
			

			// if(attackerType==1 && nonAttackerType==1){
			// 	BigDecimal k = this.nonAttackerWitness;
			// 	BigDecimal w = this.attackerWitnessUp;
			// 	for (Agent a : agents1) {
			// 		// int c=0;
			// 		for (Agent b : agents2) {
			// 			if(a.id!=b.id){
			// 				String aid=a.id;
			// 				String bid=b.id;
			// 				Integer vote=-1;	
			// 				// BigDecimal r=new BigDecimal(Math.random());
			// 				if(a.role.equals("nonAttacker_1")){
			// 					BigDecimal r=new BigDecimal(Math.random());
			// 					if(r.compareTo(k)==-1){
			// 						if(b.role.equals("nonAttacker_1")){
			// 							vote=1;
			// 							a.nVoteUpOthers++;
			// 							b.nVotedUpByOthers++;	
			// 							this.nonAttackerUpCount++;									
			// 						}
			// 						else{
			// 							vote=0;
			// 							a.nVoteDownOthers++;
			// 							b.nVotedDownByOthers++;	
			// 							this.nonAttackerDownCount++;									
			// 						}									
			// 					}
			// 				}
			// 				else{//attacker_1
			// 					if(b.role.equals("attacker_1")){
			// 						BigDecimal r=new BigDecimal(Math.random());
			// 						if(r.compareTo(w)==-1){
			// 							vote=1;
			// 							a.nVoteUpOthers++;
			// 							b.nVotedUpByOthers++;		
			// 							this.attackerUpCount++;									
			// 						}
			// 					}
			// 					// else{
			// 					// 	vote=0;
			// 					// 	a.nVoteDownOthers++;
			// 					// 	b.nVotedDownByOthers++;										
			// 					// }
			// 				}
			// 				if(vote!=-1){
			// 					if(reviewerToProductAndVote.containsKey(aid)){
			// 						reviewerToProductAndVote.get(aid).add(bid+" "+vote);
			// 					}
			// 					else{
			// 						List<String> l = new ArrayList<String>();
			// 						l.add(bid+" "+vote);
			// 						reviewerToProductAndVote.put(aid, l);
			// 					}
			// 					if(productToReviewerAndVote.containsKey(bid)){
			// 						productToReviewerAndVote.get(bid).add(aid+" "+vote);
			// 					}
			// 					else{
			// 						List<String> l = new ArrayList<String>();
			// 						l.add(aid+" "+vote);
			// 						productToReviewerAndVote.put(bid, l);
			// 					}																
			// 				}							
			// 			}
			// 		}
			// 		// System.out.println("c:"+c);
			// 	}				
			// }
			// else if(attackerType==1 && nonAttackerType==2){
			// 	BigDecimal w = this.attackerWitnessUp;
			// 	for (Agent a : agents1) {
			// 		// int c=0;
			// 		for (Agent b : agents2) {
			// 			if(a.id!=b.id){
			// 				String aid=a.id;
			// 				String bid=b.id;
			// 				Integer vote=-1;	
			// 				// BigDecimal r=new BigDecimal(Math.random());
			// 				if(a.role.equals("attacker_1")){
			// 					if(b.role.equals("attacker_1")){
			// 						BigDecimal r=new BigDecimal(Math.random());
			// 						if(r.compareTo(w)==-1){
			// 							vote=1;
			// 							a.nVoteUpOthers++;
			// 							b.nVotedUpByOthers++;										
			// 						}
			// 					}
			// 					// else{
			// 					// 	vote=0;
			// 					// 	a.nVoteDownOthers++;
			// 					// 	b.nVotedDownByOthers++;										
			// 					// }
			// 				}
			// 				if(vote!=-1){
			// 					if(reviewerToProductAndVote.containsKey(aid)){
			// 						reviewerToProductAndVote.get(aid).add(bid+" "+vote);
			// 					}
			// 					else{
			// 						List<String> l = new ArrayList<String>();
			// 						l.add(bid+" "+vote);
			// 						reviewerToProductAndVote.put(aid, l);
			// 					}
			// 					if(productToReviewerAndVote.containsKey(bid)){
			// 						productToReviewerAndVote.get(bid).add(aid+" "+vote);
			// 					}
			// 					else{
			// 						List<String> l = new ArrayList<String>();
			// 						l.add(aid+" "+vote);
			// 						productToReviewerAndVote.put(bid, l);
			// 					}																	
			// 				}							
			// 			}
			// 		}
			// 		// System.out.println("c:"+c);
			// 	}					
			// }	
			// else if(attackerType==2 && nonAttackerType==1){
			// 	BigDecimal k = this.nonAttackerWitness;
			// 	BigDecimal w = this.attackerWitnessDown;
			// 	for (Agent a : agents1) {
			// 		// int c=0;
			// 		for (Agent b : agents2) {
			// 			if(a.id!=b.id){
			// 				String aid=a.id;
			// 				String bid=b.id;
			// 				Integer vote=-1;	
			// 				// BigDecimal r=new BigDecimal(Math.random());
			// 				if(a.role.equals("nonAttacker_1")){
			// 					BigDecimal r=new BigDecimal(Math.random());
			// 					if(r.compareTo(k)==-1){
			// 						if(b.role.equals("nonAttacker_1")){
			// 							vote=1;
			// 							a.nVoteUpOthers++;
			// 							b.nVotedUpByOthers++;		
			// 							this.nonAttackerUpCount++;							
			// 						}
			// 						else{
			// 							vote=0;
			// 							a.nVoteDownOthers++;
			// 							b.nVotedDownByOthers++;	
			// 							this.nonAttackerUpCount++;									
			// 						}									
			// 					}
			// 				}
			// 				else{//attacker_2
			// 					if(b.role.equals("nonAttacker_1")){
			// 						BigDecimal r=new BigDecimal(Math.random());
			// 						if(r.compareTo(w)==-1){
			// 							vote=0;
			// 							a.nVoteDownOthers++;
			// 							b.nVotedDownByOthers++;
			// 							this.attackerDownCount++;										
			// 						}
			// 					}
			// 					// else{
			// 					// 	vote=0;
			// 					// 	a.nVoteDownOthers++;
			// 					// 	b.nVotedDownByOthers++;										
			// 					// }
			// 				}
			// 				if(vote!=-1){
			// 					if(reviewerToProductAndVote.containsKey(aid)){
			// 						reviewerToProductAndVote.get(aid).add(bid+" "+vote);
			// 					}
			// 					else{
			// 						List<String> l = new ArrayList<String>();
			// 						l.add(bid+" "+vote);
			// 						reviewerToProductAndVote.put(aid, l);
			// 					}
			// 					if(productToReviewerAndVote.containsKey(bid)){
			// 						productToReviewerAndVote.get(bid).add(aid+" "+vote);
			// 					}
			// 					else{
			// 						List<String> l = new ArrayList<String>();
			// 						l.add(aid+" "+vote);
			// 						productToReviewerAndVote.put(bid, l);
			// 					}								
			// 				}							
			// 			}
			// 		}
			// 		// System.out.println("c:"+c);
			// 	}				
			// }	
			// else if(attackerType==2 && nonAttackerType==2){
			// 	BigDecimal w = this.attackerWitnessDown;
			// 	for (Agent a : agents1) {
			// 		for (Agent b : agents2) {
			// 			if(a.id!=b.id){
			// 				String aid=a.id;
			// 				String bid=b.id;
			// 				Integer vote=-1;	
			// 				// BigDecimal r=new BigDecimal(Math.random());
			// 				if(a.role.equals("attacker_2")){
			// 					if(b.role.equals("nonAttacker_2")){
			// 						BigDecimal r=new BigDecimal(Math.random());
			// 						// System.out.println(r+" "+w);
			// 						if(r.compareTo(w)==-1){
			// 							vote=0;
			// 							a.nVoteDownOthers++;
			// 							b.nVotedDownByOthers++;
			// 							this.attackerDownCount++;	
			// 						}
			// 					}
			// 					// else{
			// 					// 	vote=0;
			// 					// 	a.nVoteDownOthers++;
			// 					// 	b.nVotedDownByOthers++;										
			// 					// }
			// 				}
			// 				if(vote!=-1){
			// 					if(reviewerToProductAndVote.containsKey(aid)){
			// 						reviewerToProductAndVote.get(aid).add(bid+" "+vote);
			// 					}
			// 					else{
			// 						List<String> l = new ArrayList<String>();
			// 						l.add(bid+" "+vote);
			// 						reviewerToProductAndVote.put(aid, l);
			// 					}
			// 					if(productToReviewerAndVote.containsKey(bid)){
			// 						productToReviewerAndVote.get(bid).add(aid+" "+vote);
			// 					}
			// 					else{
			// 						List<String> l = new ArrayList<String>();
			// 						l.add(aid+" "+vote);
			// 						productToReviewerAndVote.put(bid, l);
			// 					}																	
			// 				}							
			// 			}
			// 		}
			// 	}					
			// }
			// else if(attackerType==3 && nonAttackerType==1){
				
			// }
			// else if(attackerType==3 && nonAttackerType==2){
				
			// }
			// //a4n1
			// else if(attackerType==4 && nonAttackerType==1){
			// 	BigDecimal k = this.nonAttackerWitness;
			// 	BigDecimal w = this.attackerWitnessDown;
			// 	for (Agent a : agents1) {
			// 		// int c=0;
			// 		for (Agent b : agents2) {
			// 			if(a.id!=b.id){
			// 				String aid=a.id;
			// 				String bid=b.id;
			// 				Integer vote=-1;	
			// 				// BigDecimal r=new BigDecimal(Math.random());
			// 				if(a.role.equals("nonAttacker_1")){
			// 					BigDecimal r=new BigDecimal(Math.random());
			// 					if(r.compareTo(k)==-1){
			// 						if(b.role.equals("nonAttacker_1")){
			// 							vote=1;
			// 							a.nVoteUpOthers++;
			// 							b.nVotedUpByOthers++;										
			// 						}
			// 						else{
			// 							vote=0;
			// 							a.nVoteDownOthers++;
			// 							b.nVotedDownByOthers++;										
			// 						}									
			// 					}
			// 				}
			// 				else{//attacker_2
			// 					if(b.role.equals("nonAttacker_1")){
			// 						BigDecimal r=new BigDecimal(Math.random());
			// 						if(r.compareTo(w)==-1){
			// 							vote=0;
			// 							a.nVoteDownOthers++;
			// 							b.nVotedDownByOthers++;										
			// 						}
			// 					}
			// 					// else{
			// 					// 	vote=0;
			// 					// 	a.nVoteDownOthers++;
			// 					// 	b.nVotedDownByOthers++;										
			// 					// }
			// 				}
			// 				if(vote!=-1){
			// 					if(reviewerToProductAndVote.containsKey(aid)){
			// 						reviewerToProductAndVote.get(aid).add(bid+" "+vote);
			// 					}
			// 					else{
			// 						List<String> l = new ArrayList<String>();
			// 						l.add(bid+" "+vote);
			// 						reviewerToProductAndVote.put(aid, l);
			// 					}
			// 					if(productToReviewerAndVote.containsKey(bid)){
			// 						productToReviewerAndVote.get(bid).add(aid+" "+vote);
			// 					}
			// 					else{
			// 						List<String> l = new ArrayList<String>();
			// 						l.add(aid+" "+vote);
			// 						productToReviewerAndVote.put(bid, l);
			// 					}								
			// 				}							
			// 			}
			// 		}
			// 		// System.out.println("c:"+c);
			// 	}					
			// }
			// else if(attackerType==4 && nonAttackerType==2){
				
			// }
			// else if(attackerType==5 && nonAttackerType==1){
			// 	BigDecimal k = this.nonAttackerWitness;
			// 	BigDecimal w = this.attackerWitnessUp;
			// 	for (Agent a : agents1) {
			// 		// int c=0;
			// 		for (Agent b : agents2) {
			// 			if(a.id!=b.id){
			// 				String aid=a.id;
			// 				String bid=b.id;
			// 				Integer vote=-1;	
			// 				// BigDecimal r=new BigDecimal(Math.random());
			// 				if(a.role.equals("nonAttacker_1")){
			// 					BigDecimal r=new BigDecimal(Math.random());
			// 					if(r.compareTo(k)==-1){
			// 						if(b.role.equals("nonAttacker_1")){
			// 							vote=1;
			// 							a.nVoteUpOthers++;
			// 							b.nVotedUpByOthers++;	
			// 							this.nonAttackerUpCount++;									
			// 						}
			// 						else{
			// 							vote=0;
			// 							a.nVoteDownOthers++;
			// 							b.nVotedDownByOthers++;	
			// 							this.nonAttackerDownCount++;									
			// 						}									
			// 					}
			// 				}
			// 				else{//attacker_1
			// 					if(b.role.equals("attacker_5")){
			// 						BigDecimal r=new BigDecimal(Math.random());
			// 						if(r.compareTo(w)==-1){
			// 							vote=1;
			// 							a.nVoteUpOthers++;
			// 							b.nVotedUpByOthers++;		
			// 							this.attackerUpCount++;									
			// 						}
			// 					}
			// 					// else{
			// 					// 	vote=0;
			// 					// 	a.nVoteDownOthers++;
			// 					// 	b.nVotedDownByOthers++;										
			// 					// }
			// 				}
			// 				if(vote!=-1){
			// 					if(reviewerToProductAndVote.containsKey(aid)){
			// 						reviewerToProductAndVote.get(aid).add(bid+" "+vote);
			// 					}
			// 					else{
			// 						List<String> l = new ArrayList<String>();
			// 						l.add(bid+" "+vote);
			// 						reviewerToProductAndVote.put(aid, l);
			// 					}
			// 					if(productToReviewerAndVote.containsKey(bid)){
			// 						productToReviewerAndVote.get(bid).add(aid+" "+vote);
			// 					}
			// 					else{
			// 						List<String> l = new ArrayList<String>();
			// 						l.add(aid+" "+vote);
			// 						productToReviewerAndVote.put(bid, l);
			// 					}																
			// 				}							
			// 			}
			// 		}
			// 		// System.out.println("c:"+c);
			// 	}					
			// }
			// else if(attackerType==5 && nonAttackerType==2){
			// 	BigDecimal w = this.attackerWitnessUp;
			// 	for (Agent a : agents1) {
			// 		// int c=0;
			// 		for (Agent b : agents2) {
			// 			if(a.id!=b.id){
			// 				String aid=a.id;
			// 				String bid=b.id;
			// 				Integer vote=-1;	
			// 				// BigDecimal r=new BigDecimal(Math.random());
			// 				if(a.role.equals("attacker_5")){
			// 					if(b.role.equals("attacker_5")){
			// 						BigDecimal r=new BigDecimal(Math.random());
			// 						if(r.compareTo(w)==-1){
			// 							vote=1;
			// 							a.nVoteUpOthers++;
			// 							b.nVotedUpByOthers++;										
			// 						}
			// 					}
			// 					// else{
			// 					// 	vote=0;
			// 					// 	a.nVoteDownOthers++;
			// 					// 	b.nVotedDownByOthers++;										
			// 					// }
			// 				}
			// 				if(vote!=-1){
			// 					if(reviewerToProductAndVote.containsKey(aid)){
			// 						reviewerToProductAndVote.get(aid).add(bid+" "+vote);
			// 					}
			// 					else{
			// 						List<String> l = new ArrayList<String>();
			// 						l.add(bid+" "+vote);
			// 						reviewerToProductAndVote.put(aid, l);
			// 					}
			// 					if(productToReviewerAndVote.containsKey(bid)){
			// 						productToReviewerAndVote.get(bid).add(aid+" "+vote);
			// 					}
			// 					else{
			// 						List<String> l = new ArrayList<String>();
			// 						l.add(aid+" "+vote);
			// 						productToReviewerAndVote.put(bid, l);
			// 					}																	
			// 				}							
			// 			}
			// 		}
			// 		// System.out.println("c:"+c);
			// 	}					
			// }
			// else if(attackerType==6 && nonAttackerType==1){
			// 	BigDecimal k = this.nonAttackerWitness;
			// 	BigDecimal w = this.attackerWitnessDown;
			// 	for (Agent a : agents1) {
			// 		// int c=0;
			// 		for (Agent b : agents2) {
			// 			if(a.id!=b.id){
			// 				String aid=a.id;
			// 				String bid=b.id;
			// 				Integer vote=-1;	
			// 				// BigDecimal r=new BigDecimal(Math.random());
			// 				if(a.role.equals("nonAttacker_1")){
			// 					BigDecimal r=new BigDecimal(Math.random());
			// 					if(r.compareTo(k)==-1){
			// 						if(b.role.equals("nonAttacker_1")){
			// 							vote=1;
			// 							a.nVoteUpOthers++;
			// 							b.nVotedUpByOthers++;										
			// 						}
			// 						else{
			// 							vote=0;
			// 							a.nVoteDownOthers++;
			// 							b.nVotedDownByOthers++;										
			// 						}									
			// 					}
			// 				}
			// 				else{//attacker_6
			// 					if(b.role.equals("attacker_6")){
			// 						BigDecimal r=new BigDecimal(Math.random());
			// 						if(r.compareTo(w)==-1){
			// 							vote=0;
			// 							a.nVoteDownOthers++;
			// 							b.nVotedDownByOthers++;										
			// 						}
			// 					}
			// 					// else{
			// 					// 	vote=0;
			// 					// 	a.nVoteDownOthers++;
			// 					// 	b.nVotedDownByOthers++;										
			// 					// }
			// 				}
			// 				if(vote!=-1){
			// 					if(reviewerToProductAndVote.containsKey(aid)){
			// 						reviewerToProductAndVote.get(aid).add(bid+" "+vote);
			// 					}
			// 					else{
			// 						List<String> l = new ArrayList<String>();
			// 						l.add(bid+" "+vote);
			// 						reviewerToProductAndVote.put(aid, l);
			// 					}
			// 					if(productToReviewerAndVote.containsKey(bid)){
			// 						productToReviewerAndVote.get(bid).add(aid+" "+vote);
			// 					}
			// 					else{
			// 						List<String> l = new ArrayList<String>();
			// 						l.add(aid+" "+vote);
			// 						productToReviewerAndVote.put(bid, l);
			// 					}									
			// 				}							
			// 			}
			// 		}
			// 		// System.out.println("c:"+c);
			// 	}				
			// }
			// else if(attackerType==6 && nonAttackerType==2){
			// 	BigDecimal k = this.nonAttackerWitness;
			// 	BigDecimal w = this.attackerWitnessDown;
			// 	for (Agent a : agents1) {
			// 		// int c=0;
			// 		for (Agent b : agents2) {
			// 			if(a.id!=b.id){
			// 				String aid=a.id;
			// 				String bid=b.id;
			// 				Integer vote=-1;	
			// 				if(a.role.equals("attacker_6")){//attacker_7
			// 					if(b.role.equals("attacker_6")){
			// 						BigDecimal r=new BigDecimal(Math.random());
			// 						if(r.compareTo(w)==-1){
			// 							vote=0;
			// 							a.nVoteDownOthers++;
			// 							b.nVotedDownByOthers++;										
			// 						}
			// 					}
			// 					// else{
			// 					// 	vote=0;
			// 					// 	a.nVoteDownOthers++;
			// 					// 	b.nVotedDownByOthers++;										
			// 					// }
			// 				}
			// 				if(vote!=-1){
			// 					if(reviewerToProductAndVote.containsKey(aid)){
			// 						reviewerToProductAndVote.get(aid).add(bid+" "+vote);
			// 					}
			// 					else{
			// 						List<String> l = new ArrayList<String>();
			// 						l.add(bid+" "+vote);
			// 						reviewerToProductAndVote.put(aid, l);
			// 					}
			// 					if(productToReviewerAndVote.containsKey(bid)){
			// 						productToReviewerAndVote.get(bid).add(aid+" "+vote);
			// 					}
			// 					else{
			// 						List<String> l = new ArrayList<String>();
			// 						l.add(aid+" "+vote);
			// 						productToReviewerAndVote.put(bid, l);
			// 					}									
			// 				}							
			// 			}
			// 		}
			// 		// System.out.println("c:"+c);
			// 	}				
			// }			
			// else if(attackerType==7 && nonAttackerType==2){
			// 	BigDecimal k = this.nonAttackerWitness;
			// 	BigDecimal w = this.attackerWitnessUp;
			// 	for (Agent a : agents1) {
			// 		// int c=0;
			// 		for (Agent b : agents2) {
			// 			if(a.id!=b.id){
			// 				String aid=a.id;
			// 				String bid=b.id;
			// 				Integer vote=-1;	
			// 				if(a.role.equals("attacker_7")){//attacker_7
			// 					if(b.role.equals("nonAttacker_2")){
			// 						BigDecimal r=new BigDecimal(Math.random());
			// 						if(r.compareTo(w)==-1){
			// 							vote=1;
			// 							a.nVoteUpOthers++;
			// 							b.nVotedUpByOthers++;										
			// 						}
			// 					}
			// 					// else{
			// 					// 	vote=0;
			// 					// 	a.nVoteDownOthers++;
			// 					// 	b.nVotedDownByOthers++;										
			// 					// }
			// 				}
			// 				if(vote!=-1){
			// 					if(reviewerToProductAndVote.containsKey(aid)){
			// 						reviewerToProductAndVote.get(aid).add(bid+" "+vote);
			// 					}
			// 					else{
			// 						List<String> l = new ArrayList<String>();
			// 						l.add(bid+" "+vote);
			// 						reviewerToProductAndVote.put(aid, l);
			// 					}
			// 					if(productToReviewerAndVote.containsKey(bid)){
			// 						productToReviewerAndVote.get(bid).add(aid+" "+vote);
			// 					}
			// 					else{
			// 						List<String> l = new ArrayList<String>();
			// 						l.add(aid+" "+vote);
			// 						productToReviewerAndVote.put(bid, l);
			// 					}								
			// 				}							
			// 			}
			// 		}
			// 		// System.out.println("c:"+c);
			// 	}				
			// }
			// else if(attackerType==7 && nonAttackerType==1){
			// 	BigDecimal k = this.nonAttackerWitness;
			// 	BigDecimal w = this.attackerWitnessUp;
			// 	for (Agent a : agents1) {
			// 		// int c=0;
			// 		for (Agent b : agents2) {
			// 			if(a.id!=b.id){
			// 				String aid=a.id;
			// 				String bid=b.id;
			// 				Integer vote=-1;	
			// 				// BigDecimal r=new BigDecimal(Math.random());
			// 				if(a.role.equals("nonAttacker_1")){
			// 					BigDecimal r=new BigDecimal(Math.random());
			// 					if(r.compareTo(k)==-1){
			// 						if(b.role.equals("nonAttacker_1")){
			// 							vote=1;
			// 							a.nVoteUpOthers++;
			// 							b.nVotedUpByOthers++;										
			// 						}
			// 						else{
			// 							vote=0;
			// 							a.nVoteDownOthers++;
			// 							b.nVotedDownByOthers++;										
			// 						}									
			// 					}
			// 				}
			// 				else{//attacker_7
			// 					if(b.role.equals("nonAttacker_1")){
			// 						BigDecimal r=new BigDecimal(Math.random());
			// 						if(r.compareTo(w)==-1){
			// 							vote=1;
			// 							a.nVoteUpOthers++;
			// 							b.nVotedUpByOthers++;										
			// 						}
			// 					}
			// 					// else{
			// 					// 	vote=0;
			// 					// 	a.nVoteDownOthers++;
			// 					// 	b.nVotedDownByOthers++;										
			// 					// }
			// 				}
			// 				if(vote!=-1){
			// 					if(reviewerToProductAndVote.containsKey(aid)){
			// 						reviewerToProductAndVote.get(aid).add(bid+" "+vote);
			// 					}
			// 					else{
			// 						List<String> l = new ArrayList<String>();
			// 						l.add(bid+" "+vote);
			// 						reviewerToProductAndVote.put(aid, l);
			// 					}
			// 					if(productToReviewerAndVote.containsKey(bid)){
			// 						productToReviewerAndVote.get(bid).add(aid+" "+vote);
			// 					}
			// 					else{
			// 						List<String> l = new ArrayList<String>();
			// 						l.add(aid+" "+vote);
			// 						productToReviewerAndVote.put(bid, l);
			// 					}								
			// 				}							
			// 			}
			// 		}
			// 		// System.out.println("c:"+c);
			// 	}					
			// }
			// // else if(attackerType==7 && nonAttackerType==2){
				
			// // }
			// else if(attackerType==8 && nonAttackerType==1){
			// 	//inactive attacker
			// 	//nonattacker witness k% of agents 
			// 	BigDecimal k = this.nonAttackerWitness;
			// 	for (Agent a : agents1) {
			// 		// int c=0;
			// 		for (Agent b : agents2) {
			// 			if(a.id!=b.id){

			// 					// c++;
			// 				if(a.role.equals("nonAttacker_1")){
			// 					String aid=a.id;
			// 					String bid=b.id;
			// 					Integer vote=-1;
			// 					if(b.role.equals("nonAttacker_1")){
			// 						BigDecimal r=new BigDecimal(Math.random());									
			// 						if(r.compareTo(k)==-1){
			// 							vote=1;
			// 							a.nVoteUpOthers++;
			// 							b.nVotedUpByOthers++;	
			// 						}									
			// 					}
			// 					else{
			// 						BigDecimal r=new BigDecimal(Math.random());										
			// 						if(r.compareTo(k)==-1){									
			// 							vote=0;
			// 							a.nVoteDownOthers++;
			// 							b.nVotedDownByOthers++;										
			// 						}
			// 					}
			// 					}
			// 				}
			// 			}
			// 		}
			// 		// System.out.println("c:"+c);
			// 	}

			
			// else if(attackerType==8 && nonAttackerType==2){
			// 	//do nothing for both inactive agents
			// }
			// else{
			// 	System.out.println("#Attacker/nonAttacker type error");
			// }					
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
	// ... infer from rw witnesses
	public void mcmc(int x) {//x: rounds of MCMC sampling
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
	//inference of RW(a)
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
		
		if(reviewerToProductAndVote.get(a)!=null){//we combined CS and RW for witnessing stances. 
			if(reviewerToProductAndVote.get(a)!=null){
				List<String> list = reviewerToProductAndVote.get(a);
				for(int i=0; i<list.size(); i++) {
					double[][] CPT_crt_CS = CPT_CS;
					double[][] CPT_crt_RW = CPT_RW;
					String o = list.get(i);
					String product = o.split(" ")[0];//witnessed
					String vote=o.split(" ")[1];
					if(vote.equals("1")){
						int state_product_CS = stateMapCS.get(product);
						int state_product_RW = stateMapRW.get(product);
						//a summation for Wab_CS
						alpha_T *= CPT_crt_CS[T][state_product_CS];
						alpha_F *= CPT_crt_CS[F][state_product_CS];
						//a summation for Wab_RW
						alpha_T *= CPT_crt_RW[T][state_product_RW];
						alpha_F *= CPT_crt_RW[F][state_product_RW];					
					}
					else{
						int state_product_CS = stateMapCS.get(product);//efficiency
						int state_product_RW = stateMapRW.get(product);
						alpha_T *= 1 - CPT_crt_CS[T][state_product_CS];
						alpha_F *= 1 - CPT_crt_CS[F][state_product_CS];
						alpha_T *= 1 - CPT_crt_RW[T][state_product_RW];
						alpha_F *= 1 - CPT_crt_RW[F][state_product_RW];							
					}
				}				
			}

			if(productToReviewerAndVote.get(a)!=null){
				List<String> list = productToReviewerAndVote.get(a);
				for(int i=0; i<list.size(); i++) {
				   double[][] CPT_crt_RW = CPT_RW;
				   String o = list.get(i);
				   // System.out.println("o:"+o);
				   String reviewer = o.split(" ")[0];
				   String vote=o.split(" ")[1];
				   // System.out.println(vote);
				   // System.out.println(vote.equals("1"));
					if(vote.equals("1")){
						int state_reviewer_RW = stateMapRW.get(reviewer);
						// a summation for Wba_RW
						alpha_T *= CPT_crt_RW[state_reviewer_RW][T];
						alpha_F *= CPT_crt_RW[state_reviewer_RW][F];
					}
					else{
						int state_reviewer_RW = stateMapRW.get(reviewer);
						alpha_T *= 1 - CPT_crt_RW[state_reviewer_RW][T];
						alpha_F *= 1 - CPT_crt_RW[state_reviewer_RW][F];
					}		   
				}
			}
		}
		//todo: self witnessing: MCMC(5).java
		stateMapRW.put(a, sample(alpha_T / (alpha_T + alpha_F)));
		if(stateMapRW.get(a)==1){
			int c=NRWMapT.get(a);
			c++;
			NRWMapT.put(a, c);//for counting (vector)
		}
		else{
			int c=NRWMapF.get(a);
			c++;
			NRWMapF.put(a, c);
		}
	}
	//inference of CS(a)
	public void mcmc_A_CS(String a) {
		double alpha_T = 1 * prior[CS];
		double alpha_F = 1 * (1 - prior[CS]);
		
		if(productToReviewerAndVote.get(a)!=null){
			List<String> list = productToReviewerAndVote.get(a);
			for(int i=0; i<list.size(); i++) {
			   double[][] CPT_crt_CS = CPT_CS;
			   String o = list.get(i);
			   // System.out.println("o:"+o);
			   String reviewer = o.split(" ")[0];//b
			   String vote=o.split(" ")[1];
			   // System.out.println(vote);
			   // System.out.println(vote.equals("1"));
				if(vote.equals("1")){
					int state_reviewer_RW = stateMapRW.get(reviewer);
					// a summation for Wba_CS
					alpha_T *= CPT_crt_CS[state_reviewer_RW][T];
					alpha_F *= CPT_crt_CS[state_reviewer_RW][F];
				}
				else{
					int state_reviewer_RW = stateMapRW.get(reviewer);
					alpha_T *= 1 - CPT_crt_CS[state_reviewer_RW][T];
					alpha_F *= 1 - CPT_crt_CS[state_reviewer_RW][F];
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
		//agents in the current neighborhood
		//key=agent id, value=agent object
		HashMap<String, Agent> neighbors;
		int neiId;//id of this neighborhood, e.g., 1, 2, 3, ..., 100
		List<Agent> hacs = new ArrayList<Agent>();
		List<Agent> attackers = new ArrayList<Agent>();
		List<Agent> ias = new ArrayList<Agent>();
		List<Agent> uncensables = new ArrayList<Agent>();
		List<Agent> censables = new ArrayList<Agent>();
		Neighborhood(int i, int neiSize, int nAttacker, int  nHOA, int nIA, double priorCS, double priorRW,
			int attackerType
		){
			this.neiId=i;
			neighbors = new HashMap<String, Agent>();
			double percentageCensable=0.9;
			double percentageUncensable=0.1;
			double percentageActive;
			double percentageInactive;
			//Create agents in this neighorhood
			int j=1;
			int isCensable;
			//Create malicious agents
			for(;j<=nAttacker;j++){
				//i=neighborhood id, j=local agent id
				String id = i+"_"+j;
				isCensable = sample(percentageCensable);
				Agent attacker = new Agent(id);
				if (attackerType==2){//FUA
					attacker.role="FUA";
					attacker.e_binary=isCensable;
					attacker.r_binary=0;
					attacker.e_prob=priorCS;
					attacker.r_prob=priorRW;
					if(isCensable==1){
						nea++;				
						censables.add(attacker);
						}
					else{
						nnea++;							
						uncensables.add(attacker);
					}
					nnra++;
				}
				else if (attackerType==1){//FFA
					attacker.role="FFA";
					attacker.e_binary=isCensable;//censable
					attacker.r_binary=0;
					attacker.e_prob=priorCS;
					attacker.r_prob=priorRW;					
					if(isCensable==1){
						nea++;				
						censables.add(attacker);
						}
					else{
						nnea++;							
						uncensables.add(attacker);
					}
					nnra++;	
				}
				else {System.out.println("#Error: unknown attackerType when generating Neighborhood");}
				attackers.add(attacker);
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
			//Create HACs
			for(;j<=nAttacker+nHOA;j++){
				String id = i+"_"+j;
				isCensable = sample(percentageCensable);
				Agent hac = new Agent(id);
				// if(nonAttackerType==1){
				hac.role="HOA";
				hac.e_binary=isCensable;
				hac.r_binary=1;
				if(isCensable==1){
					nea++;				
					censables.add(hac);
					}
				else{
					nnea++;							
					uncensables.add(hac);
				}				
				hac.e_prob=priorCS;
				hac.r_prob=priorRW;				
				nra++;
				// }
				// else if(nonAttackerType==2){					
				// 	nonAttacker.role="nonAttacker_2";
				// 	nonAttacker.e_binary=1;
				// 	nonAttacker.r_binary=-1;
				// 	nea++;			
				// }
				// else {System.out.println("#Error: unknown nonAttackerType when generating Neighborhood");}
				hacs.add(hac);
				neighbors.put(id, hac);
				idToAgents.put(id, hac);
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

			//Generation of IAs
			for(;j<=neiSize;j++){
				String id = i+"_"+j;
				isCensable = sample(percentageCensable);
				Agent iA = new Agent(id);
				// if(nonAttackerType==1){
				iA.role="IA";
				iA.e_binary=1;
				iA.r_binary=-1;//unknown
				iA.e_prob=priorCS;
				iA.r_prob=priorRW;
				if(isCensable==1){
					nea++;				
					censables.add(iA);
					}
				else{
					nnea++;							
					uncensables.add(iA);
				}				
				// nra++;					
				// }
				// else if(nonAttackerType==2){					
				// 	nonAttacker.role="nonAttacker_2";
				// 	nonAttacker.e_binary=1;
				// 	nonAttacker.r_binary=-1;
				// 	nea++;			
				// }
				// else {System.out.println("#Error: unknown nonAttackerType when generating Neighborhood");}
				ias.add(iA);
				neighbors.put(id, iA);
				idToAgents.put(id, iA);
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
			System.out.println("Neighborhood #:" + neiId);
			System.out.println("Number of agents:" + (hacs.size()+attackers.size()+ias.size()));
			System.out.println("Number of hacs: "+hacs.size());
			System.out.println("Number of attackers: "+attackers.size());
			System.out.println("size of ias: "+ias.size());
			System.out.println("size of uncensables: "+uncensables.size());
			System.out.println("size of censables: "+censables.size());
			System.out.println();
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
		int nVoteDownOthers;//numver of votes of the current agent casted to others
		int nVoteUpOthers;
		int nVotedDownByOthers;//number of votes from others
		int nVotedUpByOthers;
		// , double priorCS, double priorRW
		Agent(String id){//inactive agent by default
			this.id = id;
			this.role = "N/A";
			this.e_binary=-1;//0-ineligible, 1-eligible, -1-N/A
			this.r_binary=-1;//0-reliable, 1-not reliable, -1-N/A
			// this.e_prob=priorCS;
			// this.r_prob=priorRW;
			this.nVoteDownOthers=0;
			this.nVoteUpOthers=0;
			this.nVotedDownByOthers=0;
			this.nVotedUpByOthers=0;
		}
	}

	public static void main(String[] args){
		if(args.length<=0) {
			System.out.println("#Not Enough Arguments for DCP");
			return;
		}
		long startTime = System.currentTimeMillis();

		PDCPExp1 dcp = new PDCPExp1(
			//int numNei, int neiSize, double nonAttackerPercentage,  Integer ob, int mcmcRounds, String pathOfCpt 
			Integer.parseInt(args[0]), //numNei
			Integer.parseInt(args[1]), // size of Neighborhood
			// new BigDecimal(args[2]),//hoaPercentage
			Integer.parseInt(args[2]),//n attacker
			Integer.parseInt(args[3]),//n HOA
			Integer.parseInt(args[4]),//mcmc rounds
			Integer.parseInt(args[5]),//attacker TYpe
			args[6]//filename to write out tpr, fpr
			);
        // PowerLaw p = new PowerLaw(new Random(555));
        // int x=p.zipf(100);//e.g., number of witnesses  = 0-99

        // int count[]=new int[100];
        // for (int i = 0; i < 100; i++) {//e.g., 100 agents
        //     // System.out.println("Zipf: " + p.zipf(100));
        //     int x=p.zipf(100);//e.g., number of witnesses  = 0-99
        //     count[x]++;
        // }
        // int sum=0;
        // for(int i=0;i<100;i++){
        //     System.out.println(i+" "+count[i]/100.0);
        //     sum+=count[i];
        // }		
		// long endTime   = System.currentTimeMillis();
		// long totalTime = endTime - startTime;
		// System.out.println("#time in minutes: "+ totalTime/1000.0/60);			
	}
}
