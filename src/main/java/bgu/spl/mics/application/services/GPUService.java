package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Model;

import java.util.concurrent.ConcurrentLinkedDeque;

import static bgu.spl.mics.application.objects.Student.Degree.PhD;

/**
 * GPU service is responsible for handling the
 * {@link TrainModelEvent} and {@link TestModelEvent},
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class GPUService extends MicroService {
    private GPU gpu;
    private boolean isDone;
    private TrainModelEvent trainModelEvent; // Nir's implement
    private ConcurrentLinkedDeque<TrainModelEvent> trainModelEvents;

    public GPUService(String name,GPU _gpu) {
        super(name);
        gpu = _gpu;
        isDone = true;
    }

    public GPUService(String name,GPU _gpu,int n) {  // Nir's implement
        super(name);
        gpu = _gpu;
        isDone = true; // no need , using GPU isDone
        gpu.setGpuService(this);
        trainModelEvent= null;
        trainModelEvents = new ConcurrentLinkedDeque<TrainModelEvent>();
    }

    public boolean getIsDoneGpu(){   // Nir's implement
        return gpu.isDone();
     }

     public void gpuComplete(){
         trainModelEvent.getModel().setStatus(Model.status.Trained);
         complete(trainModelEvent,trainModelEvent.getModel());
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class,(t)-> {
            if(gpu.isDone() && !trainModelEvents.isEmpty()) {
                trainModelEvent = trainModelEvents.removeFirst();
                gpu.setModel2(trainModelEvent.getModel());
                trainModelEvent.getModel().setStatus(Model.status.Training);
            }
            gpu.updateTime2(); // Nir's implement
        });
        subscribeBroadcast(TerminateBroadcast.class,(t) ->{
            gpu.terminate();
            terminate();
        });
        subscribeEvent(TrainModelEvent.class,(t)-> { // Nir's implement
            trainModelEvents.add(t);
            if(gpu.isDone()) {
                this.trainModelEvent = trainModelEvents.removeFirst();
                gpu.setModel2(t.getModel());
                t.getModel().setStatus(Model.status.Training);
            }
                });

        /*subscribeEvent(TrainModelEvent.class,(t)-> {
                    Thread set = new Thread(() ->
                    {
                        t.getModel().setStatus(Model.status.Training);
                        try {
                            isDone = false;
                            gpu.setModel(t.getModel());
                            t.getModel().setStatus(Model.status.Trained);
                            complete(t, t.getModel());
                            isDone = true;
                        } catch (InterruptedException e) {
                        }
                    });
                    set.start();
                    System.out.println("Thread TrainModelEvent is terminated" );
                });
         */

        subscribeEvent(TestModelEvent.class,(t)->{
            double rnd = Math.random();
            switch (t.getType()){
                case MSc:
                    if(rnd < 0.8) {
                        complete(t, Model.results.Good);
                        t.getModel().setResult(Model.results.Good);
                    }
                    else {
                        complete(t, Model.results.Bad);
                        t.getModel().setResult(Model.results.Bad);
                    }
                    break;
                case PhD:
                    if(rnd < 0.6) {
                        complete(t, Model.results.Good);
                        t.getModel().setResult(Model.results.Good);
                    }
                    else {
                        complete(t, Model.results.Bad);
                        t.getModel().setResult(Model.results.Bad);
                    }
                    break;
                default:
                    if(rnd < 0.6) {
                        complete(t, Model.results.Good);
                        t.getModel().setResult(Model.results.Good);
                    }
                    else {
                        complete(t, Model.results.Bad);
                        t.getModel().setResult(Model.results.Bad);
                    }
                    break;

            }
            System.out.println("Models is tested: " + t.getModel().getName());
            t.getModel().setStatus(Model.status.Tested);
        });
    }

    public boolean isDone() {
        return isDone;
    }
}
