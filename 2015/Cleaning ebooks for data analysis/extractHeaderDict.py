
# Neil Hegde (c) 2012
import csv
from sys import stdin, stdout
import sys
import fileinput


csv.field_size_limit(sys.maxint)

ebooknum = None

csvFile = open('ebook.csv', 'ab')

bookWriter = csv.DictWriter(csvFile, fieldnames=['title','author','release_date','ebook_id','language','body'], restval='null')#, lineterminator='\r\n')


for line in fileinput.input():
	book = open(line.rstrip('\n'), 'r')

	headerDict = {}
	lastFile = False
	count = 0
	while True:
		line = book.readline().rstrip('\r\n')
		#line = line.rstrip()
		if 'Title: ' in line:
			headerDict['title'] = line.split('Title: ', 1)[1]

		if 'Author: ' in line:
			headerDict['author'] = line.split('Author: ', 1)[1].lstrip()
		if 'Release Date: ' in line:
			headerDict['release_date'] = line.split('Release Date: ', 1)[1].split('[')[0].rstrip()
			headerDict['ebook_id'] = line.split('#', 1)[1][:-1]
		if 'Language: ' in line:
			headerDict['language'] = line.split('Language: ', 1)[1]

		if "*** START OF THE PROJECT" in line:	
			headerDict['body'] = "".join(book.readlines())
			break;
		count += 1
		if count == 1000:
<<<<<<< HEAD
	#			print 'Taking too long to find the body'
=======
			print 'Taking too long to find the body'
>>>>>>> parent of 29642bd... Before Unix Rabbit Hole
			lastFile = True
			break;
	#		if lineNum == 999:
	#			print "book#: " + headerDict['ebook_id']
	#			print "book file: " + fi + str(num - 1)
	#			raise IOError("Never found book's body")
	if not lastFile:
		bookWriter.writerow(headerDict)
		print headerDict['body']

	

csvFile.close()

