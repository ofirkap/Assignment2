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

    private static class SingletonHolder{
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

    @Override
    public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
        if (!messageTypes.containsKey(type))
            synchronized (this) {
                if (!messageTypes.containsKey(type))
                    messageTypes.put(type, new ConcurrentLinkedQueue<>());
            }
        messageTypes.get(type).add(m);
    }

    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
        if (!messageTypes.containsKey(type))
            synchronized (this) {
                if (!messageTypes.containsKey(type))
                    messageTypes.put(type, new ConcurrentLinkedQueue<>());
            }
        messageTypes.get(type).add(m);
    }

    //make sure 2 services cant handle same event
    @Override
    @SuppressWarnings("unchecked")
    public <T> void complete(Event<T> e, T result) {
        if (events.containsKey(e))
            events.get(e).resolve(result);
    }

    //more efficient thread safety??
    @Override
    public synchronized void sendBroadcast(Broadcast b) {
        ConcurrentLinkedQueue<MicroService> receivers = messageTypes.get(b.getClass());
        for (MicroService service : receivers)
            services.get(service).add(b);
    }

    //need thread safety in case 2 threads try to sent events subscribed by the same service!!!
    @Override
    public <T> Future<T> sendEvent(Event<T> e) {
        MicroService service = messageTypes.get(e.getClass()).poll();
        if (service != null) {
            services.get(service).add(e);
            messageTypes.get(e.getClass()).add(service);
            Future<T> future = new Future<>();
            events.put(e, future);
            return future;
        }
        return null;
    }

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
        messageTypes.forEach((key,queue)-> queue.remove(m));
    }

    @Override
    public Message awaitMessage(MicroService m) throws InterruptedException {
        LinkedBlockingQueue<Message> messages = services.get(m);
        if (messages == null)
            throw new IllegalStateException("The Microservice " + m.getName() + "isn't registered");
        return messages.take();
    }
}