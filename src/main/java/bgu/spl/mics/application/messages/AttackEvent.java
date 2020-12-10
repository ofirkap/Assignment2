package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.passiveObjects.Attack;

/**
 * An {@link AttackEvent} is sent to {@link bgu.spl.mics.application.services.C3POMicroservice}
 * and to {@link bgu.spl.mics.application.services.HanSoloMicroservice} who then preform an
 * {@link Attack} using the given {@param attack}
 */
public class AttackEvent implements Event<Boolean> {

    private final Attack attack;

    public AttackEvent(Attack attack) {
        this.attack = attack;
    }

    public Attack getAttack() {
        return attack;
    }
}
