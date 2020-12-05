package bgu.spl.mics.application.services;


import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.passiveObjects.Ewoks;

/**
 * HanSoloMicroservices is in charge of the handling {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class HanSoloMicroservice extends MicroService {

    Ewoks myVillage;

    public HanSoloMicroservice(Ewoks givenVillage) {
        super("Han");
        this.myVillage = givenVillage;
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
        });
        subscribeBroadcast(TerminationBroadcast.class, (broadcast) -> terminate());
    }
}