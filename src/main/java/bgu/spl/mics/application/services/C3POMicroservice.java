package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.TerminationBroadcast;
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

    public C3POMicroservice(Ewoks givenVillage) {
        super("C3PO");
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
        });
        subscribeBroadcast(TerminationBroadcast.class, (broadcast) -> {
            terminate();
        });
    }
}
