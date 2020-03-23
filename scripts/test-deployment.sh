#!/bin/bash

#annoying thing about docker:
#hard to tell when service inside it is actually up and running..
#have to just try to connect.
x=""
count=0
while [ "$x" == "" ]
do
    if [ "$count" == "20" ]
    then
        echo "Giving up after 20 attempts to connect!"
        exit 1
    fi
    x=`netcat -N -w 1 localhost 12345 < /dev/null`
    sleep 4
    let count=count+1
done

{
    ./gradlew run-client --console=plain < ./test_files/player1.txt 
} &

sleep 3

{
    ./gradlew run-client --console=plain < ./test_files/player2.txt 
} &
wait

