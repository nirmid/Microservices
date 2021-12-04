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
        CPU temp = new CPU(4, null,null,0 );
        assertNotNull("was expecting not null linkedlist of DataBatch",temp.getData()); // testing @pre for nullity
        assertThrows("expected to throw execption for empty list",Exception.class,() -> cpu.processData()); // testing @pre for empty list
        DataBatch d=new DataBatch(new Data(),0);
        DataBatch e=new DataBatch(new Data(),0);
        cpu.recieveDataBatch(d);
        cpu.recieveDataBatch(e);
        cpu.processData();
        assertEquals(e,cpu.getData().getFirst());  // testing @post condition


    }

    @Test
    public void recieveDataBatch() {
        assertThrows("expected excption for null param",Exception.class,() ->cpu.recieveDataBatch(null)); //testing @pre condition
        DataBatch data=new DataBatch(new Data(),0);
        cpu.recieveDataBatch(data);
        assertEquals(data,cpu.getData().getLast()); // testing @post condition

    }

    @Test
    public void updateTime() {
        cpu.updateTime(10);
        assertEquals(10,cpu.getTime());
    }
}