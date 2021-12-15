package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Model;


public class TrainModelEvent implements Event<String> {
    private Model model;
    public TrainModelEvent(Model m){
        model = m;
    }

    public Model getModel() {
        return model;
    }
}