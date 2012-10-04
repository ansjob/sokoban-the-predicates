#!/bin/bash
for i in {1..100}
do
	./timeout3 -t 60 -i 1 java Client dd2380.csc.kth.se 5032 $i
done
