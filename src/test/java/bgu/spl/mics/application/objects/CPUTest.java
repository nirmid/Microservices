package bgu.spl.mics.application.objects;

import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.*;

public class CPUTest {
    private static CPU cpu;

    @Before
    public void setUp() throws Exception {
        LinkedList<DataBatch> data=new LinkedList<DataBatch>();
        Cluster cluster=new Cluster();
        cpu=new CPU(4,data,cluster,0);
    }

    @Test
    public void processData() {
        assertNotNull("was expecting not null linkedlist of DataBatch",);
    }

    @Test
    public void recieveDataBatch() {
    }

    @Test
    public void updateTime() {
    }
}