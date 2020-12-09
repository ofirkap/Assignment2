package bgu.spl.mics.application.services;


import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Diary;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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
    protected void initialize() {

        subscribeBroadcast(TerminationBroadcast.class, (broadcast) -> {
            terminate();
            myDiary.setLeiaTerminate(System.currentTimeMillis());
        });

        Future<Boolean>[] attackResults = new Future[attacks.length];
        boolean attackFinished = false;
        //leia waits for a short while before starting sending messages to make sure all other threads
        //successfully subscribed to all relevant message types.
        try {
            countDown.await();
        }catch (InterruptedException ignored){}
        //send all the attacks for han and c3po to preform, store the future results in 'attackResults'
        for (int i = 0; i < attacks.length; i++) {
            attackResults[i] = sendEvent(new AttackEvent(attacks[i]));
        }

        //send the 'AttackFinishTimeBroadcast' to han and c3po so they can register their finish time in the diary
        //this broadcast will be added to han / c3po message queue after all the attacks
        //and because of that it will be executed after them.
        sendBroadcast(new AttackFinishTimeBroadcast());

        //get the results from all the attacks and ensure they all finished
        //if not wait for 500 Milli and try again
        for (int i = 0; i < attacks.length; i++) {
            attackFinished = attackResults[i].get();
        }

        //send r2d2 the deactivation event (because the attacks finished) and store the future result
        Future<Boolean> deactivationResult = sendEvent(new DeactivationEvent());
        //get the result after completion, if the event haven't completed wait for 500 Milli and try again
        boolean deactivationFinished = deactivationResult.get();

        //after the deactivation of the shields send lando the 'BombDestroyerEvent' to finally defeat the empire
        sendEvent(new BombDestroyerEvent());
    }
}