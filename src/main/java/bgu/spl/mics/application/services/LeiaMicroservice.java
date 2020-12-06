package bgu.spl.mics.application.services;


import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Diary;

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
    Diary myDiary;

    public LeiaMicroservice(Attack[] attacks) {
        super("Leia");
        this.attacks = attacks;
        myDiary = Diary.getInstance();
    }

    @Override
    protected void initialize() {

        subscribeBroadcast(TerminationBroadcast.class, (broadcast) -> {
            terminate();
            myDiary.setLeiaTerminate(System.currentTimeMillis());
        });

        Future<Boolean>[] attackResults = new Future[attacks.length];
        boolean attackFinished = false;
        for (int i = 0; i < attacks.length; i++) {
            attackResults[i] = sendEvent(new AttackEvent(attacks[i]));
        }
        for (int i = 0; i < attacks.length; i++) {
            attackFinished = attackResults[i].get();
        }
        while (!attackFinished) {
            try {
                wait(500);
            } catch (InterruptedException ignored) {
            }
        }

        sendBroadcast(new AttackFinishTimeBroadcast());

        Future<Boolean> deactivationResult = sendEvent(new DeactivationEvent());
        boolean deactivationFinished = deactivationResult.get();
        while (!deactivationFinished) {
            try {
                wait(500);
            } catch (InterruptedException ignored) {
            }
        }

        sendBroadcast(new DeactivationFinishTimeBroadcast());

        sendEvent(new BombDestroyerEvent());
    }
}