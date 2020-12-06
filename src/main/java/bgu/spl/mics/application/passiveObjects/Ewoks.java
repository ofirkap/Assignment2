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
    
    private static class SingletonHolder {
        private static final Ewoks instance = new Ewoks();
    }

    private Ewok[] ewoksVillage;

    //The Ewoks class is a Singleton
    public static Ewoks getInstance() {
        return Ewoks.SingletonHolder.instance;
    }

    /**
     * This method is used to initialize the field
     * {@param ewoksVillage} to the specified size.
     * <p>
     * @param villageSize The size to initialize the array at
     */
    public void setEwoksVillage(int villageSize){
        this.ewoksVillage = new Ewok[villageSize];
        for (int i = 0; i < villageSize; i++) {
            ewoksVillage[i] = new Ewok(i + 1);
        }
    }

    /**
     * Using this method, a thread can acquire the
     * desired ewok from the ewoks collection.
     * This method is blocking meaning that if the requested
     * ewok isn't available the thread will wait until its release.
     * <p>
     * @param serialNumber The requested ewok
     */
    public synchronized void acquireEwok(int serialNumber) {
        while (!ewoksVillage[serialNumber - 1].isAvailable())
            try {
                wait();
            }catch (InterruptedException ignored){}
        ewoksVillage[serialNumber - 1].acquire();
    }

    /**
     * Using this method, a thread can release the
     * specified ewok after finishing using it.
     * <p>
     * @param serialNumber The ewok to be released
     */
    public synchronized void releaseEwok(int serialNumber) {
            ewoksVillage[serialNumber - 1].release();
            notifyAll();
    }
}
