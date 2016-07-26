from __future__ import print_function

import numpy as np
from scipy.integrate import simps
from numpy import trapz


f='A1N1nonAttackerWitness0.05attackerWitness0.60'
x,y=np.loadtxt(f, delimiter=' ', usecols=(1,0),unpack=True)
# Compute the area using the composite trapezoidal rule.
print(x)
area = trapz(y, x)
area = abs(area)
print("area =", area)
print("x.max", x.max())
print("x max pos", np.argmax(x))
print("y.max", y.max())
print("x.min", x.min())
print("y.min", y.min())
# Compute the area using the composite Simpson's rule.
# area = simps(y, x)
# print("area =", area)


# f='A1N1nonAttackerWitness0.05attackerWitness0.05'
# x,y=np.loadtxt(f, delimiter=' ', usecols=(1,0),unpack=True)
# # Compute the area using the composite trapezoidal rule.
# area = trapz(y, x)
# area = abs(area)
# print("area =", area)
# # area = simps(y, x)
# # print("area =", area)


# f='A1N1nonAttackerWitness0.05attackerWitness0.10'
# x,y=np.loadtxt(f, delimiter=' ', usecols=(1,0),unpack=True)
# # Compute the area using the composite trapezoidal rule.
# area = trapz(y, x)
# area = abs(area)
# print("area =", area)



# f='A1N1nonAttackerWitness0.05attackerWitness0.15'
# x,y=np.loadtxt(f, delimiter=' ', usecols=(1,0),unpack=True)
# # Compute the area using the composite trapezoidal rule.
# area = trapz(y, x)
# area = abs(area)
# print("area =", area)


# f='A1N1nonAttackerWitness0.05attackerWitness0.20'
# x,y=np.loadtxt(f, delimiter=' ', usecols=(1,0),unpack=True)
# # Compute the area using the composite trapezoidal rule.
# area = trapz(y, x)
# area = abs(area)
# print("area =", area)

# f='A1N1nonAttackerWitness0.05attackerWitness0.25'
# x,y=np.loadtxt(f, delimiter=' ', usecols=(1,0),unpack=True)
# # Compute the area using the composite trapezoidal rule.
# area = trapz(y, x)
# area = abs(area)
# print("area =", area)

# f='A1N1nonAttackerWitness0.05attackerWitness0.30'
# x,y=np.loadtxt(f, delimiter=' ', usecols=(1,0),unpack=True)
# # Compute the area using the composite trapezoidal rule.
# area = trapz(y, x)
# area = abs(area)
# print("area =", area)

# f='A1N1nonAttackerWitness0.05attackerWitness0.35'
# x,y=np.loadtxt(f, delimiter=' ', usecols=(1,0),unpack=True)
# # Compute the area using the composite trapezoidal rule.
# area = trapz(y, x)
# area = abs(area)
# print("area =", area)

# f='A1N1nonAttackerWitness0.05attackerWitness0.40'
# x,y=np.loadtxt(f, delimiter=' ', usecols=(1,0),unpack=True)
# # Compute the area using the composite trapezoidal rule.
# area = trapz(y, x)
# area = abs(area)
# print("area =", area)

# f='A1N1nonAttackerWitness0.05attackerWitness0.45'
# x,y=np.loadtxt(f, delimiter=' ', usecols=(1,0),unpack=True)
# # Compute the area using the composite trapezoidal rule.
# area = trapz(y, x)
# area = abs(area)
# print("area =", area)

# f='A1N1nonAttackerWitness0.05attackerWitness0.50'
# x,y=np.loadtxt(f, delimiter=' ', usecols=(1,0),unpack=True)
# # Compute the area using the composite trapezoidal rule.
# area = trapz(y, x)
# area = abs(area)
# print("area =", area)

# f='A1N1nonAttackerWitness0.05attackerWitness0.55'
# x,y=np.loadtxt(f, delimiter=' ', usecols=(1,0),unpack=True)
# # Compute the area using the composite trapezoidal rule.
# area = trapz(y, x)
# area = abs(area)
# print("area =", area)

# f='A1N1nonAttackerWitness0.05attackerWitness0.60'
# x,y=np.loadtxt(f, delimiter=' ', usecols=(1,0),unpack=True)
# # Compute the area using the composite trapezoidal rule.
# area = trapz(y, x)
# area = abs(area)
# print("area =", area)

# f='A1N1nonAttackerWitness0.05attackerWitness0.65'
# x,y=np.loadtxt(f, delimiter=' ', usecols=(1,0),unpack=True)
# # Compute the area using the composite trapezoidal rule.
# area = trapz(y, x)
# area = abs(area)
# print("area =", area)

# f='A1N1nonAttackerWitness0.05attackerWitness0.70'
# x,y=np.loadtxt(f, delimiter=' ', usecols=(1,0),unpack=True)
# # Compute the area using the composite trapezoidal rule.
# area = trapz(y, x)
# area = abs(area)
# print("area =", area)

# f='A1N1nonAttackerWitness0.05attackerWitness0.75'
# x,y=np.loadtxt(f, delimiter=' ', usecols=(1,0),unpack=True)
# # Compute the area using the composite trapezoidal rule.
# area = trapz(y, x)
# area = abs(area)
# print("area =", area)

# f='A1N1nonAttackerWitness0.05attackerWitness0.80'
# x,y=np.loadtxt(f, delimiter=' ', usecols=(1,0),unpack=True)
# # Compute the area using the composite trapezoidal rule.
# area = trapz(y, x)
# area = abs(area)
# print("area =", area)
# f='A1N1nonAttackerWitness0.05attackerWitness0.85'
# x,y=np.loadtxt(f, delimiter=' ', usecols=(1,0),unpack=True)
# # Compute the area using the composite trapezoidal rule.
# area = trapz(y, x)
# area = abs(area)
# print("area =", area)
# f='A1N1nonAttackerWitness0.05attackerWitness0.90'
# x,y=np.loadtxt(f, delimiter=' ', usecols=(1,0),unpack=True)
# # Compute the area using the composite trapezoidal rule.
# area = trapz(y, x)
# area = abs(area)
# print("area =", area)


# f='A1N1nonAttackerWitness0.05attackerWitness0.95'
# x,y=np.loadtxt(f, delimiter=' ', usecols=(1,0),unpack=True)
# # Compute the area using the composite trapezoidal rule.
# area = trapz(y, x)
# area = abs(area)
# print("area =", area)

# f='A1N1nonAttackerWitness0.05attackerWitness1.00'
# x,y=np.loadtxt(f, delimiter=' ', usecols=(1,0),unpack=True)
# # Compute the area using the composite trapezoidal rule.
# area = trapz(y, x)
# area = abs(area)
# print("area =", area)

