package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

public class TestModelEvent implements Event <Model.results> {
    private Student.Degree type;
    private Model model;

    public TestModelEvent(Student.Degree _type, Model _model){
        type = _type;
        model = _model;
    }

    public Model getModel() {
        return model;
    }

    public Student.Degree getType() {
        return type;
    }
}
