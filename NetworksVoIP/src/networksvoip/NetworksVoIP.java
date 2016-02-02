
package networksvoip;

public class NetworksVoIP {

   public static void main(String[] args) {
       AudioReceiver audioReceiver = new AudioReceiver();
       AudioSender audioSender = new AudioSender();
       
       audioReceiver.start();
       audioSender.start();
    }
    
}
