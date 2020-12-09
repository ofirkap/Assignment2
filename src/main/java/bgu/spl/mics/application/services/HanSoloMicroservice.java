package bgu.spl.mics.application.services;


import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.AttackFinishTimeBroadcast;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;

import java.util.concurrent.CountDownLatch;

/**
 * HanSoloMicroservices is in charge of the handling {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class HanSoloMicroservice extends MicroService {

    private final Ewoks myVillage = Ewoks.getInstance();
    private final Diary myDiary = Diary.getInstance();
    private final CountDownLatch countDown;

    public HanSoloMicroservice(CountDownLatch count) {
        super("Han");
        this.countDown = count;

    }

    @Override
    protected void initialize() {

        subscribeEvent(AttackEvent.class, (event) -> {
            //Acquire all ewoks needed for the attack
            for (int serial : event.getAttack().getSerials()) {
                myVillage.acquireEwok(serial);
            }
            //Attack (simulated by sleeping for the specified duration)
            Thread.sleep(event.getAttack().getDuration());
            //Announce the attack is done and release all ewoks after finishing the attack
            complete(event, true);
            for (int serial : event.getAttack().getSerials())
                myVillage.releaseEwok(serial);
            //Increase the number of attacks preformed by 1
            myDiary.getTotalAttacks().addAndGet(1);
        });

        subscribeBroadcast(AttackFinishTimeBroadcast.class, (broadcast) -> myDiary.setHanSoloFinish(System.currentTimeMillis()));

        subscribeBroadcast(TerminationBroadcast.class, (broadcast) -> {
            terminate();
            myDiary.setHanSoloTerminate(System.currentTimeMillis());
        });
        countDown.countDown();
    }
}