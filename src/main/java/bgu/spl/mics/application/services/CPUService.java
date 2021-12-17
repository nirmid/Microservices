package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Message;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.CPU;

import java.util.Timer;
import java.util.TimerTask;

/**
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class CPUService extends MicroService {
    private long time;
    private CPU cpu;

    public CPUService(String name,CPU cpu) {
        super(name);
        this.cpu = cpu;
    }

    @Override
    protected void initialize()  {
        subscribeBroadcast(TerminateBroadcast.class,(t)-> {
            cpu.terminateCpu();
            terminate();
        });
        subscribeBroadcast(TickBroadcast.class, (t)-> {
        cpu.updateTime2(); // for Nir's implement
        });
        /*Thread process = new Thread (()->  // for Nir's implement , no need for this thread
                cpu.processData());
        process.start();*/
    }


}
