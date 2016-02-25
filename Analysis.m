clear

%%  OPEN AND SCAN FILE
file = fopen('Testing\DatagramSocket4\receivedPackets.txt','r');
formatSpec ='%d';
receivedPackets = fscanf(file,formatSpec);


expectedOrder = 1:1000;
expectedOrder = expectedOrder';

%% SORT THE PACKETS RECEIVED
sortedReceived = sort(receivedPackets);

%% CALCULATE DIFFERENCE BETWEEN PACKET INDICES - AMOUNT OF PACKETS LOST + 1 PER BURST
differencePacketsLost = diff(sortedReceived);



%% ADD ZERO AT THE BEGINNING TO OFFSET ARRAY AND COMBINE WITH ORIGINAL INDICES
differencePacketsLostOffset = differencePacketsLost;
differencePacketsLostOffset(2:end+1)=differencePacketsLost;
differencePacketsLostOffset(1)=0; 




combinedPacketsLostAnalysis = horzcat(sortedReceived, differencePacketsLostOffset);

%% COMBINE ARRAYS
combinedFiltered = combinedPacketsLostAnalysis(combinedPacketsLostAnalysis(:,2) > 1,:); 

%%  SAVE RAW RESULTS
fid = fopen('indexStartLosing_AmountLost.txt', 'wt'); % Open for writing
 for i=1:size(combinedFiltered,1)
    fprintf(fid, '%d ', combinedFiltered(i,:));
    fprintf(fid, '\n');
 end
 fclose(fid);
 
 
 
 %% PERFORM ANALYSIS
 
 % PACKET LOSS
 PL_spread_mean = mean(diff(combinedFiltered(:,1)));
 PL_spread_max = max(diff(combinedFiltered(:,1)));
 PL_spread_min = min(diff(combinedFiltered(:,1)));
 PL_spread_med = median(diff(combinedFiltered(:,1)));
 PL_spread_mode = mode(diff(combinedFiltered(:,1)));
 
 PL_length_mean = mean(combinedFiltered(:,2)-1);
 PL_length_max = max(combinedFiltered(:,2)-1);
 PL_length_med = median(combinedFiltered(:,2)-1);
 PL_length_mode = mode(combinedFiltered(:,2)-1);
 
 
 % ORDER MISMATCH
 combinedOrderMismatchAnalysis = horzcat(sortedReceived,receivedPackets);
 combinedOrderMismatchAnalysis(:,3) = abs(combinedOrderMismatchAnalysis(:,1)-combinedOrderMismatchAnalysis(:,2));
 
 % get rid of zero mismatch (filter data)
 orderMismatchFiltered = nonzeros(combinedOrderMismatchAnalysis(:,3));
 OM_count = length(orderMismatchFiltered);
 
 OM_length_min = min(orderMismatchFiltered);
 OM_length_max = max(orderMismatchFiltered);
 OM_length_med = median(orderMismatchFiltered);
 OM_length_mean = mean(orderMismatchFiltered);
 OM_length_mode = mode(orderMismatchFiltered);
 
 
 mismatchSpreadCounter  = 0;
 mismatchSpreadArray = [];
 for i=1:length(combinedOrderMismatchAnalysis)
     if(combinedOrderMismatchAnalysis(i,3) == 0)
         mismatchSpreadCounter = mismatchSpreadCounter + 1;
     else
         mismatchSpreadArray(end+1) = mismatchSpreadCounter; 
         mismatchSpreadCounter = 0;
     end
     
 end
 
mismatchSpreadArray = mismatchSpreadArray';
 
OM_spread_min = min(mismatchSpreadArray);
OM_spread_max = max(mismatchSpreadArray);
OM_spread_mean = mean(mismatchSpreadArray);
OM_spread_mode = mode(mismatchSpreadArray);
OM_spread_med = median(mismatchSpreadArray);


 