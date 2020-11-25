package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.Ewok;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EwokTest {

    private Ewok ewok;

    @BeforeEach
    void setUp() {
        ewok = new Ewok();
    }

    @AfterEach
    void tearDown() {
        ewok = null;
    }

    @Test
    void acquire() {
        assertTrue(ewok.isAvailable());
        ewok.acquire();
        assertFalse(ewok.isAvailable());
    }

    @Test
    void release() {
        assertTrue(ewok.isAvailable());
        ewok.acquire();
        ewok.release();
        assertTrue(ewok.isAvailable());
    }
}