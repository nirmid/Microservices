package bgu.spl.mics.application.objects;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Passive object representing information on a conference.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class ConfrenceInformation {

    private String name;
    private int date;
    private ConcurrentLinkedQueue<Model> models;
    private int numOfPublications;

    public ConfrenceInformation(String name,int date){
        this.name = name;
        this.date = date;
        models = new ConcurrentLinkedQueue<Model>();
        numOfPublications = 0;
    }

    public void addModel(Model model){
        models.add(model);
        numOfPublications = numOfPublications +1;
    }

    public int getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public ConcurrentLinkedQueue<Model> getModels() {
        return models;
    }

    public int getNumOfPublications() {
        return numOfPublications;
    }

    public String toString(){
        String output = "conference information: "+name+" , "+date+" , "+models.toString()+" , "+numOfPublications;
        return output;
    }
}
