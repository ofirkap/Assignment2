package bgu.spl.mics;

import bgu.spl.mics.example.messages.ExampleBroadcast;
import bgu.spl.mics.example.messages.ExampleEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class MessageBusImplTest {

    public class SomeService extends MicroService {

        private boolean isEvent;

        public SomeService(boolean isEvent) {
            super("service");
            this.isEvent = isEvent;
        }

        @Override
        protected void initialize() {
            if (isEvent){
                Future<String> futureObject = (Future<String>)sendEvent(new SomeEvent());
                String resolved = futureObject.get();
                System.out.println(resolved);
            }
            else {
                sendBroadcast(new ExampleBroadcast(getName()));
                System.out.println(getName());
            }
            terminate();

        }
    }

    public class SomeEvent implements Event<String>{}

    public class SomeBroadcast implements Broadcast{}


    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    //ofir
    @Test
    void subscribeEvent() {
        MicroService m1 = new SomeService(true);
        m1.subscribeEvent(ExampleEvent.class,  ev -> {
            m1.complete(ev, "Hello");
        });
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