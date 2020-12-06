package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.AttackFinishTimeBroadcast;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;


/**
 * C3POMicroservices is in charge of the handling {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class C3POMicroservice extends MicroService {

    Ewoks myVillage;
    Diary myDiary;

    public C3POMicroservice(Ewoks givenVillage) {
        super("C3PO");
        this.myVillage = givenVillage;
        myDiary = Diary.getInstance();
    }

    @Override
    protected void initialize() {

        subscribeEvent(AttackEvent.class, (event) -> {
            for (int serial : event.getAttack().getSerials()) {
                if (!myVillage.acquireEwok(serial))
                    wait();
            }
            Thread.sleep(event.getAttack().getDuration());
            complete(event, true);
            for (int serial : event.getAttack().getSerials())
                myVillage.releaseEwok(serial);
            myDiary.getTotalAttacks().addAndGet(1);
        });

        subscribeBroadcast(AttackFinishTimeBroadcast.class, (broadcast) -> myDiary.setC3POFinish(System.currentTimeMillis()));

        subscribeBroadcast(TerminationBroadcast.class, (broadcast) -> {
            terminate();
            myDiary.setC3POTerminate(System.currentTimeMillis());
        });
    }
}