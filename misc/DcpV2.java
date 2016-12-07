//decentralized census problem
import java.util.*;
import java.io.*;
import java.math.*;
public class DcpV2{
	int fast=1;
	int printLess=1;
	int numNei;//number of neighborhoods in DCP
	int neiSize;//number of residents per neighborhood
	int p;//population
	BigDecimal nonAttackerPercentage;//honest agent percentage (eligible=1 and reliable=1)
	BigDecimal attackerPercentage;//malicious agent percentage (not reliable(0), but eligible(1))
	int nAttacker;
	int nNonAttacker;
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
	BigDecimal attackerWitnessUp;
	BigDecimal attackerWitnessDown;
	BigDecimal nonAttackerWitness;
	int attackerDownCount=0;
	int attackerUpCount=0;
	int nonAttackerDownCount=0;
	int nonAttackerUpCount=0;
	static Random rnd;
	// int c;//parameter passed from Batch class
	// static Random rnd = new Random(0);
	ArrayList<Neighborhood> neighborhoods;
	public static float random(float max) {
		float result = rnd.nextFloat() * max;
		return result;
	}	
	//constructor
	//n:a reference number 
	DcpV2(int numNei, int neiSize, BigDecimal nonAttackerPercentage, BigDecimal nonAttackerWitness,  Integer ob, int mcmcRounds, String pathOfCpt, 
		int attackerType, int nonAttackerType, BigDecimal attackerWitnessUp, BigDecimal attackerWitnessDown, String fileName){
		rnd = new Random(0);
		this.numNei = numNei;
		this.nonAttackerPercentage = nonAttackerPercentage;
		this.attackerPercentage = BigDecimal.ONE.subtract(nonAttackerPercentage);	
		this.neiSize = neiSize;
		this.nAttacker=(attackerPercentage.multiply(new BigDecimal(neiSize))).intValue();
		this.nNonAttacker=(nonAttackerPercentage.multiply(new BigDecimal(neiSize))).intValue();		
		// System.out.println(nAttacker);
		// System.out.println(nNonAttacker);
		this.p = numNei * neiSize;

		this.ob=ob;
		this.pathOfCpt = pathOfCpt;
		this.mcmcRounds = mcmcRounds;
		this.attackerType=attackerType;
		this.nonAttackerType=nonAttackerType;
		this.attackerWitnessUp=attackerWitnessUp;
		this.attackerWitnessDown=attackerWitnessDown;
		this.nonAttackerWitness=nonAttackerWitness;
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
			Neighborhood n = new Neighborhood(i, neiSize, nAttacker, nNonAttacker, this.prior[CS], this.prior[RW],
				attackerType, nonAttackerType
			 );
			neighborhoods.add(n);
		}
		// populateToyWitnessStances();
		System.out.println("populate witness");
		populateWitnessStances();
		// printWitness();
		System.out.println("mcmc");
		mcmc(mcmcRounds);
		// printNeighborhoodAndAgents();
		// printAllAgents();
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
		System.out.println("agent_id, role,    eligible, reliable, Eligible, Reliable, csUpvoted, csDownvoted, csUpvote, csDownvote");
		for(Agent a: idToAgents.values()){
			System.out.println("#"+
				a.id+"     "+
				a.role+"       "+
				a.e_binary+"   "+
				a.r_binary+"       |"+
				a.e_prob+"    "+
				a.r_prob+"     |"+
				a.csUpvotedCount+"     "+
				a.csDownvotedCount+"      |"+
				a.csUpvoteCount+"     "+
				a.csDownvoteCount
				);
		}
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
		System.out.println("nNonAttacker:"+nNonAttacker);
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
			Collection<Agent> agents1 = m.values();
			Collection<Agent> agents2 = m.values();
			// Collection<Agent> agents = m.values();
			//todo
			if(attackerType==1 && nonAttackerType==1){
				BigDecimal k = this.nonAttackerWitness;
				BigDecimal w = this.attackerWitnessUp;
				for (Agent a : agents1) {
					// int c=0;
					for (Agent b : agents2) {
						if(a.id!=b.id){
							String aid=a.id;
							String bid=b.id;
							Integer vote=-1;	
							// BigDecimal r=new BigDecimal(Math.random());
							if(a.role.equals("nonAttacker_1")){
								BigDecimal r=new BigDecimal(Math.random());
								if(r.compareTo(k)==-1){
									if(b.role.equals("nonAttacker_1")){
										vote=1;
										a.csUpvoteCount++;
										b.csUpvotedCount++;	
										this.nonAttackerUpCount++;									
									}
									else{
										vote=0;
										a.csDownvoteCount++;
										b.csDownvotedCount++;	
										this.nonAttackerDownCount++;									
									}									
								}
							}
							else{//attacker_1
								if(b.role.equals("attacker_1")){
									BigDecimal r=new BigDecimal(Math.random());
									if(r.compareTo(w)==-1){
										vote=1;
										a.csUpvoteCount++;
										b.csUpvotedCount++;		
										this.attackerUpCount++;									
									}
								}
								// else{
								// 	vote=0;
								// 	a.csDownvoteCount++;
								// 	b.csDownvotedCount++;										
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
					// System.out.println("c:"+c);
				}				
			}
			else if(attackerType==1 && nonAttackerType==2){
				BigDecimal w = this.attackerWitnessUp;
				for (Agent a : agents1) {
					// int c=0;
					for (Agent b : agents2) {
						if(a.id!=b.id){
							String aid=a.id;
							String bid=b.id;
							Integer vote=-1;	
							// BigDecimal r=new BigDecimal(Math.random());
							if(a.role.equals("attacker_1")){
								if(b.role.equals("attacker_1")){
									BigDecimal r=new BigDecimal(Math.random());
									if(r.compareTo(w)==-1){
										vote=1;
										a.csUpvoteCount++;
										b.csUpvotedCount++;										
									}
								}
								// else{
								// 	vote=0;
								// 	a.csDownvoteCount++;
								// 	b.csDownvotedCount++;										
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
					// System.out.println("c:"+c);
				}					
			}	
			else if(attackerType==2 && nonAttackerType==1){
				BigDecimal k = this.nonAttackerWitness;
				BigDecimal w = this.attackerWitnessDown;
				for (Agent a : agents1) {
					// int c=0;
					for (Agent b : agents2) {
						if(a.id!=b.id){
							String aid=a.id;
							String bid=b.id;
							Integer vote=-1;	
							// BigDecimal r=new BigDecimal(Math.random());
							if(a.role.equals("nonAttacker_1")){
								BigDecimal r=new BigDecimal(Math.random());
								if(r.compareTo(k)==-1){
									if(b.role.equals("nonAttacker_1")){
										vote=1;
										a.csUpvoteCount++;
										b.csUpvotedCount++;		
										this.nonAttackerUpCount++;							
									}
									else{
										vote=0;
										a.csDownvoteCount++;
										b.csDownvotedCount++;	
										this.nonAttackerUpCount++;									
									}									
								}
							}
							else{//attacker_2
								if(b.role.equals("nonAttacker_1")){
									BigDecimal r=new BigDecimal(Math.random());
									if(r.compareTo(w)==-1){
										vote=0;
										a.csDownvoteCount++;
										b.csDownvotedCount++;
										this.attackerDownCount++;										
									}
								}
								// else{
								// 	vote=0;
								// 	a.csDownvoteCount++;
								// 	b.csDownvotedCount++;										
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
					// System.out.println("c:"+c);
				}				
			}	
			else if(attackerType==2 && nonAttackerType==2){
				BigDecimal w = this.attackerWitnessDown;
				for (Agent a : agents1) {
					for (Agent b : agents2) {
						if(a.id!=b.id){
							String aid=a.id;
							String bid=b.id;
							Integer vote=-1;	
							// BigDecimal r=new BigDecimal(Math.random());
							if(a.role.equals("attacker_2")){
								if(b.role.equals("nonAttacker_2")){
									BigDecimal r=new BigDecimal(Math.random());
									// System.out.println(r+" "+w);
									if(r.compareTo(w)==-1){
										vote=0;
										a.csDownvoteCount++;
										b.csDownvotedCount++;
										this.attackerDownCount++;	
									}
								}
								// else{
								// 	vote=0;
								// 	a.csDownvoteCount++;
								// 	b.csDownvotedCount++;										
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
			else if(attackerType==3 && nonAttackerType==1){
				
			}
			else if(attackerType==3 && nonAttackerType==2){
				
			}
			//a4n1
			else if(attackerType==4 && nonAttackerType==1){
				BigDecimal k = this.nonAttackerWitness;
				BigDecimal w = this.attackerWitnessDown;
				for (Agent a : agents1) {
					// int c=0;
					for (Agent b : agents2) {
						if(a.id!=b.id){
							String aid=a.id;
							String bid=b.id;
							Integer vote=-1;	
							// BigDecimal r=new BigDecimal(Math.random());
							if(a.role.equals("nonAttacker_1")){
								BigDecimal r=new BigDecimal(Math.random());
								if(r.compareTo(k)==-1){
									if(b.role.equals("nonAttacker_1")){
										vote=1;
										a.csUpvoteCount++;
										b.csUpvotedCount++;										
									}
									else{
										vote=0;
										a.csDownvoteCount++;
										b.csDownvotedCount++;										
									}									
								}
							}
							else{//attacker_2
								if(b.role.equals("nonAttacker_1")){
									BigDecimal r=new BigDecimal(Math.random());
									if(r.compareTo(w)==-1){
										vote=0;
										a.csDownvoteCount++;
										b.csDownvotedCount++;										
									}
								}
								// else{
								// 	vote=0;
								// 	a.csDownvoteCount++;
								// 	b.csDownvotedCount++;										
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
					// System.out.println("c:"+c);
				}					
			}
			else if(attackerType==4 && nonAttackerType==2){
				
			}
			else if(attackerType==5 && nonAttackerType==1){
				BigDecimal k = this.nonAttackerWitness;
				BigDecimal w = this.attackerWitnessUp;
				for (Agent a : agents1) {
					// int c=0;
					for (Agent b : agents2) {
						if(a.id!=b.id){
							String aid=a.id;
							String bid=b.id;
							Integer vote=-1;	
							// BigDecimal r=new BigDecimal(Math.random());
							if(a.role.equals("nonAttacker_1")){
								BigDecimal r=new BigDecimal(Math.random());
								if(r.compareTo(k)==-1){
									if(b.role.equals("nonAttacker_1")){
										vote=1;
										a.csUpvoteCount++;
										b.csUpvotedCount++;	
										this.nonAttackerUpCount++;									
									}
									else{
										vote=0;
										a.csDownvoteCount++;
										b.csDownvotedCount++;	
										this.nonAttackerDownCount++;									
									}									
								}
							}
							else{//attacker_1
								if(b.role.equals("attacker_5")){
									BigDecimal r=new BigDecimal(Math.random());
									if(r.compareTo(w)==-1){
										vote=1;
										a.csUpvoteCount++;
										b.csUpvotedCount++;		
										this.attackerUpCount++;									
									}
								}
								// else{
								// 	vote=0;
								// 	a.csDownvoteCount++;
								// 	b.csDownvotedCount++;										
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
					// System.out.println("c:"+c);
				}					
			}
			else if(attackerType==5 && nonAttackerType==2){
				BigDecimal w = this.attackerWitnessUp;
				for (Agent a : agents1) {
					// int c=0;
					for (Agent b : agents2) {
						if(a.id!=b.id){
							String aid=a.id;
							String bid=b.id;
							Integer vote=-1;	
							// BigDecimal r=new BigDecimal(Math.random());
							if(a.role.equals("attacker_5")){
								if(b.role.equals("attacker_5")){
									BigDecimal r=new BigDecimal(Math.random());
									if(r.compareTo(w)==-1){
										vote=1;
										a.csUpvoteCount++;
										b.csUpvotedCount++;										
									}
								}
								// else{
								// 	vote=0;
								// 	a.csDownvoteCount++;
								// 	b.csDownvotedCount++;										
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
					// System.out.println("c:"+c);
				}					
			}
			else if(attackerType==6 && nonAttackerType==1){
				BigDecimal k = this.nonAttackerWitness;
				BigDecimal w = this.attackerWitnessDown;
				for (Agent a : agents1) {
					// int c=0;
					for (Agent b : agents2) {
						if(a.id!=b.id){
							String aid=a.id;
							String bid=b.id;
							Integer vote=-1;	
							// BigDecimal r=new BigDecimal(Math.random());
							if(a.role.equals("nonAttacker_1")){
								BigDecimal r=new BigDecimal(Math.random());
								if(r.compareTo(k)==-1){
									if(b.role.equals("nonAttacker_1")){
										vote=1;
										a.csUpvoteCount++;
										b.csUpvotedCount++;										
									}
									else{
										vote=0;
										a.csDownvoteCount++;
										b.csDownvotedCount++;										
									}									
								}
							}
							else{//attacker_6
								if(b.role.equals("attacker_6")){
									BigDecimal r=new BigDecimal(Math.random());
									if(r.compareTo(w)==-1){
										vote=0;
										a.csDownvoteCount++;
										b.csDownvotedCount++;										
									}
								}
								// else{
								// 	vote=0;
								// 	a.csDownvoteCount++;
								// 	b.csDownvotedCount++;										
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
					// System.out.println("c:"+c);
				}				
			}
			else if(attackerType==6 && nonAttackerType==2){
				BigDecimal k = this.nonAttackerWitness;
				BigDecimal w = this.attackerWitnessDown;
				for (Agent a : agents1) {
					// int c=0;
					for (Agent b : agents2) {
						if(a.id!=b.id){
							String aid=a.id;
							String bid=b.id;
							Integer vote=-1;	
							if(a.role.equals("attacker_6")){//attacker_7
								if(b.role.equals("attacker_6")){
									BigDecimal r=new BigDecimal(Math.random());
									if(r.compareTo(w)==-1){
										vote=0;
										a.csDownvoteCount++;
										b.csDownvotedCount++;										
									}
								}
								// else{
								// 	vote=0;
								// 	a.csDownvoteCount++;
								// 	b.csDownvotedCount++;										
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
					// System.out.println("c:"+c);
				}				
			}			
			else if(attackerType==7 && nonAttackerType==2){
				BigDecimal k = this.nonAttackerWitness;
				BigDecimal w = this.attackerWitnessUp;
				for (Agent a : agents1) {
					// int c=0;
					for (Agent b : agents2) {
						if(a.id!=b.id){
							String aid=a.id;
							String bid=b.id;
							Integer vote=-1;	
							if(a.role.equals("attacker_7")){//attacker_7
								if(b.role.equals("nonAttacker_2")){
									BigDecimal r=new BigDecimal(Math.random());
									if(r.compareTo(w)==-1){
										vote=1;
										a.csUpvoteCount++;
										b.csUpvotedCount++;										
									}
								}
								// else{
								// 	vote=0;
								// 	a.csDownvoteCount++;
								// 	b.csDownvotedCount++;										
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
					// System.out.println("c:"+c);
				}				
			}
			else if(attackerType==7 && nonAttackerType==1){
				BigDecimal k = this.nonAttackerWitness;
				BigDecimal w = this.attackerWitnessUp;
				for (Agent a : agents1) {
					// int c=0;
					for (Agent b : agents2) {
						if(a.id!=b.id){
							String aid=a.id;
							String bid=b.id;
							Integer vote=-1;	
							// BigDecimal r=new BigDecimal(Math.random());
							if(a.role.equals("nonAttacker_1")){
								BigDecimal r=new BigDecimal(Math.random());
								if(r.compareTo(k)==-1){
									if(b.role.equals("nonAttacker_1")){
										vote=1;
										a.csUpvoteCount++;
										b.csUpvotedCount++;										
									}
									else{
										vote=0;
										a.csDownvoteCount++;
										b.csDownvotedCount++;										
									}									
								}
							}
							else{//attacker_7
								if(b.role.equals("nonAttacker_1")){
									BigDecimal r=new BigDecimal(Math.random());
									if(r.compareTo(w)==-1){
										vote=1;
										a.csUpvoteCount++;
										b.csUpvotedCount++;										
									}
								}
								// else{
								// 	vote=0;
								// 	a.csDownvoteCount++;
								// 	b.csDownvotedCount++;										
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
					// System.out.println("c:"+c);
				}					
			}
			// else if(attackerType==7 && nonAttackerType==2){
				
			// }
			else if(attackerType==8 && nonAttackerType==1){
				//inactive attacker
				//nonattacker witness k% of agents 
				BigDecimal k = this.nonAttackerWitness;
				for (Agent a : agents1) {
					// int c=0;
					for (Agent b : agents2) {
						if(a.id!=b.id){

								// c++;
							if(a.role.equals("nonAttacker_1")){
								String aid=a.id;
								String bid=b.id;
								Integer vote=-1;
								if(b.role.equals("nonAttacker_1")){
									BigDecimal r=new BigDecimal(Math.random());									
									if(r.compareTo(k)==-1){
										vote=1;
										a.csUpvoteCount++;
										b.csUpvotedCount++;	
									}									
								}
								else{
									BigDecimal r=new BigDecimal(Math.random());										
									if(r.compareTo(k)==-1){									
										vote=0;
										a.csDownvoteCount++;
										b.csDownvotedCount++;										
									}
								}
								}
							}
						}
					}
					// System.out.println("c:"+c);
				}

			
			else if(attackerType==8 && nonAttackerType==2){
				//do nothing for both inactive agents
			}
			else{
				System.out.println("#Attacker/nonAttacker type error");
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
		
		if(reviewerToProductAndVote.get(a)!=null){
			List<String> list = reviewerToProductAndVote.get(a);
			for(int i=0; i<list.size(); i++) {
			   double[][] CPT_crt_CS = CPT_CS;
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
		
		if(productToReviewerAndVote.get(a)!=null){
			List<String> list = productToReviewerAndVote.get(a);
			for(int i=0; i<list.size(); i++) {
			   double[][] CPT_crt_CS = CPT_CS;
			   String o = list.get(i);
			   // System.out.println("o:"+o);
			   String reviewer = o.split(" ")[0];
			   String vote=o.split(" ")[1];
			   // System.out.println(vote);
			   // System.out.println(vote.equals("1"));
				if(vote.equals("1")){
					int state_reviewer = stateMapRW.get(reviewer);
					alpha_T *= CPT_crt_CS[state_reviewer][T];
					alpha_F *= CPT_crt_CS[state_reviewer][F];
				}
				else{
					int state_reviewer = stateMapRW.get(reviewer);
					alpha_T *= 1 - CPT_crt_CS[state_reviewer][T];
					alpha_F *= 1 - CPT_crt_CS[state_reviewer][F];
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

		Neighborhood(int i, int neiSize, int nAttacker, int nNonAttacker, double priorCS, double priorRW,
			int attackerType, int nonAttackerType
		){
			neighbors = new HashMap<String, Agent>();
			int j=1;
			for(;j<=nAttacker;j++){
				String id = i+"_"+j;
				Agent attacker = new Agent(id, true, priorCS, priorRW);
				if(attackerType==8){
					attacker.role="attacker_8";
					attacker.e_binary=0;
					attacker.r_binary=-1;
					nnea++;
				}
				else if (attackerType==7){
					attacker.role="attacker_7";
					attacker.e_binary=0;
					attacker.r_binary=1;
					nnea++;
					nra++;
				}
				else if (attackerType==6){
					attacker.role="attacker_6";
					attacker.e_binary=0;
					attacker.r_binary=1;
					
					nnea++;		
					nra++;		
				}
				else if (attackerType==5){
					attacker.role="attacker_5";
					attacker.e_binary=1;
					attacker.r_binary=0;
					nea++;
					nnra++;				
				}
				else if (attackerType==4){
					attacker.role="attacker_4";
					attacker.e_binary=1;
					attacker.r_binary=0;
					nea++;
					nnra++;
				}
				// else if (attackerType==3){
				// 	attacker.role="attacker_3";
				// 	attacker.e_binary=1;
				// 	attacker.r_binary=0;
				// 	nnra++;
				// }
				else if (attackerType==2){
					attacker.role="attacker_2";
					attacker.e_binary=0;
					attacker.r_binary=0;
					nnea++;				
					nnra++;
				}
				else if (attackerType==1){
					attacker.role="attacker_1";
					attacker.e_binary=0;
					attacker.r_binary=0;
					nnea++;				
					nnra++;	
				}
				else {System.out.println("#Error: unknown attackerType when generating Neighborhood");}
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
				Agent nonAttacker = new Agent(id, false, priorCS, priorRW);
				if(nonAttackerType==1){
					nonAttacker.role="nonAttacker_1";
					nonAttacker.e_binary=1;
					nonAttacker.r_binary=1;
					nea++;
					nra++;					
				}
				else if(nonAttackerType==2){					
					nonAttacker.role="nonAttacker_2";
					nonAttacker.e_binary=1;
					nonAttacker.r_binary=-1;
					nea++;			
				}
				else {System.out.println("#Error: unknown nonAttackerType when generating Neighborhood");}
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
		boolean isAttacker;
		Agent(String id, boolean isAttacker, double priorCS, double priorRW){//inactive agent by default
			this.id = id;
			this.isAttacker=isAttacker;
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
			System.out.println("#Not Enough Arguments for DCP");
			return;
		}
		// long startTime = System.currentTimeMillis();
		DcpV2 dcp = new DcpV2(
			//int numNei, int neiSize, double nonAttackerPercentage,  Integer ob, int mcmcRounds, String pathOfCpt 
			Integer.parseInt(args[0]), //numNei
			Integer.parseInt(args[1]), // size of Neighborhood
			new BigDecimal(args[2]),//nonattackerPercentage
			new BigDecimal(args[3]),//nonAttackerWitness
			Integer.parseInt(args[4]),//ob
			Integer.parseInt(args[5]),//mcmc rounds
			args[6],//pathOfCpt
			Integer.parseInt(args[7]), //attackerType
			Integer.parseInt(args[8]), //nonAttackerType
			new BigDecimal(args[9]),//attackerWitnessUp
			new BigDecimal(args[10]),//attackerWitnessDown
			args[11]//filename to write out tpr, fpr
			);
		// long endTime   = System.currentTimeMillis();
		// long totalTime = endTime - startTime;
		// System.out.println("#time in minutes: "+ totalTime/1000.0/60);			
	}
}
