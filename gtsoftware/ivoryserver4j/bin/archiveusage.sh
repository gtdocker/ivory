#!/bin/bash
# Given a directory with Usage Based Monitoring files (.ivl) 
# this batch file will zip up all the files within the directory. 
# The archive file will then be stored in a subdirectory named archive. 
# All files except the current .ivl file (file with the newest date) will be deleted, 
# leaving the current .ivl file in the directory. 
# The archive file will be given the name of the oldest .ivl file 
# denoting the start point of the archive.

if [ -z "$1" ]; then 
   usageDir="/var/ivory/usage" 
else
   usageDir=$1
fi

if [ ! -d "$usageDir" ];then
    echo Directory "$usageDir" does not exist, exiting
    exit
fi

ivlFilesCount=$(ls "$usageDir"/*ivl 2> /dev/null | wc -l)
if [ "$ivlFilesCount" == "0" ];then
    echo Directory "$usageDir" does not contain .ivl files, exiting
    exit
fi

if [ ! -d "$usageDir"/archive ];then
    echo Directory "$usageDir"/archive does not exist,creating
    mkdir "$usageDir"/archive
fi

currDir=$(pwd)
cd "$usageDir"

oldest=`ls -t *.ivl | tail -n 1`
newest=`ls *.ivl | tail -n 1`

tar -czf archive/"$oldest".tar.gz *.ivl

cp $newest $newest.tmp
rm -f *.ivl 
mv $newest.tmp $newest 
cd "$currDir"

echo "Archiving of Ivory Server Usage Monitoring files has been completed."
echo "Archive file "$usageDir"/archive/"$oldest".tar.gz created."

