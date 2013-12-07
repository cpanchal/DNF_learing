#!/bin/sh
#'''
#use
#<thisscript> $1 $2 $3 $4
#$1: positive dataset data (filename)
#$2: negative dataset data (filename)
#$3: number of folds (int)
#$4: ROC weight (0-1)
#
#'''


java -Xmx3000m -Xms3000m -cp Algorithm-1.0.1.jar:CompBio.Java-1.1.jar:MachineLearning-1.0.1.jar:JavaDNF.Java-1.2.jar edu.cmu.cs.JavaDNF.performance.CrossValidationEvaluation $1 $2 $3 $4
