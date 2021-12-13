package bgu.spl.mics.application.objects;

/**
 * Passive object representing a Deep Learning model.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */


public class Model {

    enum status {PreTrained, Training, Trained, Tested}
    enum results {None, Good, Bad}



    private String name;
    private Data data;
    private Student student;
    private status status;
    private results result;

    public Model  (String name, Data data, Student student, status status, results result){

    }

    public Data getData(){ return data;}


}
