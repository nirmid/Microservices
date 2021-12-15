package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

import java.util.LinkedList;

public class PublishResultsEvent implements Event {
    private LinkedList<String> modelNames;

    public PublishResultsEvent(){
        modelNames = new LinkedList<String>();
    }
    public void addModelName(String name){
        synchronized (modelNames){
            modelNames.add(name);
        }
    }

}
