package bgu.spl.mics.application.objects;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.*;

public class CPUTest {
    private static CPU cpu;
    private static Cluster cluster;

    @Before
    public void setUp() throws Exception {
        cpu = new CPU(16);
        cluster = Cluster.getInstance();

    }

    @Test
    public void updateTime() {
        cpu.updateTime();
        assertEquals(2,cpu.getTime());
    }
}