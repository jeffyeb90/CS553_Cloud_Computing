#!/bin/bash

# This script does the following task
#	1. create hosts and config (read from the file private_ips.txt)
#	2. copy the 2 files above into the master and slaves "~/.ssh/" (read from the file public_ips.txt)
#	3. log into each node, move the hosts file from "~./ssh/" to "/etc/"
#	4. log into each slave node, modify the hostname's content to corresponding names

# In order to configure, must provide private_ips.txt and public_ips.txt
# private_ips.txt contains ALL slave's private ip, but NO master's ip
# public_ips.txt contians ALL slave's public ip, also master's ip. Master's must be the first one

#---------------------------------------function definitions------------------------------
#function to write to the config file

writeConfig()
{
         echo "Host slave$1" >> config
         echo "  HostName $2" >> config
         echo "  User ubuntu" >> config
         echo "  IdentityFile ~/.ssh/553_hw2_keyPair.pem" >> config
	 echo "" >> config
}


#function to copy "hosts" and "config" to a remote server

copyFile()
{
	scp -i ~/.ssh/553_hw2_keyPair.pem ./config ubuntu@$1:~/.ssh/
	scp -i ~/.ssh/553_hw2_keyPair.pem ./hosts ubuntu@$1:~/.ssh/
	echo "done copying to $1"
}

#function to move hosts on a remote server

moveHosts()
{
	ssh -i ~/.ssh/553_hw2_keyPair.pem ubuntu@$1 "sudo mv ~/.ssh/hosts /etc/hosts"
}

#function to update hostname on a remote server

updateHostname()
{
	printf -v _ %q "$2"
	ssh -i ~/.ssh/553_hw2_keyPair.pem ubuntu@$1 "echo "slave$_" | sudo tee /etc/hostname"
}



#------------------step 1 --------------------------------

count=0

#store an array containing lines in private_ips.txt
while read line
do
	private_array[$count]=$line
	let count=count+1
done < private_ips.txt

echo "the value of count is now $count"
echo "the value of private_array is:"
echo ${private_array[*]}

#----write to hosts--------------------
echo "172.31.44.21 master" > hosts

# i starts from 1
for i in $(seq "${count-1}")
do
	echo "${private_array[$((i-1))]} slave$i" >> hosts
done

#-----write to config------------------
echo "Host *" > config
echo "	StrictHostKeyChecking no" >> config

echo "Host master" >> config
echo "	HostName 172.31.44.21" >> config
echo "	User ubuntu" >> config
echo "	IdentityFile ~/.ssh/553_hw2_keyPair.pem" >> config

for i in $(seq "${count-1}")
do
	writeConfig $i ${private_array[$((i-1))]} 
done

#-----------------------------step 2 -----------------------------

count=0
 
#store an array containing lines in public_ips.txt
while read line
do
        public_array[$count]=$line
        let count=count+1
done < public_ips.txt

echo "the value of count is now $count"
echo "the value of public_array is:"
echo ${public_array[*]}

for i in $(seq "${count-1}")
do
	copyFile ${public_array[$((i-1))]} 
done

echo "Done copying files"

#--------------------------------step 3 ---------------------------

for i in $(seq "${count-1}")
do
	moveHosts ${public_array[$((i-1))]}
	echo "done moving hosts on machine ${public_array[$((i-1))]}"
done

#----------------------------------step 4 -------------------------
for ((i=1; i < $((count)); i++))
do
	updateHostname ${public_array[$i]} $i
	echo "done update hostname on machine ${public_array[$i]}" to "slave$i"
done



