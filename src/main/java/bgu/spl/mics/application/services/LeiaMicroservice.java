package bgu.spl.mics.application.services;


import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Diary;

import java.util.concurrent.CountDownLatch;

/**
 * LeiaMicroservices Initialized with Attack objects, and sends them as  {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LeiaMicroservice extends MicroService {
    private final Attack[] attacks;
    private final Diary myDiary;
    private final CountDownLatch countDown;

    public LeiaMicroservice(Attack[] attacks, CountDownLatch count) {
        super("Leia");
        this.attacks = attacks;
        myDiary = Diary.getInstance();
        this.countDown = count;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void initialize() {

        subscribeBroadcast(TerminationBroadcast.class, (broadcast) -> {
            terminate();
            myDiary.setLeiaTerminate(System.currentTimeMillis());
        });

        //Leia waits until C3PO and HanSolo finish initializing
        try {
            countDown.await();
        }catch (InterruptedException ignored){}

        //Send all the attacks for han and c3po to preform, store the future results in 'attackResults'
        Future<Boolean>[] attackResults = new Future[attacks.length];
        for (int i = 0; i < attacks.length; i++) {
            attackResults[i] = sendEvent(new AttackEvent(attacks[i]));
        }

        //Send the 'AttackFinishTimeBroadcast' to han and c3po so they can register their finish time in the diary
        //This broadcast will be added to han / c3po message queue after all the attacks and because of that
        //it will be executed after them.
        sendBroadcast(new AttackFinishTimeBroadcast());

        //get the results from all the attacks and ensure they all finished
        for (int i = 0; i < attacks.length; i++) {
            attackResults[i].get();
        }

        //send r2d2 the deactivation event (because the attacks finished)
        sendEvent(new DeactivationEvent());
    }
}