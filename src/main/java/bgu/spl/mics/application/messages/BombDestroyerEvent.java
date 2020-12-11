package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Attack;

/**
 * A {@link BombDestroyerEvent} is sent to {@link bgu.spl.mics.application.services.LandoMicroservice}
 * to inform him it is time to bomb the destroyer
 */
public class BombDestroyerEvent implements Event<Boolean> {

}
