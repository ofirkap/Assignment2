package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
/**
 * A {@link DeactivationEvent} is sent to {@link bgu.spl.mics.application.services.R2D2Microservice}
 * to inform him it is time to deactivate the shields
 */
public class DeactivationEvent implements Event<Boolean>{
}
