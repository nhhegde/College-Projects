#!/bin/bash
# Neil Hegde (c) 2012
# bash command-line arguments are accessible as $0 (the bash script), $1, etc.
# echo "Running" $0 "on" $1
# echo "Replace the contents of this file with your solution."

rm xx*
echo '' > ebook.csv
echo '' > tokens.csv
echo '' > token_counts.csv
echo '' > name_counts.csv
echo '' > tempTokens
echo '' > tempFile777

#$ sed '1,/Title: /d' $1 | csplit "Title: " | sed '/*** END OF THE PROJECT GUTENBERG EBOOK/,$d' | sed -n '/*** END OF THE PROJECT GUTENBERG EBOOK/q;p'| python program for ebook.csv | tr strip ebook id, tokens.csv | uniq token_counts.csv | for loop name_counts.csv
#sed -n '/*** END OF THE PROJECT GUTENBERG EBOOK/q;p'| \
#sed -r -n -e '/PATTERN/,${p}'
#sed -r -n -e '/PATTERN/,${p}'
#sed -n '/Title: /,$p $1 
#sed -r -n -e '/PATTERN/,${p}'
#sed -r -n -e '/Title:/,${p}' $1| 

#Delete Between
#awk 'BEGIN{p=1} /line 1/ {p=0} /line 2/ {p=1} p'
#sed '/END OF THE PROJECT/,/Title:/d'

#sed -n '/END OF THE PROJECT GUTENBERG/q;p' | \


#csplit -s --suffix-format=%d $1 '/\*\*\* START OF THE PROJECT/'-16 {*}

#awk '/Title:/{i++}i' $1 | \
#sed '/\*\*\* END OF THE PROJECT/,/Title:/{/Title:/!d}' |\
#csplit -s --suffix-format=%d - '/\*\*\* START OF THE PROJECT/'-16 {*}


#csplit -s --suffix-format=%d $1 '/\*\*\* START OF THE PROJECT/'-16 {*}
csplit -s --suffix-format=%d $1 '/\*\*\* END OF THE PROJECT/' {*}
ls -d xx* | python extractHeaderDict.py 
#cat a.txt | xargs -I % sh -c 'command1; command2; ...'
#uniq -c tempTokens > token_counts.csv




# tr, tr , tr

#tr -cs 'a-zA-Z0-9' '\n' <input | sed -e 's/^/ebookid/' >> tokens.csv


#tr -cs 'a-zA-Z0-9' '\n' < xx1 | sed -e 's/^/11111,/' >> tokens.csv
#ebook_id,token
#10001,produced
#
#| \
#tee -a ebook.csv | \
#echo 'done'

#***first use the tokenizer to break the book down into tokens, delete the characters that you don't need,
#keep the ebook id, with a delimeter between the ebookid and the rest of the tokens then use xargs and tr to write the ebook_id to every line. 
#python tokenize.py + unix utility for tokenize, but pass on the tokenized version | \  #tokenize ebook, strip ebook id tr tokens.csv, pass on just the tokense | \

#uniq | \
#python update token counts tee -a token_counts.csv | \
#python updateNameCounts.py 
#for loop name_counts.csv


exit 0