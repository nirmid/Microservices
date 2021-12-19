package bgu.spl.mics.application.objects;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GPUTest {
    private GPU gpu;

    @Before
    public void setUp() throws Exception {
        gpu = new GPU(GPU.Type.GTX1080);

    }

    @Test
    public void updateTime(){
        gpu.updateTime();
        assertEquals(2,gpu.getTime());
    }

    @Test
    public void setModel() {
        Data data = new Data(Data.Type.Images,1000);
        assertThrows("wa expecting exception for null argument",IllegalArgumentException.class, ()-> gpu.setModel(null) );
        Model model = new Model("test model",data);
        gpu.setModel(model);
        assertEquals(gpu.getModel(),model);
    }
}