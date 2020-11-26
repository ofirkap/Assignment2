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

    private class SomeService extends MicroService {

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
    }

    private MessageBus someBus;
    private MicroService subscriber;
    private ExampleMessageSenderService sender;
    private Message msg;

    @BeforeEach
    void setUp() {
        someBus = new MessageBusImpl();
        subscriber = new ExampleEventHandlerService("event sender", new String[]{"1"});
        sender = new ExampleMessageSenderService("event subscriber", new String[]{"event"});
    }

    @AfterEach
    void tearDown() {
    }

    //ofir
    @Test
    void subscribeEvent() {
        someBus.register(subscriber);
        someBus.subscribeEvent(ExampleEvent.class, subscriber);
        Future<String> misc = sender.sendEvent(new ExampleEvent(sender.getName()));

        try{
            assertNotNull(someBus.awaitMessage(subscriber));
        } catch (InterruptedException e) {
            System.out.println("test failed, message wasn't received");
        }
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
    }

    //ofir
    @Test
    void sendEvent() {
        MicroService m1 = new SomeService(true);
        m1.initialize();

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
