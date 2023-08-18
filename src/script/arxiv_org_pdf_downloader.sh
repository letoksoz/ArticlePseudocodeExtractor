#gsutil -m cp -r gs://arxiv-dataset/arxiv/arxiv/pdf/0704/ /Users/Levent/Desktop/ACL_Data/

#!/bin/bash
for i in {23..07}
do
  for j in {12..1}
  do
    n=${#i}
    if [ $n -eq 1 ]
    then
      year=0$i
    else
      year=$i
    fi

    n=${#j}
    if [ $n -eq 1 ]
    then
      month=0$j
    else
      month=$j
    fi

    directory=$year$month
    echo "Welcome $directory "
    #gsutil ls gs://arxiv-dataset/arxiv/arxiv/pdf/$directory
    gsutil -m cp -r gs://arxiv-dataset/arxiv/arxiv/pdf/$directory /Volumes/T7/data

    #gsutil -m cp -r gs://arxiv-dataset/arxiv/arxiv/ps/2306/2306.17838v1.ps.gz ./data

  done
done
