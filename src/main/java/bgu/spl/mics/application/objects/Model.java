package bgu.spl.mics.application.objects;

/**
 * Passive object representing a Deep Learning model.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */


public class Model {

    public enum status {PreTrained, Training, Trained, Tested}
    public enum results {None, Good, Bad}
    private String name;
    private Data data;
    private Student student;
    private status status;   // student name
    private results result;

    public Model  (String name, Data data, Student student){
        this.name = name;
        this.data = data;
        this.student = student;
        status = Model.status.PreTrained;
        result = results.None;
    }

    public Data getData(){ return data;}
    public void setResult(results result){this.result = result;}
    public void setStatus(status status){this.status = status;}


}
