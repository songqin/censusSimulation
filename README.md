# censusSimulation
To compile: javac *java
To run: java PDCPExp1 100 100 10 5 100 1 WWW_E1_5



			int numNei=Integer.parseInt(args[0]); //numNei
			int neiSize=Integer.parseInt(args[1]); // neiSize
			double hap=Double.parseDouble(args[2]);//hap
			double map=Double.parseDouble(args[3]);//map
			double fap=Double.parseDouble(args[4]);//fap
			double hawp=Double.parseDouble(args[5]);//hawp
			double mawp=Double.parseDouble(args[6]);//mawp
			double fawp=Double.parseDouble(args[7]);//fawp
			int ob=Integer.parseInt(args[8]);//ob
			int n=Integer.parseInt(args[9]);//mcmc rounds
			String pathOfCpt=args[10];//pathOfCpt
			String chaningPara=args[11];//which para to vary. hap, map, etc.
			

java Dcp 
roc1.1: 
java Batch 1 100 0.05 0.05 0.05 0.05 0.05 0.05 0 5 ./cpt.txt map>>./plots/roc1.1.dat
 plot 'roc1.1.dat' using 3:2 with points
conclusion: map not sensitive


roc2.1
java Batch 1 100 0.05 0.05 0.05 0.05 0.05 0.05 0 5 ./cpt.txt fap>>./plots/roc2.1.dat
conclusion: fap is not sensitive, because FA are honest. Future work is make them dishonest.


roc3.1
java Batch 1 100 0.05 0.05 0.05 0.05 0.05 0.05 0 5 ./cpt.txt mawp>>./plots/roc3.1.dat
hypo: mawp could be (the only) very sensitive
conclusion: very sensitive. but TPR is very high. 


roc3.2 increase mcmc rounds to 1000 to validate the convergence
java Batch 1 100 0.05 0.05 0.05 0.05 0.05 0.05 0 1000 ./cpt.txt mawp>>./plots/roc3.2.dat
hypo: mawp could be (the only) very sensitive
conclusion: very sensitive. but TPR is very high. 

roc4.1
hypo: map+mwap combo attack is more sensitive and malicious. they could lower the TPR.
java Batch 1 100 0.05 0.2 0.05 0.05 0.05 0.05 0 5 ./cpt.txt mawp>>./plots/roc4.1.dat

roc4.2
hypo: map+mwap combo attack is more sensitive and malicious. they could lower the TPR.
java Batch 1 100 0.05 0.5 0.05 0.05 0.05 0.05 0 5 ./cpt.txt mawp>>./plots/roc4.2.dat

roc4.3
hypo: map+mwap combo attack is more sensitive and malicious. they could lower the TPR.
java Batch 1 100 0.05 0.8 0.05 0.05 0.05 0.05 0 5 ./cpt.txt mawp>>./plots/roc4.3.dat


Thinking: Should FA be modelled as honest or not?

gnuplot
plot 'roc4.3.dat' using 3:2 with points

experiments:
java Batch3 1 100 0.05 0.05 0.05 0.05 0.05 0.05 0 100 ./cpt.txt mawp>>./plots/roc5.1.dat


experiments
 java Dcp 1 100 0.01666666666666657 0.9500000000000003 0.01666666666666657 0.05 0.9000000000000002 0.05 0 100 ./cpt.txt 1
 #93.87999999999995 0.0 8.48 0.0
 #95 5 9 91
 100 0.988210526315789 0.0 0.9422222222222223 0.0

type of agents, eligible/not, reliable/not, active/inactive
experiments: only fake and honest and inactive 
 java Dcp 1 100 0.05 0.9500000000000003 0.01666666666666657 0.05 0.9000000000000002 0.05 0 100 ./cpt.txt 1 >>./plots/r1.1.dat

 experiments: improve runtime
 dcp.java vs dcpV2.java
java Dcp 1 100 0.05 0.05 0.05 




--------------------
a8n1 redo

//attackerType==8 && nonAttackerType==1
meaningful
 java BatchA8N1 0.4
 java BatchA8N1 0.3
 java BatchA8N1 0.2
 java BatchA8N1 0.1

--------------------
a8n2 redo
//attackerType==8 && nonAttackerType==2
meaningful:?
 java BatchA1N1 0.4 | folder:A1N1nonAttacker0.4
java BatchA1N1 0.5
--------------------
a1n1
java BatchA1N1V2 0.95
java BatchA1N1V2 0.9
java BatchA1N1V2 0.85
java BatchA1N1V2 0.80
--------------------
a1n2

--------------------
a4 all eligible, to false positive = 0  
--------------------
a7n2 plot is interesting
a7n1 not interesting
--------------------
a6n2 interesting
a6n1 
--------------------


more population
reduced to experiment mcmc round to 1000 to save some time
--------------------
a1n1

--------------------
7 26 16
bug: witnessSet has 2. only 1 is populated
double precision problem->solution BigDecimal
java DcpV2 1 100 0.9 0.0 0 1000 ./cpt.txt 2 2 0.0 0.05 attackerWitness0.05

--------------------
7 27 16
populate witness stance: too random not enough witness stances
learned:compute area under the curve todo: read theory of trapz, nump, scipy
rerun a1n1 experiments to see if result is the same after fixed bug (reviewerToProductAndVote)
make sure: edit populateWitness for each experiments
for a1n1 ap=5%, when awfp=20%, 1/5, each will witness 1, 40%, witness 2, 60% for 3, 80% for 4, 100% for 5 agents. 
--------------------
7 28 16
explain how witness is populated with probability
--------------------
--------------------
--------------------

--------------------
--------------------
--------------------

--------------------
--------------------
--------------------

--------------------
--------------------
--------------------

--------------------
--------------------
--------------------
--------------------
--------------------
--------------------
--------------------
--------------------
--------------------
--------------------
--------------------
--------------------
--------------------
--------------------
--------------------
--------------------
--------------------
--------------------
--------------------
anchoring neighborhood
experiment 1
java DCPCorruptedNeighborhood 1 10 0.0 0.0 0 10000 ./cpt.txt 1 1 1.0 0 attackerWitness1.00

java DCPCorruptedNeighborhood 1 100 0.0 0.0 0 10000 ./cpt.txt 1 1 1.0 0 attackerWitness1.00

experiment 2
java DCPCorruptedNeighborhood 1 10 0.1 0.0 0 1000 ./cpt.txt 2 2 0 1.0  attackerWitness1.00
java DCPCorruptedNeighborhood 1 100 0.1 0.0 0 1000 ./cpt.txt 2 2 0 1.0  attackerWitness1.00