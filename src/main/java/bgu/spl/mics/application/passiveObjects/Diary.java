package bgu.spl.mics.application.passiveObjects;


import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive data-object representing a Diary - in which the flow of the battle is recorded.
 * We are going to compare your recordings with the expected recordings, and make sure that your output makes sense.
 * <p>
 * Do not add to this class nothing but a single constructor, getters and setters.
 */
public class Diary {

    private static class SingletonHolder {
        private static final Diary instance = new Diary();
    }

    AtomicInteger totalAttacks;
    long HanSoloFinish;
    long C3POFinish;
    long R2D2Deactivate;
    long LeiaTerminate;
    long HanSoloTerminate;
    long C3POTerminate;
    long R2D2Terminate;
    long LandoTerminate;
    long StartTime;

    public void setStartTime(long startTime) {
        StartTime = startTime;
    }

    private Diary() {
        totalAttacks = new AtomicInteger(0);
        HanSoloFinish = 0;
        C3POFinish = 0;
        R2D2Deactivate = 0;
        LeiaTerminate = 0;
        HanSoloTerminate = 0;
        C3POTerminate = 0;
        R2D2Terminate = 0;
        LandoTerminate = 0;
    }

    //The Diary is a Singleton
    public static Diary getInstance() {
        return Diary.SingletonHolder.instance;
    }

    public AtomicInteger getTotalAttacks() {
        return totalAttacks;
    }

    public void setHanSoloFinish(long hanSoloFinish) {
        HanSoloFinish = hanSoloFinish-StartTime;
    }

    public void setC3POFinish(long c3POFinish) {
        C3POFinish = c3POFinish-StartTime;
    }

    public void setR2D2Deactivate(long r2D2Deactivate) {
        R2D2Deactivate = r2D2Deactivate-StartTime;
    }

    public void setLeiaTerminate(long leiaTerminate) {
        LeiaTerminate = leiaTerminate-StartTime;
    }

    public void setHanSoloTerminate(long hanSoloTerminate) {
        HanSoloTerminate = hanSoloTerminate-StartTime;
    }

    public void setC3POTerminate(long c3POTerminate) {
        C3POTerminate = c3POTerminate-StartTime;
    }

    public void setR2D2Terminate(long r2D2Terminate) {
        R2D2Terminate = r2D2Terminate-StartTime;
    }

    public void setLandoTerminate(long landoTerminate) {
        LandoTerminate = landoTerminate-StartTime;
    }


    public long getHanSoloFinish() {
        return HanSoloFinish;
    }

    public long getC3POFinish() {
        return C3POFinish;
    }

    public long getR2D2Deactivate() {
        return R2D2Deactivate;
    }

    public long getLeiaTerminate() {
        return LeiaTerminate;
    }

    public long getHanSoloTerminate() {
        return HanSoloTerminate;
    }

    public long getC3POTerminate() {
        return C3POTerminate;
    }

    public long getR2D2Terminate() {
        return R2D2Terminate;
    }

    public long getLandoTerminate() {
        return LandoTerminate;
    }

    public void resetNumberAttacks(){
        totalAttacks.compareAndSet(totalAttacks.get(),0);
    }
}
