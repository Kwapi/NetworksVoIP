file = fopen('Lost packets - simple - datagramSocket2.txt','r');
formatSpec ='%d';

A = fscanf(file,formatSpec);


hist(A, unique(A));

avg = mean(A);
standardDeviation = std(A);
