%%  OPEN AND SCAN FILE
file = fopen('Testing\DatagramSocket3\delay9.txt','r');
formatSpec ='%d';
receivedPackets = fscanf(file,formatSpec);

avgDelay = mean(receivedPackets);