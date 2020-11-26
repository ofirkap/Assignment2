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
        someBus = new MessageBusImpl();
        subscriber = new ExampleEventHandlerService("event sender", new String[]{"1"});
    }

    @AfterEach
    void tearDown() {
        someBus = null;
        subscriber = null;
    }

    //ofir
    @Test
    void subscribeEvent() {
    }

    //ofir
    @Test
    void subscribeBroadcast() {
    }

    //amit
    @Test
    void complete() {
    }

    //ofir
    @Test
    void sendBroadcast() {
        ExampleBroadcast broadcast = new ExampleBroadcast("hi");
        someBus.register(subscriber);
        someBus.subscribeBroadcast(broadcast.getClass(), subscriber);
        someBus.sendBroadcast(broadcast);
        try {
            assertEquals(someBus.awaitMessage(subscriber).toString(), "hi");
        } catch (InterruptedException e) {
            System.out.println("test failed, broadcast wasn't sent");
        }
    }

    //ofir
    @Test
    void sendEvent() {
        ExampleEvent event = new ExampleEvent("sender");
        someBus.register(subscriber);
        someBus.subscribeEvent(event.getClass(), subscriber);
        assertNotNull(someBus.sendEvent(event));
    }

    //amit
    @Test
    void register() {
    }

    //amit
    @Test
    void unregister() {
    }

    //amit
    @Test
    void awaitMessage() {
    }
}
    /*private class SomeService extends MicroService {

        private boolean isEvent;
        private boolean happend;

        public SomeService(boolean isEvent) {
            super("service");
            this.isEvent = isEvent;
            this.happend = false;
        }

        @Override
        protected void initialize() {
            if (isEvent) {
                subscribeEvent(ExampleEvent.class, ev -> {
                    happend = true;
                });
            } else {
                subscribeBroadcast(ExampleBroadcast.class, message -> {
                    happend = true;
                });
            }
            terminate();
        }
    }
    private class Callback implements bgu.spl.mics.Callback<Boolean> {
        public void call(Boolean c) {
            c = true;
        }
    }*/