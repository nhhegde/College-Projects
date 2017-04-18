## Neil Hegde (c) 2012

import csv as csv

from builddt import *
from random import random
from random import shuffle

from scipy import *

from scipy import spatial as so


reader = csv.reader
writer = csv.writer

trainFeatures = reader(open('trainFeatures.csv'))
trainLabels = reader(open('trainLabels.csv'))

trainingSet = []

for label in trainLabels:
	observedImage = array(trainFeatures.next(), dtype=float)
	trainingSet.append((observedImage, label))






mDict = {}

def EuclidDist(x1, x2):
	return so.distance.euclidean(x1, x2)




def NearestNeighbors(x, k):
	ret = []
	for i in range(k):
		ret.append((float('inf'), 'blank'))
	for observation in trainingSet:
		for i in range(k):
			yo = observation[0]
			d = EuclidDist(x, observation[0])
			if d < ret[i][0]:
				ret = ret[:i] + [(d, observation[1])] + ret[i:]
				ret = ret[:5]
	return ret


valFeatures = reader(open('valFeatures.csv'))
valLabels = ''

out1 = open('digitsOutput1.csv', "w") 
out2 = open('digitsOutput2.csv', "w") 
out3 = open('digitsOutput3.csv', "w") 
out4 = open('digitsOutput4.csv', "w") 
out5 = open('digitsOutput5.csv', "w") 

def Knn1():
	for valOb in valFeatures:
		valOb = array(valOb, dtype=float)
		neighbors = NearestNeighbors(valOb, 1)
		rd = {}
		retLabel = neighbors.pop()[1][0]
		out1.write(retLabel + '\n')

def Knn2():
	for valOb in valFeatures:
		valOb = array(valOb, dtype=float)
		neighbors = NearestNeighbors(valOb, 2)
		rd = {}
		for i in range(0, 10):
			rd[str(i)] = 0
		for neighbor in neighbors:
			t = neighbor[1][0]
			rd[t] += 1
		keys = rd.keys()
		retLabel = '1'
		retWeight = rd['1']
		for key in keys:
			if rd[key] > retWeight:
				retWeight = rd[key]
				retLabel = key
		out2.write(retLabel + '\n')


Knn1()

Knn2()

out1.close()
out2.close()
out3.close()
out4.close()
out5.close()


print "####\\\\Random Decision Forest Classifier////####"

def disassembleInstance(instance):
	s = ""
	for i in instance:
		s = s + i
		if s != instance[-1]:
			s = s + ","
		
	return s

def makeRDForest(instances, features, targetfeatures, numTrees = 10, fraction = 0.6):
	fold = int(len(instances) * fraction)
	# print fold
	dtrees = []
	for aTree in range(numTrees):
		#every iteration
		randomInstances = []
		randomCollection = []
		while(len(randomCollection) < fold):
			num = int(random()*len(instances))
			if num not in randomCollection:
				randomCollection.append(num)
		
		for i in randomCollection:
			randomInstances.append(copy.deepcopy(instances[i]))
			
		dtrees.append(Compute(copy.deepcopy(randomInstances), copy.deepcopy(features), copy.deepcopy(targetfeatures)))
		#print str(aTree) + "=====" + str(dtrees)
	return dtrees

def decideOnInstance(dtrees, testInstance):	
	answers = {}
	for tree in dtrees:
		#print "traversing", tree
		answer = traverse(copy.deepcopy(tree), copy.deepcopy(testInstance),1)
		#print answer
		if answer != "-1":		#To remove those instances when a particular decision tree could not find anything
			if answer in answers.keys():
				answers[answer] = answers[answer] + 1
			else:
				answers[answer] = 1

	if	len(answers.values()) != 0:
		decision = max(answers.values())
		for answer in answers.keys():
			if answers[answer] == decision:
				decision = answer
				return decision
	else:
		return "-1"



