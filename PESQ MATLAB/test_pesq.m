id = 32;

finalFileNameInput = sprintf('input_trim%d.wav',id);
finalFileNameOutput = sprintf('output_trim%d.wav',id);

pathOriginal = '../NetworksVoIP/';
org_input = strcat(pathOriginal,'input.wav');
org_output = strcat(pathOriginal,'output.wav');

[org_input_a,fs] = wavread(org_input);
org_output_a = wavread(org_output);

path = '../Recordings/DatagramSocket3/';
mod_filenameInput = strcat(path,finalFileNameInput);
mod_filenameOutput = strcat(path,finalFileNameOutput);


wavwrite(org_input_a(1:10 * fs),mod_filenameInput);
wavwrite(org_output_a(1:10 * fs),mod_filenameOutput);


input = strcat(path,finalFileNameInput);
output = strcat(path,finalFileNameOutput);

pesq(input,output);