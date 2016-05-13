#!/bin/bash

echo "Kompilerer programmet, vent venligst."
cd src

javac *.java

echo "Starter to instanser af programmet, vent venligst."

java DistributedTextEditor &
java DistributedTextEditor &

cd ..
cd ..
cd ..

echo "Programmerne er startet, kan de ikke ses, så vent et øjeblik."
