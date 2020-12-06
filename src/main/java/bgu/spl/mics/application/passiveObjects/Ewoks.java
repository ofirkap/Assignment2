package bgu.spl.mics.application.passiveObjects;


/**
 * Passive object representing the resource manager.
 * <p>
 * This class must be implemented as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class Ewoks {

    private static Ewoks instance = null;

    private final Ewok[] ewoksVillage;

    private Ewoks(int numOfEwoks) {
        this.ewoksVillage = new Ewok[numOfEwoks];
        for (int i = 0; i < numOfEwoks; i++) {
            ewoksVillage[i] =  new Ewok(i+1);
        }
    }

    //The Ewoks class is a Singleton
    public static Ewoks getInstance(int numOfEwoks) {
        if (instance == null)
            synchronized (Ewoks.class) {
                if (instance == null)
                    instance = new Ewoks(numOfEwoks);
            }
        return instance;
    }

    //synchronized limits access to any specific ewok to only 1 thread at a time
    public boolean acquireEwok(int serialNumber) {
        if (ewoksVillage[serialNumber].isAvailable()) {
            synchronized (ewoksVillage[serialNumber]) {
                ewoksVillage[serialNumber].acquire();
                return true;
            }
        }
        return false;
    }

    public synchronized void releaseEwok(int serialNumber){
        ewoksVillage[serialNumber].release();
    }
}
