import java.util.*;
import java.io.*;
public class Test{
	static Random  rnd ;
	Test(){
		rnd = new Random(0);
	}
	public boolean sample(double d) {
		float v = random(1.0f);
		if (v < d)
			return true;
		return false;
	}
	public static float random(float max) {
		float result = rnd.nextFloat() * max;
		return result;
	}		
	public static void main(String[] args) {
		
		HashMap<Integer, List<String>> reviewerToProductAndVote;
		reviewerToProductAndVote=new HashMap<Integer, List<String>>();
		Integer a=1, b=2, vote=0;
		if(reviewerToProductAndVote.containsKey(a)){
			reviewerToProductAndVote.get(a).add(b+" "+vote);
		}
		else{
			List<String> l = new ArrayList<String>();
			l.add(b+" "+vote);
			reviewerToProductAndVote.put(a, l);
		}
		a=1;
		 b=4;
		  vote=1;
		if(reviewerToProductAndVote.containsKey(a)){
			reviewerToProductAndVote.get(a).add(b+" "+vote);
		}
		else{
			List<String> l = new ArrayList<String>();
			l.add(b+" "+vote);
			reviewerToProductAndVote.put(a, l);
		}
		for(Integer key:reviewerToProductAndVote.keySet()){
			List<String> list = reviewerToProductAndVote.get(key);
			for(int i=0; i<list.size(); i++) {
			   String o = list.get(i);
			   
			   System.out.println(o.split(" ")[0]);
			   System.out.println(o.split(" ")[1]);//Integer.parseInt
			}			
			// System.out.println("#"+key+" "+reviewerToProductAndVote.get(key));
		}

		// new List<String>() 
		Integer n=1;
		if(n==1){System.out.println("n==1");}
		String s="1";
		if(s=="1"){System.out.println("s==1");}
		int i=1;
		for(;i<=5;i++){}
		System.out.println(i);
		for(;i<=10;i++)System.out.println(i);
		String s1="12";
		String s2="12";
		if(s1==s2) System.out.println("equal");
		s2=12+"";
		if(s1==s2) System.out.println("equal");
		String s3 =null;
		String v="1 1".split(" ")[1];
		System.out.println(v);
		String s4="1";
		System.out.println(s4.equals(v));
		Test test = new Test();
		for(double l=0.1;l<1;l+=0.1){
			boolean mm = test.sample(l);
			System.out.println(mm);
		}
		for(int j=0;j<=2000;j+=100){
			System.out.println(j+" "+0.0);
		}
	}
}


