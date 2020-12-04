package bgu.spl.mics;

import bgu.spl.mics.example.messages.ExampleBroadcast;
import bgu.spl.mics.example.messages.ExampleEvent;
import bgu.spl.mics.example.services.ExampleEventHandlerService;
import bgu.spl.mics.example.services.ExampleMessageSenderService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class MessageBusImplTest {

    private MessageBus someBus;
    private MicroService subscriber;

    @BeforeEach
    void setUp() {
        someBus = MessageBusImpl.getInstance();
        subscriber = new ExampleEventHandlerService("event sender", new String[]{"1"});
    }

    @AfterEach
    void tearDown() {
        subscriber = null;
    }

    @Test
    void testGetInstance() {
        assertNotNull(someBus);
        assertEquals(someBus, MessageBusImpl.getInstance());
    }

    //ofir
    @Test
    void testSubscribeEvent() {
    }

    //ofir
    @Test
    void testSubscribeBroadcast() {
    }

    //amit
    @Test
    void testComplete() {
        ExampleEvent event = new ExampleEvent(subscriber.getName());
        someBus.register(subscriber);
        Future<String> futureObject = subscriber.sendEvent(event);
        if (futureObject != null) {
            assertFalse(futureObject.isDone());
            someBus.complete(event, "resolved");
            assertTrue(futureObject.isDone());
        }
        someBus.unregister(subscriber);
    }

    //ofir
    @Test
    void testSendBroadcast() {
        ExampleBroadcast broadcast = new ExampleBroadcast("hi");
        someBus.register(subscriber);
        someBus.subscribeBroadcast(broadcast.getClass(), subscriber);
        someBus.sendBroadcast(broadcast);
        try {
            assertEquals(someBus.awaitMessage(subscriber), broadcast);
        } catch (InterruptedException e) {
            System.out.println("test failed, broadcast wasn't sent");
            someBus.unregister(subscriber);
        } catch (NullPointerException e) {
            fail();
            someBus.unregister(subscriber);
        }
        someBus.unregister(subscriber);
    }

    //ofir
    @Test
    void testSendEvent() {
        ExampleEvent event = new ExampleEvent("sender");
        someBus.register(subscriber);
        someBus.subscribeEvent(event.getClass(), subscriber);
        assertNotNull(someBus.sendEvent(event));
        someBus.unregister(subscriber);
    }

    //amit
    @Test
    void testRegister() {
    }

    //amit
    @Test
    void testUnregister() {
    }

    //amit
    @Test
    void testAwaitMessage() {
        someBus.register(subscriber);
        ExampleEvent event = new ExampleEvent("sender");
        someBus.subscribeEvent(event.getClass(), subscriber);
        someBus.sendEvent(event);
        try {
            assertEquals(event, someBus.awaitMessage(subscriber));
        } catch (InterruptedException ignored) {}
        someBus.unregister(subscriber);
    }
}
