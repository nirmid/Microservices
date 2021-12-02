package bgu.spl.mics;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class FutureTest {
    private static Future future;
    @Before
    public void setUp() throws Exception {
        future = new Future();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void get() {
        assertNotNull("expected result to not be null",future.get());
    }

    @Test
    public void resolve() {

    }
}