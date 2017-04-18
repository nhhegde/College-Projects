# Neil Hegde (c) 2012
from scipy import *
import random


TreeDepth = 1

class TreeNode:
	setOfFeatures = None
	featureSplit = None
	threshold = None
	left = None
	right = None


def BuildTree(subsetOfFeatures, subsetOfLabels):
	for i in range(len(subsetOfFeatures)):
		feature = feature.sort()
		for value in feature:




def BuildRandomForest(setOfFeatures, labels, numTrees, subsampleFraction=0.7):
	forest = []
	for i in range(numTrees):
		subsetFeatures = []
		subsetLabels = []
		n = len(setOfFeatures) * subsampleFraction
		for i in range(n):
			rando = random.randint(0, n)
			subsetFeatures.append(setOfFeatures[rando])
			subsetLabels.append(labels[rando])
			forest.append(buildTree(subsetFeatures, subsetLabels))
	return forest
