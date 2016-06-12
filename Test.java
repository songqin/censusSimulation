//decentralized census problem
import java.util.*;
public class Test{
	int numNei;//number of neighborhoods in DCP
	int neiSize;//number of residents per neighborhood
	int p;
	double hap;//honest agent percentage
	double fap;//false agent percentage
	double map;//malicious agent percentage
	double mawp;//malicious agent witness percentage
	double hawp;//honest agent wintess percentage
	int ob;//id of the observer
	ArrayList<Neighborhood> neighborhoodSet;
	Test(int numNei, int neiSize, double hap, double fap, double map, 
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
			int pos=0,neg=0;
			for(Integer k: m.keySet()){
				// System.out.println(k.toString()+" "+m.get(k).role.toString());
				if(m.get(k).role==1) pos++;
				else neg++;
			}
			// System.out.println(pos+" "+neg);
			double d = pos*1.0/(pos+neg);

			System.out.println(pos*1.0/(pos+neg));
			// System.out.println(neg*1.0/(pos+neg));

		}
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
				Agent a = new Agent();
				agentMap.put(j, a);
			}
		}
		
	}

	/*
	args[0 ... n]
	int numNei;//number of neighborhoods in DCP
	int neiSize;//number of residents per neighborhood
	*/
	public static void main(String[] args){
		HashMap<Integer, Agent> m=  new HashMap<Integer, Agent>();
		m.put (1, new Agent());
		for(Integer k: m.keySet()){
			System.out.println(k.toString()+" "+m.get(k).role.toString());
		}		
		m.get(1).role=1;
		for(Integer k: m.keySet()){
			System.out.println(k.toString()+" "+m.get(k).role.toString());
		}	

	}
}
	 class Agent{
		Integer role;//1: 
		Agent(){
			this.role = 0;
		}
	}