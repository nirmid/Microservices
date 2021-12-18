package bgu.spl.mics.application.objects;

import bgu.spl.mics.Event;

import java.awt.*;
import java.util.LinkedList;

/**
 * Passive object representing single student.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Student {
    /**
     * Enum representing the Degree the student is studying for.
     */
    public enum Degree {
        MSc, PhD
    }

    private String name;
    private String department;
    private Degree status;
    private int publications;
    private int papersRead;
    private LinkedList<Model> models;
    private boolean terminated;
    private boolean isDone;
    private LinkedList<Model> trainedModels;

    public Student(String _name, String _department, Degree _status, LinkedList<Model>  _models){
        name = _name;
        department = _department;
        status = _status;
        publications = 0;
        papersRead = 0;
        models = _models;
        terminated = false;
        isDone = false;
        trainedModels = new LinkedList<Model>();
    }

    public void receiveTrainedModels(Model trainedModel){
        publications = publications + 1;
        synchronized (trainedModel) {
            trainedModels.add(trainedModel);
        }
    }

    public LinkedList<Model> getTrainedModels() {
        return trainedModels;
    }

    public LinkedList<Model> getModels (){return  models; }
    public void setPapersRead(int add){papersRead = papersRead + add; }
    public Degree getStatus(){return status;}

    public String getDepartment() {
        return department;
    }

    public int getPublications() {
        return publications;
    }

    public int getPapersRead() {
        return papersRead;
    }

    public String getName() {
        return name;
    }

    public void terminate(){
        papersRead = papersRead - publications;
        terminated = true;
    }

    public String toString(){
        String output = name+"\n"+department+"\n"+status+"\n"+publications+"\n"+papersRead+"\n"+trainedModels;
        return output;
    }







}
