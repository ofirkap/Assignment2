package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.passiveObjects.Attack;

/**
 * An {@link AttackFinishTimeBroadcast} is sent to
 * {@link bgu.spl.mics.application.services.C3POMicroservice} and to
 * {@link bgu.spl.mics.application.services.HanSoloMicroservice} after
 * finishing all {@link AttackEvent} to register completion time in
 * the {@link bgu.spl.mics.application.passiveObjects.Diary}
 */
public class AttackFinishTimeBroadcast implements Broadcast {
}
