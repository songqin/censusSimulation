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
print("area 0.00=", area)



f='attackerWitness0.05'
x,y=np.loadtxt(f, delimiter=' ', usecols=(1,0),unpack=True)
# Compute the area using the composite trapezoidal rule.
area = trapz(y, x)
area = abs(area)
print("area 0.05=", area)


f='attackerWitness0.10'
x,y=np.loadtxt(f, delimiter=' ', usecols=(1,0),unpack=True)
# Compute the area using the composite trapezoidal rule.
area = trapz(y, x)
area = abs(area)
print("area 0.10=", area)


f='attackerWitness0.20'
x,y=np.loadtxt(f, delimiter=' ', usecols=(1,0),unpack=True)
# Compute the area using the composite trapezoidal rule.
area = trapz(y, x)
area = abs(area)
print("area 0.2=", area)

f='attackerWitness0.30'
x,y=np.loadtxt(f, delimiter=' ', usecols=(1,0),unpack=True)
# Compute the area using the composite trapezoidal rule.
area = trapz(y, x)
area = abs(area)
print("area 0.3=", area)

f='attackerWitness0.40'
x,y=np.loadtxt(f, delimiter=' ', usecols=(1,0),unpack=True)
# Compute the area using the composite trapezoidal rule.
area = trapz(y, x)
area = abs(area)
print("area 0.4=", area)

f='attackerWitness0.50'
x,y=np.loadtxt(f, delimiter=' ', usecols=(1,0),unpack=True)
# Compute the area using the composite trapezoidal rule.
area = trapz(y, x)
area = abs(area)
print("area0.5 =", area)

f='attackerWitness0.60'
x,y=np.loadtxt(f, delimiter=' ', usecols=(1,0),unpack=True)
# Compute the area using the composite trapezoidal rule.
area = trapz(y, x)
area = abs(area)
print("area0.6=", area)

f='attackerWitness0.70'
x,y=np.loadtxt(f, delimiter=' ', usecols=(1,0),unpack=True)
# Compute the area using the composite trapezoidal rule.
area = trapz(y, x)
area = abs(area)
print("area 0.7=", area)

f='attackerWitness0.80'
x,y=np.loadtxt(f, delimiter=' ', usecols=(1,0),unpack=True)
# Compute the area using the composite trapezoidal rule.
area = trapz(y, x)
area = abs(area)
print("area 0.8=", area)

f='attackerWitness0.90'
x,y=np.loadtxt(f, delimiter=' ', usecols=(1,0),unpack=True)
# Compute the area using the composite trapezoidal rule.
area = trapz(y, x)
area = abs(area)
print("area 0.9=", area)

f='attackerWitness1.00'
x,y=np.loadtxt(f, delimiter=' ', usecols=(1,0),unpack=True)
# Compute the area using the composite trapezoidal rule.
area = trapz(y, x)
area = abs(area)
print("area 1.0=", area)
