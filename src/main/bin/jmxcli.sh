#!/bin/sh

CLASSPATH=lib/

for JAR in `cd "lib/" && ls -1 *.jar` 
do
	CLASSPATH="$CLASSPATH:lib/$JAR"
done

java -cp $CLASSPATH org.github.jmxcli.CLI $*
