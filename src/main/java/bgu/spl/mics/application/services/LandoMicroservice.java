package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * LandoMicroservice
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LandoMicroservice extends MicroService {

    long duration;
    Diary myDiary;

    public LandoMicroservice(long duration) {
        super("Lando");
        this.duration = duration;
        myDiary = Diary.getInstance();
    }

    @Override
    protected void initialize() {

        subscribeEvent(BombDestroyerEvent.class, (event) -> {
            //Bomb (simulated by sleeping for the specified duration),
            //announce the completion of the bombing and send a termination broadcast to all threads.
            Thread.sleep(duration);
            complete(event, true);
            sendBroadcast(new TerminationBroadcast());
        });
        
        subscribeBroadcast(TerminationBroadcast.class, (broadcast) -> {
            terminate();
            myDiary.setLandoTerminate(System.currentTimeMillis());
        });
    }
}
