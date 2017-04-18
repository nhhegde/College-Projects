from sys import stdin, stdout


book = ""

line = stdin.readline()
while(line != ""):
	book += line
	line = stdin.readline()
	if("Title:" in line):
		print book
		book = ""
print book