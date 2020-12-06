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
        if (!messageTypes.containsKey(type))
            synchronized (type) {
                if (!messageTypes.containsKey(type))
                    messageTypes.put(type, new ConcurrentLinkedQueue<>());
            }
        messageTypes.get(type).add(m);
    }

    //synchronization only on type to allow other, unrelated types to register as well
    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
        if (!messageTypes.containsKey(type))
            synchronized (type) {
                if (!messageTypes.containsKey(type))
                    messageTypes.put(type, new ConcurrentLinkedQueue<>());
            }
        messageTypes.get(type).add(m);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void complete(Event<T> e, T result) {
        if (events.containsKey(e))
            events.get(e).resolve(result);
    }

    //same lock as subscribe to ensure you cant subscribe to this broadcast type as it's being sent
    @Override
    public void sendBroadcast(Broadcast b) {
        ConcurrentLinkedQueue<MicroService> receivers = messageTypes.get(b.getClass());
        if (receivers != null) {
            synchronized (b.getClass()) {
                for (MicroService service : receivers)
                    services.get(service).add(b);
            }
        }
    }

    //same lock as subscribe to ensure you cant subscribe to this event type as it's being sent
    @Override
    public <T> Future<T> sendEvent(Event<T> e) {
        MicroService service = messageTypes.get(e.getClass()).poll();
        if (service != null) {
            synchronized (e.getClass()) {
                services.get(service).add(e);
                messageTypes.get(e.getClass()).add(service);
                Future<T> future = new Future<>();
                events.put(e, future);
                return future;
            }
        }
        return null;
    }

    //need thread safety!!!
    @Override
    public void register(MicroService m) {
        if (!services.containsKey(m))
            services.put(m, new LinkedBlockingQueue<>());
    }

    //need thread safety!!!
    //Removes the message queue allocated to m and removes all of its subscriptions froms every message type
    @Override
    public void unregister(MicroService m) {
        services.remove(m);
        messageTypes.forEach((key, queue) -> queue.remove(m));
    }

    @Override
    public Message awaitMessage(MicroService m) throws InterruptedException {
        LinkedBlockingQueue<Message> messages = services.get(m);
        if (messages == null)
            throw new IllegalStateException("The Microservice " + m.getName() + "isn't registered");
        return messages.take();
    }
}