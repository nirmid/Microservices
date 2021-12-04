package bgu.spl.mics.application.objects;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GPUTest {
    private static GPU gpu;

    @Before
    public void setUp() throws Exception {
        Cluster cluster=new Cluster();
        gpu=new GPU(GPU.Type.GTX1080,cluster);
    }

    @Test
    public void insertProcessed() {
        assertTrue("preTrained.size should be smaller than capacity",gpu.getPreTrainedSize() < gpu.getCapacity());
        Data d1=new Data();
        Data d2=new Data();
        DataBatch data1= new DataBatch(d1,0);
        DataBatch data2= new DataBatch(d2,0);
        Model model = new Model("x",d1,new Student(), Model.status.PreTrained, Model.results.None);
        gpu.setModel(model);
        assertThrows("expected Exception for different data",Exception.class,() -> gpu.insertProcessed(data2));
        //post
        int preSize = gpu.getPreTrainedSize();
        gpu.insertProcessed(data1);
        assertTrue("expected preTrained.size to be equal to be @pre(preTrained.size)+1 ",preSize + 1 == gpu.getPreTrainedSize());
        assertTrue("expected preTrained.size <= capacity",gpu.getPreTrainedSize() <=gpu.getCapacity());



    }

    @Test
    public void setModel() {
        Data d1=new Data();
        DataBatch data1= new DataBatch(d1,0);
        Model model = new Model("x",d1,new Student(), Model.status.PreTrained, Model.results.None);
        gpu.setModel(model);
        assertEquals(0,gpu.getBatchIdx());
        gpu.insertProcessed(data1);
        Model model2 = new Model("y",new Data(),new Student(), Model.status.PreTrained, Model.results.None);
        assertThrows("cannot set model to gpu while processing another model",Exception.class,()-> gpu.setModel(model2));
        assertEquals(model,gpu.getModel());


    }
}