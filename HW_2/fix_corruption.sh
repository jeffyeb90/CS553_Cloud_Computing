#!/bin/bash

#fix the corruption error, input the number of slaves need to repair

count=0
 
#store an array containing lines in public_ips.txt
while read line
do
        public_array[$count]=$line
        let count=count+1
done < public_ips.txt

echo ${public_array[*]}

#echo ${public_array[0]}

for ((i=0; i <= $1; i++))
do
	ssh -i ~/.ssh/553_hw2_keyPair.pem ubuntu@${public_array[$i]} "rm -Rf /tmp/hadoop-ubuntu/*"
done


