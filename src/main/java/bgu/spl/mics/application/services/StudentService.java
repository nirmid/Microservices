package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

/**
 * Student is responsible for sending the {@link TrainModelEvent},
 * {@link TestModelEvent} and {@link PublishResultsEvent}.
 * In addition, it must sign up for the conference publication broadcasts.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class StudentService extends MicroService {

    private Student student;
    private boolean terminated;


    public StudentService(String name, Student _student) {
        super(name);
        student = _student;
        boolean terminated = false;
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TerminateBroadcast.class,(t) -> {
            terminated = true;
            student.terminate();
            terminate();
            synchronized (this) {
                notifyAll();
            }
        });
        subscribeBroadcast(PublishConferenceBroadcast.class,(t) -> {
            int toRead = t.getConference().getNumOfPublications();
            student.setPapersRead(toRead);
        });
        subscribeBroadcast(TickBroadcast.class, (t)-> {
            synchronized (this) {
                notifyAll();
            }
        });

        Thread send = new Thread (()-> {
            while (!terminated && !student.getModels().isEmpty()) {
                Model curModel;
                synchronized (student.getModels()) {
                    curModel = (Model) student.getModels().removeFirst();
                }
                Future newFuture = sendEvent(new TrainModelEvent(curModel));
                while (!terminated && !newFuture.isDone()) {
                    try {
                        synchronized (this) {
                            wait();
                        }
                    } catch (InterruptedException e) {
                    }
                }
                if (!terminated) {
                    sendEvent(new TestModelEvent(student.getStatus(), curModel));
                    while (!terminated && curModel.getResult() == Model.results.None) {
                        try {
                            synchronized (this) {
                                wait();
                            }
                        } catch (InterruptedException e) { }
                    }
                    if (!terminated) {
                        if (curModel.getResult() == Model.results.Good)
                            sendEvent(new PublishResultsEvent(curModel));
                        student.receiveTrainedModels(curModel);
                    }
                }
            }
        });
        send.start();
    }

}
