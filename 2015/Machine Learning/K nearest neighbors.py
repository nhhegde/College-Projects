# Neil Hegde (c) 2012
from scipy import *
import csv as csv

from scipy import spatial as so

trainFeatures = csv.reader(open('trainFeatures.csv'))
trainLabels = csv.reader(open('trainLabels.csv'))

trainingSet = []

for label in trainLabels:
	observedImage = array(trainFeatures.next(), dtype=float)
	trainingSet.append((observedImage, label))




def Knn1(x):
	retLabel = '1'
	retDist = 99999999999
	for observation in trainingSet:
		d = so.distance.euclidean(observation[0], x)
		if (d < retDist):
			retLabel = observation[1]
			retDist = d
	out1.write(retLabel[0] + '\n')
	return retLabel



def Knn2(x):
	retLabel1 = '1'
	retDist1 = 99999999999
	retLabel2 = '1'
	retDist2 = 99999999999
	for observation in trainingSet:
		d = so.distance.euclidean(observation[0], x)
		if (d < retDist1):
			retLabel2 = retLabel1
			retDist2 = retDist1

			retLabel1 = observation[1]
			retDist1 = d


		elif (d < retDist2):
			retLabel2 = observation[1]
			retDist2 = d

	retLabel = retLabel1[0]
	out2.write(retLabel[0] + '\n')
	return retLabel

def Knn(x, k):



	retList = []
	for i in range(k):
		retList.append(('1', 9999999999))
	for observation in trainingSet:
		d = so.distance.euclidean(observation[0], x)

		for i in range(k):
			if (d < retList[i][1]):
				partial = retList[:i]
				partial.append((observation[1], d))
				retList = partial + retList[i+1:k]
				break;
	retVect = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
	for neighbor in retList:
		retVect[int(neighbor[0][0])] += 1
	retVect.sort()
	retVect.reverse()
	retLabel = retVect[0] 
	outDict[k].write(str(retLabel) + '\n')
	return retLabel


out1 = open('digitsOutput1.csv', "w") 
"""
out2 = open('digitsOutput2.csv', "w") 

out5 = open('digitsOutput5.csv', "w") 
out10 = open('digitsOutput10.csv', "w") 
out25 = open('digitsOutput25.csv', "w") 

outDict = {}
outDict[5] = out5
outDict[10] = out10
outDict[25] = out25
"""

for feature in csv.reader(open('valFeatures.csv')):
	feature = array(feature, dtype=float)
	Knn1(feature)	
	#Knn2(feature)
	#Knn(feature, 5)
	#Knn(feature, 10)
	#Knn(feature, 25)

out1.close()

"""
out2.close()
out5.close()
out10.close()
out25.close()
"""