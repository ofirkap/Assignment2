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

    private final ConcurrentHashMap<MicroService, LinkedBlockingQueue<Message>> services;
    private final ConcurrentHashMap<Class, ConcurrentLinkedQueue<MicroService>> messageTypes;
    private final ConcurrentHashMap<Event, Future> events;

    private MessageBusImpl() {
        services = new ConcurrentHashMap<>();
        messageTypes = new ConcurrentHashMap<>();
        events = new ConcurrentHashMap<>();
    }

    //The messageBus is a Singleton
    public static MessageBusImpl getInstance() {
        return SingletonHolder.instance;
    }

    //synchronization only on type to allow other, unrelated types to register as well
    @Override
    public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
        messageTypes.putIfAbsent(type, new ConcurrentLinkedQueue<>());
        messageTypes.get(type).add(m);
    }

    //synchronization only on type to allow other, unrelated types to register as well
    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
        messageTypes.putIfAbsent(type, new ConcurrentLinkedQueue<>());
        messageTypes.get(type).add(m);
    }

    @Override
    @SuppressWarnings("unchecked")
    //resolve the future 'result' associated with event 'e' and remove it from 'events'
    public <T> void complete(Event<T> e, T result) {
        synchronized (e) {
                events.getOrDefault(e, new Future<T>()).resolve(result);
                events.remove(e);
        }
    }

    //same lock as subscribe to ensure you cant subscribe to this broadcast type as it's being sent
    @Override
    public void sendBroadcast(Broadcast b) {
        if (messageTypes.get(b.getClass()) != null) {
            synchronized (services) {
                for (MicroService service : messageTypes.get(b.getClass()))
                    services.get(service).add(b);
            }
        }
    }

    //same lock as subscribe to ensure you cant subscribe to this event type as it's being sent
    @Override
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

    @Override
    public void register(MicroService m) {
            services.putIfAbsent(m, new LinkedBlockingQueue<>());
    }

    //Removes the message queue allocated to m and removes all of its subscriptions froms every message type
    @Override
    public void unregister(MicroService m) {
            services.remove(m);
        synchronized (messageTypes) {
            messageTypes.forEach((key, queue) -> queue.remove(m));
        }
    }

    @Override
    public Message awaitMessage(MicroService m) throws InterruptedException {
        LinkedBlockingQueue<Message> messages = services.get(m);
        if (messages == null)
            throw new IllegalStateException("The Microservice " + m.getName() + "isn't registered");
        return messages.take();
    }
}