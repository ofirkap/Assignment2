package bgu.spl.mics.application.services;

import java.util.ArrayList;
import java.util.List;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.passiveObjects.Attack;

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

    public LeiaMicroservice(Attack[] attacks) {
        super("Leia");
        this.attacks = attacks;
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TerminationBroadcast.class, (broadcast) -> {
            terminate();
        });
        Future<Boolean>[] attackResults = new Future[attacks.length];
        boolean attackFinished = false;
        for (int i = 0; i < attacks.length; i++) {
            attackResults[i] = sendEvent(new AttackEvent(attacks[i]));
        }
        for (int i = 0; i < attacks.length; i++) {
            attackFinished = attackResults[i].get();
        }
        sendEvent(new DeactivationEvent());
    }
}