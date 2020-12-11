package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * R2D2Microservices is in charge of the handling {@link DeactivationEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link DeactivationEvent}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class R2D2Microservice extends MicroService {

    long duration;
    Diary myDiary;

    public R2D2Microservice(long duration) {
        super("R2D2");
        this.duration = duration;
        myDiary = Diary.getInstance();
    }

    @Override
    protected void initialize() {

        subscribeEvent(DeactivationEvent.class, (event) -> {
            //Deactivate the shields (simulated by sleeping for the specified duration),
            //and announce the completion of the deactivation.
            Thread.sleep(duration);
            complete(event, true);
            myDiary.setR2D2Deactivate(System.currentTimeMillis());
            //After deactivating the shields send lando the 'BombDestroyerEvent' to finally defeat the empire (for now)
            sendEvent(new BombDestroyerEvent());
        });

        subscribeBroadcast(TerminationBroadcast.class, (broadcast) -> {
            terminate();
            myDiary.setR2D2Terminate(System.currentTimeMillis());
        });
    }
}
