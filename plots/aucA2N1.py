from __future__ import print_function

import numpy as np
from scipy.integrate import simps
from numpy import trapz


# The y values.  A numpy array is used here,
# but a python list could also be used.
# y = np.array([5, 20, 4, 18, 19, 18, 7, 4])
# f='attackerWitness0.60'
# x,y=np.loadtxt(f, delimiter=' ', usecols=(1,0),unpack=True)
# # Compute the area using the composite trapezoidal rule.
# area = trapz(y, x)
# area = abs(area)
# print("area0.6=", area)

f='attackerWitness0.00'
x,y=np.loadtxt(f, delimiter=' ', usecols=(1,0),unpack=True)
# Compute the area using the composite trapezoidal rule.
area = trapz(y, x)
area = abs(area)
print("area 0=", area)

f='attackerWitness0.01'
x,y=np.loadtxt(f, delimiter=' ', usecols=(1,0),unpack=True)
# Compute the area using the composite trapezoidal rule.
area = trapz(y, x)
area = abs(area)
print("area 0.2=", area)

f='attackerWitness0.02'
x,y=np.loadtxt(f, delimiter=' ', usecols=(1,0),unpack=True)
# Compute the area using the composite trapezoidal rule.
area = trapz(y, x)
area = abs(area)
print("area 0.2=", area)



f='attackerWitness0.04'
x,y=np.loadtxt(f, delimiter=' ', usecols=(1,0),unpack=True)
# Compute the area using the composite trapezoidal rule.
area = trapz(y, x)
area = abs(area)
print("area 0.2=", area)


f='attackerWitness0.08'
x,y=np.loadtxt(f, delimiter=' ', usecols=(1,0),unpack=True)
# Compute the area using the composite trapezoidal rule.
area = trapz(y, x)
area = abs(area)
print("area 0.4=", area)


f='attackerWitness0.12'
x,y=np.loadtxt(f, delimiter=' ', usecols=(1,0),unpack=True)
# Compute the area using the composite trapezoidal rule.
area = trapz(y, x)
area = abs(area)
print("area0.5 =", area)


f='attackerWitness0.16'
x,y=np.loadtxt(f, delimiter=' ', usecols=(1,0),unpack=True)
# Compute the area using the composite trapezoidal rule.
area = trapz(y, x)
area = abs(area)
print("area0.5 =", area)