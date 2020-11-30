package bgu.spl.mics;

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

    private static MessageBusImpl instance = null;

    private ConcurrentHashMap<MicroService, Queue<Message>> serviceMap;
    private ConcurrentHashMap<Class, Queue<MicroService>> messageMap;

    private MessageBusImpl() {
        serviceMap = new ConcurrentHashMap<>();
        messageMap = new ConcurrentHashMap<>();
    }

    /*
    To create a thread safe Singleton we create a local instance of the MessageBus
    and only if we need initialize it we use synchronized to make sure only one thread
    preforms the initialization.
    */
    public static MessageBusImpl getInstance() {
        if (instance == null) {
            synchronized (MessageBusImpl.class) {
                //check again in case a thread was blocked when 'instance' was NULL but it's been initialized since
                if (instance == null)
                    instance = new MessageBusImpl();
            }
        }
        return instance;
    }

    @Override
    public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
        if (!messageMap.contains(type))
            synchronized (this) {
                if (!messageMap.contains(type))
                    messageMap.put(type, new ConcurrentLinkedQueue<>());
            }
        messageMap.get(type).add(m);
    }

    //why are these functions different???
    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
        if (!messageMap.contains(type))
            synchronized (this) {
                if (!messageMap.contains(type))
                    messageMap.put(type, new ConcurrentLinkedQueue<>());
            }
        messageMap.get(type).add(m);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void complete(Event<T> e, T result) {

    }

    @Override
    public void sendBroadcast(Broadcast b) {

    }


    @Override
    public <T> Future<T> sendEvent(Event<T> e) {

        return null;
    }

    @Override
    public void register(MicroService m) {
        if(!serviceMap.contains(m))
            serviceMap.put(m, new ConcurrentLinkedQueue<>());
    }

    @Override
    public void unregister(MicroService m) {
        serviceMap.remove(m);

    }

    @Override
    public Message awaitMessage(MicroService m) throws InterruptedException {

        return null;
    }
}