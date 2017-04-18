
#import csv
from csv
from sys import stdin, stdout
import sys


csv.field_size_limit(sys.maxint)



fi = "xx"
num = 1
ebooknum = None

csvFile = open('ebook.csv', 'wb')

bookWriter = csv.writer(csvFile, delimeter=',')

while True:
	try:
		book = open(fi + str(num), 'rb')
		num += 1
		hder = ""
		order = 0
		#print "book#: " + str(num)
	except IOError:
		break;

	for lineNum in range(12):
		line = book.readline()[:-2]
		#line = line.rstrip()
		if order == 0 and 'Title: ' in line:
			hder += line[7:] + ","
			order = 1

		elif order == 1 and 'Author: ' in line:
			order = 2
			hder += line[8:] + ","

		elif order == 2 and 'Release Date: ' in line:

			date = ''
			index = 14
			while(line[index] != '['):
				date += line[index]
				index += 1
			date = date.rstrip()
			hder += date + ","

			index += 8
			idNum = ''
			while(line[index] != ']'):
				idNum += line[index]
				index += 1
			hder += idNum + ","
			ebooknum = idNum


			order = 3

			
		elif order == 3 and 'Language: ' in line:

			hder += line[9:] + ","
			#print hder
			order = 4

		elif order == 4:
			#count = 0
			while not "*** START OF THE PROJECT" in line:
				line = book.readline()

			line = book.readline()
			#print hder + "".join(book.readlines()) + ","
			csvFile.write(hder + "".join(book.readlines()) + ",")
			break;

csvFile.close()
#stdout.flush()
#stdout.close()
