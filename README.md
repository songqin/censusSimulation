# censusSimulation
population = 10 million
25 roc
Ex1: java Dcp 2 1000 0.05 0.05 0 0.1 0.1 0 ./cpt.txt

bookmark: draw convergence of roc when N is changed
todo: vary hap or hawp (both could be dominatin para), plot roc for dif map (mawp)



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