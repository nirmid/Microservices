package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Model;

import java.util.concurrent.ConcurrentLinkedDeque;


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
    private TrainModelEvent trainModelEvent;
    private ConcurrentLinkedDeque<TrainModelEvent> trainModelEvents;


    public GPUService(String name,GPU _gpu) {
        super(name);
        gpu = _gpu;
        gpu.setGpuService(this);
        trainModelEvent= null;
        trainModelEvents = new ConcurrentLinkedDeque<TrainModelEvent>();
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
                gpu.setModel(trainModelEvent.getModel());
                trainModelEvent.getModel().setStatus(Model.status.Training);
            }
            gpu.updateTime();
        });
        subscribeBroadcast(TerminateBroadcast.class,(t) ->{
            terminate();
        });
        subscribeEvent(TrainModelEvent.class,(t)-> {
            trainModelEvents.add(t);
            if(gpu.isDone()) {
                this.trainModelEvent = trainModelEvents.removeFirst();
                gpu.setModel(t.getModel());
                t.getModel().setStatus(Model.status.Training);
            }
                });

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
            t.getModel().setStatus(Model.status.Tested);
        });
    }

}
