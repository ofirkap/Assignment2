package bgu.spl.mics;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

    private static class SingletonHolder {
        private static final MessageBusImpl instance = new MessageBusImpl();
    }

    //HashMap of message queues for each service subscribed to the bus
    private final ConcurrentHashMap<MicroService, LinkedBlockingQueue<Message>> services;
    //HashMap of round robin queues for each message type
    private final ConcurrentHashMap<Class<? extends Message>, ConcurrentLinkedQueue<MicroService>> messageTypes;
    @SuppressWarnings("rawtypes")
    private final ConcurrentHashMap<Event, Future> events;

    private MessageBusImpl() {
        services = new ConcurrentHashMap<>();
        messageTypes = new ConcurrentHashMap<>();
        events = new ConcurrentHashMap<>();
    }

    /**
     * The messageBus is implemented as a Singleton
     */
    public static MessageBusImpl getInstance() {
        return SingletonHolder.instance;
    }

    //Create a new round robin queue for event 'type' if absent and add 'm' to it
    @Override
    public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
        messageTypes.putIfAbsent(type, new ConcurrentLinkedQueue<>());
        messageTypes.get(type).add(m);
    }

    //Create a new round robin queue for broadcast 'type' if absent and add 'm' to it
    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
        messageTypes.putIfAbsent(type, new ConcurrentLinkedQueue<>());
        messageTypes.get(type).add(m);
    }

    @Override
    @SuppressWarnings("unchecked")
    //resolve the future 'result' associated with event 'e' and remove it from 'events'
    //synchronized with the corresponding sendEvent call
    //to make sure thread 1 finishes sending the event before thread 2 completes it.
    public <T> void complete(Event<T> e, T result) {
        synchronized (e) {
            events.getOrDefault(e, new Future<T>()).resolve(result);
            events.remove(e);
        }
    }

    //Loop on all services who subscribed to broadcast b and add it to their message queue
    @Override
    public void sendBroadcast(Broadcast b) {
        if (messageTypes.get(b.getClass()) != null) {
            for (MicroService service : messageTypes.get(b.getClass()))
                services.get(service).add(b);
        }
    }

    //Pop the first service waiting for event of type 'e' from the round robin queue and add 'e' to his message queue
    //synchronized on 'messageTypes' to restrict unregistering a service while it receives an event
    //synchronized on 'messageTypes' to restrict 2 threads sending event simultaniusly
    //synchronized with the corresponding complete call
    //to make sure thread 1 finishes sending the event before thread 2 completes it.
    @Override
    @SuppressWarnings("unchecked")
    public <T> Future<T> sendEvent(Event<T> e) {
        if (messageTypes.get(e.getClass()) == null)
            return null;
        synchronized (messageTypes) {
            MicroService service = messageTypes.get(e.getClass()).poll();
            if (service != null) {
                messageTypes.get(e.getClass()).add(service);
                synchronized (e) {
                    services.get(service).add(e);
                    events.putIfAbsent(e, new Future<>());
                    return events.get(e);
                }
            }
        }
        return null;
    }

    //Create a new message queue for the service 'm'
    @Override
    public void register(MicroService m) {
        services.putIfAbsent(m, new LinkedBlockingQueue<>());
    }

    //Removes the message queue allocated to 'm' and removes all of its subscriptions froms every message type
    //synchronized on 'messageTypes' to restrict unregistering a service while it receives an event
    @Override
    public void unregister(MicroService m) {
        services.remove(m);
        synchronized (messageTypes) {
            messageTypes.forEach((key, queue) -> queue.remove(m));
        }
    }

    //Return the first message waiting to be handled from the message queue or wait for one to appear
    @Override
    public Message awaitMessage(MicroService m) throws InterruptedException {
        LinkedBlockingQueue<Message> messages = services.get(m);
        if (messages == null)
            throw new IllegalStateException("The Microservice " + m.getName() + "isn't registered");
        return messages.take();
    }
}