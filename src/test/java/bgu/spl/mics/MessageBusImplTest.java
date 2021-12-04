package bgu.spl.mics;

import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.services.StudentService;
import bgu.spl.mics.example.messages.ExampleBroadcast;
import bgu.spl.mics.example.messages.ExampleEvent;
import bgu.spl.mics.example.services.ExampleBroadcastListenerService;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MessageBusImplTest {
    private static MessageBus bus;
    private static MicroService m;
    private static ExampleBroadcast broadcast;
    private static ExampleEvent event;

    @Before
    public void setUp() throws Exception {
        bus = MessageBusImpl.getInstace();
        event = new ExampleEvent("x");
        broadcast = new ExampleBroadcast("y");
        m = new StudentService("nir");
    }

    @Test
    public void subscribeEvent() {
        try{
            bus.subscribeEvent(event.getClass(),m);

        } catch(Exception e){

        }





    }

    @Test
    public void subscribeBroadcast() {
    }

    @Test
    public void complete() {
    }

    @Test
    public void sendBroadcast() {
    }

    @Test
    public void sendEvent() {
    }

    @Test
    public void register() {
    }

    @Test
    public void unregister() {
    }

    @Test
    public void awaitMessage() {
    }

    @Test
    public void isRegisterBroadcast() {
    }

    @Test
    public void isRegisterEvent() {
    }

    @Test
    public void getInstace() {
    }
}