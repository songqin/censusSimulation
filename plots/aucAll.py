#
# Compute the area under the ROC curve (AUC) from input files
#


from __future__ import print_function

import numpy as np
from scipy.integrate import simps
from numpy import trapz

# f='WWW_E1_0_2_all'
# f='WWW_E1_'

# x,y=np.loadtxt(f, delimiter=' ', usecols=(1,0),unpack=True)
# # Compute the area using the composite trapezoidal rule.
# area = trapz(y, x)
# area = abs(area)
# print("WWW_E1_0_2_all", area)

for i in range(0,6):#number of HACs/Nei
	for j in range(0, 11):#number of attackers/Nei used to be 11, 42
		f = "WWW_E1_"+str(i)+"_"+str(j)+"_all" #WWW_E1, WWW_E2, WWW_E3, WWW_E4, UUU_E1, UUU_E2, UUU_E3, UUU_E4
		#WWW_E1_victim100_
		x,y=np.loadtxt(f, delimiter=' ', usecols=(1,0),unpack=True)
		# Compute the area using the composite trapezoidal rule.
		area = trapz(y, x)
		area = abs(area)
		print(f, area)

 #WWW_E1_victim100_6_20_allx