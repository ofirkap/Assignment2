package bgu.spl.mics;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageBusImplTest {

    public class SomeService extends MicroService {

        public SomeService() {
            super("service");
        }

        @Override
        protected void initialize() {

        }
    }



    private int num;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
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