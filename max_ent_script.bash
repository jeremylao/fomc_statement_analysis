#!/bin/bash 

javac -cp .:/mnt/c/computer_science/NLP/assignment_five/opennlp/output/* PROJECT_CHUNKER.java
javac -cp .:/mnt/c/computer_science/NLP/assignment_five/opennlp/output/* MEtag_original.java
javac -cp .:/mnt/c/computer_science/NLP/assignment_five/opennlp/output/* MEtrain.java
java PROJECT_CHUNKER training_corpus.pos-tag me_train_input.txt

year=2006
month=1
filename=""

for i in 2006 2007 2008 2009 2010 2011 2012 2013 2014 2015 2016 2017

do
    for j in 1 2 3 4 5 6 7 8 9 10 11 12
    do
	if [ $j -lt 10 ] 
	then
	    filename=$i'_0'$j'.stmt-pos'
	else
	    filename=$i'_'$j'.stmt-pos'
	fi
    
	if [ -f $filename ]
	then
	    java PROJECT_CHUNKER $filename test_file_feature_vectors.txt
		java -classpath .:/mnt/c/computer_science/NLP/assignment_five/opennlp/output/* MEtrain me_train_input.txt metrain_output.txt
		java -cp .:/mnt/c/computer_science/NLP/assignment_five/opennlp/output/* MEtag_original test_file_feature_vectors.txt metrain_output.txt $filename'-response'
		echo $filename"-response executed"
	else
	   echo $filename" not found" #don't do anything  java HW4_V7 WSJ_02-21.pos 2006_01.stmt > 2006-01.stmt-pos
	fi
    done

done