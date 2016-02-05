
package networksvoip;

public class NetworksVoIP {

   public static void main(String[] args) {
       AudioReceiver2 audioReceiver = new AudioReceiver2();
       AudioSender2 audioSender = new AudioSender2();
       
       audioReceiver.start();
       audioSender.start();
    }
    
}
