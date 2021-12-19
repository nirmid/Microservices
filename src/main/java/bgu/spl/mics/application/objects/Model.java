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
    private status status_;   // student name
    private results result;

    public Model  (String name, Data data, Student student){
        this.name = name;
        this.data = data;
        this.student = student;
        status_ = Model.status.PreTrained;
        result = results.None;
    }

    public Model(String name, Data data){
        this.name = name;
        this.data = data;
        student = null;
        status_ = Model.status.PreTrained;
        result = results.None;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Data getData(){ return data;}

    public String getName() {
        return name;
    }

    public void setResult(results result){this.result = result;}

    public results getResult(){
        return result;
    }

    public status getStatus() {
        return status_;
    }

    public void setStatus(status status){this.status_ = status;}

    public String toString(){
        String output = status_+" , "+result+" , "+name;
        return output;
    }


}
