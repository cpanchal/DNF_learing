#!/bin/sh
DATA=$1
#for j in 0.1 0.3 0.4 0.45 0.5 0.55 0.6 0.7 0.8 1.0
#for j in 0.05 0.1 0.2 0.3 0.4 0.45 0.5 0.55 0.6 0.7 0.8 1.0
#for j in 0.1 0.2 0.3 0.4 0.5 0.6 0.7 0.8 0.9 1.0
for j in 0.05 0.15 0.2 0.25 0.1 0.3 0.4 0.45 0.5 0.55 0.6 0.7 0.8 0.9 1.0

do
    for ((i = 1; i <= 1; i++ ))
    do
        time ./cmd.DNF.CrossValidation.sh Data/$DATA\.pos Data/$DATA\.neg 10 $j > ./results/$DATA\.greedy.weight.crossvalidation.10.$i.$j
    done
done




