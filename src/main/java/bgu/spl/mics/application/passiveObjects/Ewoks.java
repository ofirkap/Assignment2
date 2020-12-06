package bgu.spl.mics.application.passiveObjects;


import bgu.spl.mics.*;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

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

    public void setEwoksVillage(int villageSize){
        this.ewoksVillage = new Ewok[villageSize];
        for (int i = 0; i < villageSize; i++) {
            ewoksVillage[i] = new Ewok(i + 1);
        }
    }
    //synchronized limits access to any specific ewok to only 1 thread at a time
    public boolean acquireEwok(int serialNumber) {
        if (ewoksVillage[serialNumber - 1].isAvailable()) {
            synchronized (ewoksVillage[serialNumber - 1]) {
                ewoksVillage[serialNumber - 1].acquire();
                return true;
            }
        }
        return false;
    }

    public synchronized void releaseEwok(int serialNumber) {
            ewoksVillage[serialNumber - 1].release();
            notifyAll();
    }
}
