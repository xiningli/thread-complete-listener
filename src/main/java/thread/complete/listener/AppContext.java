package thread.complete.listener;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.atomic.AtomicInteger;



class Sale extends Observable implements ThreadCompleteListener{
    private static final int minFundPerPerson= 100;
    private static final int minFundToGo = 500;
    private static final int maxSeats= 5;
    private int fund = (0);
    private boolean hasTruck = true;
    public Sale(){
    }
    

    public void notifyOfThreadComplete(final NotifyingThread truck){
        System.out.println("bus arrived!");
        this.clearChanged();
        this.deleteObservers();
        fund = 0;
        hasTruck = true;
    }
    public synchronized void addClient(Client client) throws InterruptedException {
        if (hasTruck==false) {
            System.out.println("no truck");
            return; 
        }

        if (client.claimedPrice<minFundPerPerson) {
            System.out.println("too poor");
            return; 
        }

        fund+=client.claimedPrice;
        System.out.println("Adding observer with price: " + client.claimedPrice);
        this.addObserver(client);    

        if (this.countObservers()>=maxSeats || fund>=minFundToGo) {
            System.out.println("Notifying the clients that we can go");
            this.setChanged();
            this.notifyObservers();
            Thread.sleep(3000);//synchronized blocking
            EscortTruck dispatchEscortTruck = new EscortTruck();
            System.out.println("Engine start, sale man is finishing: " + dispatchEscortTruck.hashCode());
            dispatchEscortTruck.addListener(this);
            dispatchEscortTruck.start();
            hasTruck = false;
        }
    }
}

class Client implements Observer {
    public final int claimedPrice; 
    public Client(int claimedPrice) {
        this.claimedPrice = claimedPrice;
    }
    public void update(Observable sale, Object obj) {
        System.out.println(claimedPrice + " can now go!");
    }
}

class EscortTruck extends NotifyingThread{
    public EscortTruck () {
    }
    @Override 
    public void doRun() {
        System.out.println("preparing to dispatch the escort truck");

        try {
            Thread.sleep(3000L);
        }
        catch (InterruptedException ie){
            ie.printStackTrace();
        }
        System.out.println("dispatched the escort truck");
        try {
            Thread.sleep(15000L);
        }
        catch (InterruptedException ie){
            ie.printStackTrace();
        }
        System.out.println("escort truck is back");
        // sale.notify();
    }
}
public class AppContext implements ThreadCompleteListener{ // this is demoing the escort car dispatch team


    private Sale sale = new Sale();

    /*
    the context is that, dispatchEscortTruck will be dispatch the  after wait for a while
    potential clients raised enough fund. 
    */

    
    // public final void init() throws Exception { // depatching 
    //     dispatchEscortTruck.addListener(this);
    // }


    public void registerClientNotification(Client c) throws InterruptedException {
        sale.addClient(c);
    }

    /*
    listeners are actually classes that are called after some task completion. 
    */
    public synchronized void notifyOfThreadComplete(final NotifyingThread thread) {

        System.out.println("notifying the app context that truck is back");
    }


}