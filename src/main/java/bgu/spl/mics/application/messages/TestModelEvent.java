package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Model;

public class TestModelEvent implements Event {
    public enum studentType{Msc , PhD}
    private studentType type;
    private Model model;

    public TestModelEvent(studentType _type, Model _model){
        type = _type;
        model = _model;
    }

    public Model getModel() {
        return model;
    }

    public studentType getType() {
        return type;
    }
}
