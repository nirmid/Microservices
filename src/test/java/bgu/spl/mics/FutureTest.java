package bgu.spl.mics;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class FutureTest {
    private static Future<String> future;
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
        assertThrows("was expected for exception for null param",Exception.class,() -> future.resolve(null)  );
        assertNull("was expected result to be null",future.get());
        future.resolve("test");
        assertEquals("test",future.get());
    }

    @Test
    public void getTimeout() {
        assertNull("expected null", future.get(10, TimeUnit.MILLISECONDS));
        assertThrows("was expecting non null & positive long",Exception.class,() -> future.get(-10,TimeUnit.MILLISECONDS));
        assertThrows("was expecting non null & positive long",Exception.class,() -> future.get(10,null));
        future.resolve("test");
        assertEquals("was expecting same value as resolve","test",future.get());
    }
}