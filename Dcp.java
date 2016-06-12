//decentralized census problem
import java.util.*;
public class Dcp{
	int numNei;//number of neighborhoods in DCP
	int neiSize;//number of residents per neighborhood
	int p;//population
	double hap;//honest agent percentage (eligible=1 and reliable=1)
	double fap;//false agent percentage(not eligible (0), reliable(2 ,unknown,inactive))
	double map;//malicious agent percentage (not reliable(0), but eligible(1))
	double mawp;//malicious agent witness percentage
	double hawp;//honest agent wintess percentage
	int ob;//id of the observer
	int nha=0;//number of honest agents
	int nfa=0;//number of false agents
	int nma=0;//number of malicious agents
	// note: n - nha-nfa-nma is inactive aligible agents
	ArrayList<Neighborhood> neighborhoodSet;
	Dcp(int numNei, int neiSize, double hap, double fap, double map, 
		double mawp, double hawp ){
		System.out.println("DCP Simulation Init");
		this.numNei = numNei;
		this.neiSize = neiSize;
		this.p = numNei * neiSize;
		this.fap = fap;
		this.map = map;
		this.mawp = mawp;
		this.hawp = hawp;
		neighborhoodSet = new ArrayList<Neighborhood>();
		for(int i=1; i<=numNei;i++){
			// 1 100
			// 101 200
			// 201 300
			Neighborhood n = new Neighborhood(i, neiSize, hap, map );
			neighborhoodSet.add(n);
		}
		for(Neighborhood n: neighborhoodSet){
			HashMap<Integer, Agent> m= n.agentMap;
			for(Integer k: m.keySet()){
				// System.out.println(k.toString()+" "+m.get(k).role.toString());
				if(m.get(k).role==1) nha++;
				else if(m.get(k).role==2) nfa++;
				else if(m.get(k).role==3) nma++;
			}
		}
			System.out.println(nha*1.0/p);
			System.out.println(nfa*1.0/p);
			System.out.println(nma*1.0/p);
			System.out.println((p-nha-nfa-nma)*1.0/p);

		System.out.println("Done: Dcp constructor");
	}
	void printAgents(){}
	void printTprFpr(){}
	void printWitnessSet(){}
	class Neighborhood{
		ArrayList<Integer> agentSet;
		HashMap<Integer, Agent> agentMap = new HashMap<Integer, Agent>();
		Neighborhood(int i, int neiSize, double hap, double map){
			agentSet = new ArrayList<Integer>();
			for(int j = (i-1)*neiSize+1; j<=i*neiSize;j++){
				Agent a = new Agent(j);
				double r = Math.random();
				if(r<hap) {//honest agents
					a.role=1;
					// a.e_prob=
					a.e_binary=1;
					a.r_binary=1;
				}
				else if(r>=hap && r<hap+fap){//false agents
					a.role=2;
					// a.e_prob=
					a.e_binary=0;
					a.r_binary=-1;
				}
				else if(r>=hap+fap && r<hap+fap+map){//malicious agents
					a.role=3;
					// a.e_prob=
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
		//role=2 fake agents (reliable= -1, eligible=0)
		//role=3 malicious agents (reliable=0, eligible=1)
		//role=0 inactive agents (eligible=1 , reliable = -1)
		double e_prob;//ramdom var for eligibility
		double r_prob;//random var for reliability
		int e_binary;//groud truth for eligibility 1/0
		int r_binary;//ground truth for reliability 1/0/-1 -1 for unknown

		Agent(Integer id){
			this.id = id;
			this.role = 0;
			this.e_binary=1;
			this.r_binary=-1;
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
			Double.parseDouble(args[3]),//fap
			Double.parseDouble(args[4]),//map
			Double.parseDouble(args[5]),//mawp
			Double.parseDouble(args[6])//hawp
			);
	}
}