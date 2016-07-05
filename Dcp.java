//decentralized census problem
import java.util.*;
import java.io.*;

public class Dcp{
	int fast=1;
	int printLess=1;
	int numNei;//number of neighborhoods in DCP
	int neiSize;//number of residents per neighborhood
	int p;//population
	double hap;//honest agent percentage (eligible=1 and reliable=1)
	double fap;//false agent percentage(not eligible (0), reliable(2 ,unknown,inactive))
	double map;//malicious agent percentage (not reliable(0), but eligible(1))
	double mawp;//malicious agent witness percentage
	double hawp;//honest agent witness percentage
	double fawp;//fake agnet witness percentage
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
	 // = new double[] { 0, 0.75, 0.95 }; //0 (just an offset) is not used.
	double CPT_CS[][]; // CPT[cs_a,rw_b] Conditional Probability Table for eligibility
	//map for witness stances, "id id" -> vote(0/1)
	HashMap<String, Integer> witnessMapCS;
	// query variables
	int N[][]; //A vector of count for each value of hidden variables (RW, CS), initially zero TODO: Rename RW, CS
	//stateMap stores states for both reviewers and products
	HashMap<Integer, Integer> stateMapRW ;
	HashMap<Integer, Integer> stateMapCS ;
	HashMap<Integer, Boolean> fixedMapRW ;
	HashMap<Integer, Boolean> fixedMapCS ;
	//vector for helpfulness and quality
	//vector for counting helpful 
	HashMap<Integer, Integer> NRWMapT;
	//vector for counting not-helpful 
	HashMap<Integer, Integer> NRWMapF;
	//vector for counting good quality 
	HashMap<Integer, Integer> NCSMapT;
	//vector for counting bad quality
	HashMap<Integer, Integer> NCSMapF ;
	// HashMap<Integer, Double> rwMap;
	// HashMap<Integer, Double> csMap;	
	HashMap<Integer, Agent> agentStatsMap ;
	int fixedCS = 0;	
	int fixedRW = 0;	
	int mcmcRounds=0;
	int dummy;
	// int c;//parameter passed from Batch class
	static Random rnd = new Random(0);
	// load()
	// init() 
	// mcmc()
	ArrayList<Neighborhood> neighborhoodSet;
	public static float random(float max) {
		float result = rnd.nextFloat() * max;
		return result;
	}	
	Dcp(int numNei, int neiSize, double hap, double map, double fap, 
		double hawp, double mawp, double fawp, Integer ob, int mcmcRounds, String pathOfCpt 
		,int dummy
		){
		this.numNei = numNei;
		this.neiSize = neiSize;
		this.p = numNei * neiSize;
		this.fap = fap;
		this.map = map;
		this.mawp = mawp;
		this.hawp = hawp;
		this.fawp = fawp;
		this.ob=ob;
		this.pathOfCpt = pathOfCpt;
		this.mcmcRounds = mcmcRounds;
		this.dummy=dummy;
		// this.c = c;
		agentStatsMap = new HashMap<Integer, Agent>();
		loadFromHardDrive();//load cpt
		// printCPT();
		neighborhoodSet = new ArrayList<Neighborhood>();
		for(int i=1; i<= numNei;i++){
			// 1 100
			// 101 200
			// 201 300
			Neighborhood n = new Neighborhood(i, neiSize, hap, map, this.prior[CS], this.prior[RW] );
			neighborhoodSet.add(n);
		}
		//remove following code for efficiency
		for(Neighborhood n: neighborhoodSet){
			HashMap<Integer, Agent> m= n.agentMap;
			for(Integer k: m.keySet()){
				agentStatsMap.put(k, m.get(k));
				// System.out.println(k.toString()+" "+m.get(k).role.toString());
				Agent a = m.get(k);
				if(a.role==1) nha++;
				else if(m.get(k).role==2) nfa++;
				else if(m.get(k).role==3) nma++;
				if(a.role!=2) nea++;
				if(a.role!=3) nra++;
				if(a.role==2) nnea++;
				if(a.role==3) nnra++;
			}
		}

		// printNeighborhoodAndAgents();
		populateToyWitnessStances();
		// populateWitnessStances();
		// printWitness();
		mcmc(mcmcRounds);
		// printNeighborhoodAndAgents();
		printTprFpr(mcmcRounds);
		// printAllAgents();
		printOneAgent(1);
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
	void test(){
		// for(int i=0;i<=1000;i++)
		// {System.out.println("(int) Math.floor(random(2)) "+(int) Math.floor(random(2)));}
		Agent a = agentStatsMap.get(1);
		System.out.println(dummy-1+" "+
		a.id+" "+
		a.role+" "+
		a.e_binary+" "+
		a.r_binary+" "+
		a.e_prob+" "+
		a.r_prob
		);
	}
	void printOneAgent(int id){
		//role=1 honest agents (eligible=1, reliable=1), 
		//role=2 fake agents (reliable= 1 -
		//(want to use reliability to hide the fact of being fake), eligible=0)
		//role=3 malicious agents (reliable=0, eligible=1)
		//role=0 inactive agents (eligible=1 , reliable = 1)		
		// System.out.println("Computing agent's statistics");
		for(Agent a: agentStatsMap.values()){
			if(a.id==id)
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
		for(Agent a: agentStatsMap.values()){
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
	void printNeighborhoodAndAgents(){
		for(Neighborhood n: neighborhoodSet){
			System.out.println("#neighborhood: "+n.id);
			HashMap<Integer, Agent> m = n.agentMap;
			Collection<Agent> agents = m.values();
			System.out.println("#id role e_binary r_binary e_prob r_prob");
			for (Agent a : agents) {
				System.out.println("#"+
					a.id+" "+
					a.role+" "+
					a.e_binary+" "+
					a.r_binary+" "+
					a.e_prob+" "+
					a.r_prob
					);
			}
		}
	}
	void printTprFpr(int round){
		double tCS=0;//expectation of CS being true
		double fCS=0;
		double tRW=0;
		double fRW=0;
		// System.out.println("id, role, e_binary,r_binary,e_prob, r_prob");
		for(Agent a: agentStatsMap.values()){
			if(a.e_binary==1)
				tCS+=a.e_prob;
			else{
				fCS+=a.e_prob;
				// System.out.println(a.e_binary);
				// System.out.println(fCS);
				}
			if(a.r_binary==1)
				tRW+=a.r_prob;
			else
				fRW+=a.r_prob;
		}	
		// System.out.println("#expectation: CS(true), CS(false),RW(true),RW(false)");
		System.out.println("#"+tCS+" "+fCS+" "+tRW+" "+fRW);
		// System.out.println("#"+"ground truth:nea nnea nra nnra");
		System.out.println("#"+nea+" "+ nnea+" " +nra+" "+ nnra );
		// System.out.println("#tpr(CS) fpr(CS) tpr(RW) fpr(RW)");
		System.out.println(round+" "+tCS*1.0/nea+" "+fCS*1.0/nnea+" "+tRW*1.0/nra+" "+fRW*1.0/nnra);
		System.out.println();
	}
	void printWitness(){
		System.out.println("#Witness Stances:"+"size="+witnessMapCS.size());
		for(String key:witnessMapCS.keySet()){
			System.out.println("#"+key+" "+witnessMapCS.get(key));
		}
	}
	void loadFromHardDrive(){
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
		witnessMapCS = new HashMap<String, Integer>();
		stateMapRW = new HashMap<Integer, Integer>();
		stateMapCS = new HashMap<Integer, Integer>();
		fixedMapRW = new HashMap<Integer, Boolean>();
		fixedMapCS = new HashMap<Integer, Boolean>();
		NRWMapT = new HashMap<Integer, Integer>();
		//vector for counting not-helpful 
		NRWMapF = new HashMap<Integer, Integer>();
		//vector for counting good quality 
		NCSMapT = new HashMap<Integer, Integer>();
		//vector for counting bad quality
		NCSMapF = new HashMap<Integer, Integer>();
		
		for(Neighborhood n: neighborhoodSet){
			Collection<Agent> agents = n.agentMap.values();
			for (Agent a : agents) {
				int reviewer = a.id;
				int product = a.id;
				if(!stateMapRW.containsKey(reviewer))
					stateMapRW.put(reviewer, (int) Math.floor(random(2)));//0 or 1
				if(!stateMapCS.containsKey(product))
					stateMapCS.put(product, (int) Math.floor(random(2)));//0 or 1
				if(!fixedMapRW.containsKey(reviewer))
					fixedMapRW.put(reviewer, false);
				if(!fixedMapCS.containsKey(product))
					fixedMapCS.put(product, false);
				if(!NRWMapT.containsKey(reviewer))
					NRWMapT.put(reviewer, 0);
				if(!NRWMapF.containsKey(reviewer))
					NRWMapF.put(reviewer, 0);
				if(!NCSMapT.containsKey(product))
					NCSMapT.put(product, 0);
				if(!NCSMapF.containsKey(product))
					NCSMapF.put(product, 0);
			}
		}

		for(int i=2;i<=0;i++){
			witnessMapCS.put(i+" " +1, 1);
		}
		for(int i=2;i<=88;i++){
			witnessMapCS.put(i+" " +1, 0);
		}
		for(int i=2;i<=2;i++){
			witnessMapCS.put(1+" " +i, 1);
		}
		for(int i=2;i<=81;i++){
			witnessMapCS.put(1+" " +i, 0);
		}

	}

	void populateWitnessStances(){
		witnessMapCS = new HashMap<String, Integer>();
		stateMapRW = new HashMap<Integer, Integer>();
		stateMapCS = new HashMap<Integer, Integer>();
		fixedMapRW = new HashMap<Integer, Boolean>();
		fixedMapCS = new HashMap<Integer, Boolean>();
		NRWMapT = new HashMap<Integer, Integer>();
		//vector for counting not-helpful 
		NRWMapF = new HashMap<Integer, Integer>();
		//vector for counting good quality 
		NCSMapT = new HashMap<Integer, Integer>();
		//vector for counting bad quality
		NCSMapF = new HashMap<Integer, Integer>();
		/*
		for(Neighborhood n: neighborhoodSet){
			Collection<Agent> agents = n.agentMap.values();
			for (Agent a : agents) {
				int reviewer = a.id;
				int product = a.id;
				if(!stateMapRW.containsKey(reviewer))
					stateMapRW.put(reviewer, (int) Math.floor(random(2)));//0 or 1
				if(!stateMapCS.containsKey(product))
					stateMapCS.put(product, (int) Math.floor(random(2)));//0 or 1
				if(!fixedMapRW.containsKey(reviewer))
					fixedMapRW.put(reviewer, false);
				if(!fixedMapCS.containsKey(product))
					fixedMapCS.put(product, false);
				if(!NRWMapT.containsKey(reviewer))
					NRWMapT.put(reviewer, 0);
				if(!NRWMapF.containsKey(reviewer))
					NRWMapF.put(reviewer, 0);
				if(!NCSMapT.containsKey(product))
					NCSMapT.put(product, 0);
				if(!NCSMapF.containsKey(product))
					NCSMapF.put(product, 0);
			}
		}
		for(int i=2;i<=dummy;i++){
			witnessMapCS.put(i+" 1", 1);
		}
		*/
		for(Neighborhood n: neighborhoodSet){
			HashMap<Integer, Agent> m = n.agentMap;
			Collection<Agent> agents1 = m.values();
			Collection<Agent> agents2 = m.values();
			for (Agent a : agents1) {
				int reviewer = a.id;
				int product = a.id;
				if(!stateMapRW.containsKey(reviewer))
					stateMapRW.put(reviewer, (int) Math.floor(random(2)));//0 or 1
				if(!stateMapCS.containsKey(product))
					stateMapCS.put(product, (int) Math.floor(random(2)));//0 or 1
				if(!fixedMapRW.containsKey(reviewer))
					fixedMapRW.put(reviewer, false);
				if(!fixedMapCS.containsKey(product))
					fixedMapCS.put(product, false);
				if(!NRWMapT.containsKey(reviewer))
					NRWMapT.put(reviewer, 0);
				if(!NRWMapF.containsKey(reviewer))
					NRWMapF.put(reviewer, 0);
				if(!NCSMapT.containsKey(product))
					NCSMapT.put(product, 0);
				if(!NCSMapF.containsKey(product))
					NCSMapF.put(product, 0);	
				for (Agent b : agents2) {
					String key = a.id+" "+b.id;
					int vote=-1;
					if(a.id!=b.id){
						double r = Math.random();
						if(a.role==1){//honest
							if(hawp>0){
								if(r<hawp){//witness then happens
									if(b.role==2){//opposing witness
										vote=0;
									}
									else{//endorsing witness
										vote=1;
									}
								}
							}
						}
						if(a.role==2){//fake
							if(fawp>0){
								if(r<fawp){//witness then happens
									if(b.role==2){
										vote=0;
									}
									else{
										vote=1;
									}
								}
							}
						}
						if(a.role==3){//malicious 
							if(mawp>0){
								if(r<mawp){//witness then happens
									if(b.role==2){
										vote=1;
									}
									else{
										vote=0;
									}
								}
							}
						}
						if(vote!=-1){
							witnessMapCS.put(key, vote);
							if(vote==1){//upvote
								a.csUpvoteCount++;
								b.csUpvotedCount++;
							}
							else{
								a.csDownvoteCount++;
								b.csDownvotedCount++;
							}

							// System.out.println(key+" "+vote);
						}
					}
				}			
			}
		}	//end of for
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
			for(Integer k: NRWMapT.keySet()){
				if(!fixedMapRW.get(k)){
					mcmc_A_RW(k);
				}
			}
			for(Integer k: NCSMapT.keySet()){
				if(!fixedMapCS.get(k)){
					mcmc_A_CS(k);		
				}
			}

	// 		if(rwMap==null)
	// 			rwMap=new HashMap<Integer, Double>();
	// 		if(csMap==null)
	// 			csMap=new HashMap<Integer, Double>();
	// 		for(Integer k: NRWMapT.keySet()){
	// 			int countT = NRWMapT.get(k);
	// 			int countF = NRWMapF.get(k);
	// 			agentStatsMap.get(k).r_prob= countT*1.0/(countT+countF);
	// 			// rwMap.put(k, countT*1.0/(countT+countF));
	// 		}

	// 		for(Integer k: NCSMapT.keySet()){
	// 			int countT = NCSMapT.get(k);
	// 			int countF = NCSMapF.get(k);
	// 			agentStatsMap.get(k).e_prob= countT*1.0/(countT+countF);
	// 			// csMap.put(k, countT*1.0/(countT+countF));
	// //			System.out.println("product: "+ k+" quality:" + countT*1.0/(countT+countF));
	// 		}
	// 		// if((i+1)%1000==0)
	// 			printAllAgents(i+1);
	
			// System.out.println(i+1+" "+agentStatsMap.get(1).r_prob+" "+agentStatsMap.get(1).e_prob);

		}
		// if(rwMap==null)
		// 	rwMap=new HashMap<Integer, Double>();
		// if(csMap==null)
		// 	csMap=new HashMap<Integer, Double>();
		for(Integer k: NRWMapT.keySet()){
			int countT = NRWMapT.get(k);
			int countF = NRWMapF.get(k);
			agentStatsMap.get(k).r_prob= countT*1.0/(countT+countF);
			// rwMap.put(k, countT*1.0/(countT+countF));
		}

		for(Integer k: NCSMapT.keySet()){
			int countT = NCSMapT.get(k);
			int countF = NCSMapF.get(k);
			agentStatsMap.get(k).e_prob= countT*1.0/(countT+countF);
			// csMap.put(k, countT*1.0/(countT+countF));
//			System.out.println("product: "+ k+" quality:" + countT*1.0/(countT+countF));
		}
		
	}	
	//inference RW of
	public void mcmc_A_RW(Integer a) {

		double alpha_T = 1 * prior[RW];
		double alpha_F = 1 * (1 - prior[RW]);
		//if in file
		// if(rwMap!=null && rwMap.containsKey(a)){
		// 	double rw = rwMap.get(a);
		// 	alpha_T = 1 * rw;
		// 	alpha_F = 1 * (1 - rw);
		// }
		// double rw = agentStatsMap.get(a).r_prob;
		// double alpha_T = 1 * rw;
		// double alpha_F = 1 * (1 - rw);
		double[][] CPT_crt_CS = CPT_CS;
		//enumerate all the reviewers and products
		for(String k: witnessMapCS.keySet()){
			Integer reviewer = Integer.parseInt(k.toString().split(" ")[0]);
			Integer product = Integer.parseInt(k.toString().split(" ")[1]);
			
			//todo: more efficient da for witness stances

			//when a is the reviewers, 
			if(a==reviewer){
				Integer vote = witnessMapCS.get(k);
				if(vote==1){
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
	//inference CS of a
	public void mcmc_A_CS(Integer a) {
		//TODO: count number of good product
		double alpha_T = 1 * prior[CS];
		double alpha_F = 1 * (1 - prior[CS]);
		// double cs = agentStatsMap.get(a).e_prob;
		// double alpha_T = 1 * cs;
		// double alpha_F = 1 * (1 - cs);
		// if(csMap!=null && csMap.containsKey(a)){
		// 	double cs = rwMap.get(a);
		// 	alpha_T = 1 * cs;
		// 	alpha_F = 1 * (1 - cs);
		// }
		//enumerate all the reviewers and products
		for(String k: witnessMapCS.keySet()){
			Integer reviewer = Integer.parseInt(k.toString().split(" ")[0]);
			Integer product = Integer.parseInt(k.toString().split(" ")[1]);
			double[][] CPT_crt_CS = CPT_CS;
			//when a is the products
			if(a==product){
				// System.out.println("a is product");
				Integer vote = witnessMapCS.get(k);
				if(vote==1){
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
	//test
	public int sample(double d) {
		float v = random(1.0f);
		if (v < d)
			return T;
		return F;
	}	
	class Neighborhood{
		ArrayList<Integer> agentSet;
		int id;
		HashMap<Integer, Agent> agentMap = new HashMap<Integer, Agent>();
		Neighborhood(int i, int neiSize, double hap, double map, double priorCS, double priorRW){
			id=i;
			agentSet = new ArrayList<Integer>();
			for(int j = (i-1)*neiSize+1; j<=i*neiSize;j++){
				Agent a = new Agent(j, priorCS, priorRW);
				double r = Math.random();			
				if(r<hap) {//honest agents
					a.role=1;
					a.e_binary=1;
					a.r_binary=1;
				}
				else if(r>=hap && r<hap+fap){//false agents
					a.role=2;
					a.e_binary=0;
					a.r_binary=1;
				}
				else if(r>=hap+fap && r<hap+fap+map){//malicious agents
					a.role=3;
					a.e_binary=1;
					a.r_binary=0;				
				}				
				agentMap.put(j, a);
			}
			// for(int j = (i-1)*neiSize+1; j<=i*neiSize;j++){
			// 	if(agentMap.get(j).role==1)
			// 	if(Math.random()<hap) {
			// 		r=1;
			// 	}
			// 	agentMap.put(j, a);
			// }			
		}
		
	}
	class Agent{
		Integer id;
		Integer role;
		//role=1 honest agents (eligible=1, reliable=1), 
		//role=2 fake agents (reliable= 1 -
		//(want to use reliability to hide the fact of being fake), eligible=0)
		//role=3 malicious agents (reliable=0, eligible=1)
		//role=0 inactive agents (eligible=1 , reliable = 1)
		double e_prob;//ramdom var for eligibility
		double r_prob;//random var for reliability
		int e_binary;//groud truth for eligibility 1/0
		int r_binary;//ground truth for reliability 1/0
		int csDownvoteCount;//numver of votes of the current agent casted to others
		int csUpvoteCount;
		int csDownvotedCount;//number of votes from others
		int csUpvotedCount;
		Agent(Integer id, double priorCS, double priorRW){//inactive agent by default
			this.id = id;
			this.role = 0;
			this.e_binary=1;
			this.r_binary=1;
			this.e_prob=priorCS;
			this.r_prob=priorRW;
			this.csDownvoteCount=0;
			this.csUpvoteCount=0;
			this.csDownvotedCount=0;
			this.csUpvotedCount=0;
		}
	}
	/*
	args[0 ... n]
	int numNei;//number of neighborhoods in DCP
	int neiSize;//number of residents per neighborhood
	*/
	public static void main(String[] args){
		if(args.length<=0) {
			System.out.println("Not Enough Arguments for DCP");
			return;
		}
		Dcp dcp = new Dcp(
			Integer.parseInt(args[0]), //numNei
			Integer.parseInt(args[1]), // neiSize
			Double.parseDouble(args[2]),//hap
			Double.parseDouble(args[3]),//map
			Double.parseDouble(args[4]),//fap
			Double.parseDouble(args[5]),//hawp
			Double.parseDouble(args[6]),//mawp
			Double.parseDouble(args[7]),//fawp
			Integer.parseInt(args[8]),//ob
			Integer.parseInt(args[9]),//mcmc rounds
			args[10]//pathOfCpt
			, Integer.parseInt(args[11])//dummy
			);
	}
}
