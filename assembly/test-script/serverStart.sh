#!/bin/sh

#set JAVA_OPTS
JAVA_OPTS="-server -Xms1024m -Xmx1024m -Xmn256m -Xss256k"

#set HOME
CURR_DIR=`pwd`
cd `dirname "$0"`/..
NET_HOME=`pwd`
cd $CURR_DIR
if [ -z "$NET_HOME" ] ; then
    echo
    echo "Error: NET_HOME environment variable is not defined correctly."
    echo
    exit 1
fi

#set CLASSPATH
NET_CLASSPATH="$NET_HOME/lib/classes"
for i in "$NET_HOME"/lib/*.jar
do
    NET_CLASSPATH="$NET_CLASSPATH:$i"
done

#startup Server
RUN_CMD="\"$JAVA_HOME/bin/java\""
RUN_CMD="$RUN_CMD -classpath \"$NET_CLASSPATH\""
RUN_CMD="$RUN_CMD $JAVA_OPTS"
RUN_CMD="$RUN_CMD com.nyankosama.nio.net.Startup $@"
echo $RUN_CMD
eval $RUN_CMD
#==============================================================================