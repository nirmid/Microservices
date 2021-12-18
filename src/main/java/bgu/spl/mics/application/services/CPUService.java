package bgu.spl.mics.application.services;


import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.CPU;

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
            terminate();
        });
        subscribeBroadcast(TickBroadcast.class, (t)-> {
            cpu.updateTime();
        });
    }


}
