from __future__ import print_function

import numpy as np
from scipy.integrate import simps
from numpy import trapz


# The y values.  A numpy array is used here,
# but a python list could also be used.
# y = np.array([5, 20, 4, 18, 19, 18, 7, 4])
f='A1N1nonAttackerWitness0.05attackerWitness0.00'
x,y=np.loadtxt(f, delimiter=' ', usecols=(1,0),unpack=True)
# Compute the area using the composite trapezoidal rule.
area = trapz(y, x)
area = abs(area)
print("area =", area)
# Compute the area using the composite Simpson's rule.
# area = simps(y, x)
# print("area =", area)




f='A1N1nonAttackerWitness0.05attackerWitness0.20'
x,y=np.loadtxt(f, delimiter=' ', usecols=(1,0),unpack=True)
# Compute the area using the composite trapezoidal rule.
area = trapz(y, x)
area = abs(area)
print("area =", area)



f='A1N1nonAttackerWitness0.05attackerWitness0.40'
x,y=np.loadtxt(f, delimiter=' ', usecols=(1,0),unpack=True)
# Compute the area using the composite trapezoidal rule.
area = trapz(y, x)
area = abs(area)
print("area =", area)


f='A1N1nonAttackerWitness0.05attackerWitness0.60'
x,y=np.loadtxt(f, delimiter=' ', usecols=(1,0),unpack=True)
# Compute the area using the composite trapezoidal rule.
area = trapz(y, x)
area = abs(area)
print("area =", area)


f='A1N1nonAttackerWitness0.05attackerWitness0.80'
x,y=np.loadtxt(f, delimiter=' ', usecols=(1,0),unpack=True)
# Compute the area using the composite trapezoidal rule.
area = trapz(y, x)
area = abs(area)
print("area =", area)


f='A1N1nonAttackerWitness0.05attackerWitness1.00'
x,y=np.loadtxt(f, delimiter=' ', usecols=(1,0),unpack=True)
# Compute the area using the composite trapezoidal rule.
area = trapz(y, x)
area = abs(area)
print("area =", area)

# f='test'
# x,y=np.loadtxt(f, delimiter=' ', usecols=(1,0),unpack=True)
# # Compute the area using the composite trapezoidal rule.
# area = trapz(y, x)
# print("area =", area)