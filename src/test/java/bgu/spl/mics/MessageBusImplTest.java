package bgu.spl.mics;

import bgu.spl.mics.application.services.StudentService;
import bgu.spl.mics.application.services.TimeService;
import bgu.spl.mics.example.messages.ExampleBroadcast;
import bgu.spl.mics.example.messages.ExampleEvent;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MessageBusImplTest {
    private static MessageBusImpl bus;
    private static MicroService m;
    private static ExampleBroadcast broadcast;
    private static ExampleEvent event;

    @Before
    public void setUp() throws Exception {
        bus = MessageBusImpl.getInstace();
        event = new ExampleEvent("x");
        broadcast = new ExampleBroadcast("y");
        m = new TimeService(1000,5);
    }

    @Test
    public void subscribeEvent() {
        bus.register(m);
        assertFalse(bus.isEventRegisterered(m,event));
        bus.subscribeEvent(event.getClass(),m);
        assertTrue(bus.isEventRegisterered(m,event));
        bus.unregister(m);  // in order to not interfere with other tests

    }

    @Test
    public void subscribeBroadcast() {
        bus.register(m);
        assertFalse(bus.isBroadcastRegisterered(m,broadcast));
        bus.subscribeBroadcast(broadcast.getClass(),m);
        assertTrue(bus.isBroadcastRegisterered(m,broadcast));
        bus.unregister(m);  // in order to not interfere with other tests
        }

    @Test
    public void complete() {
        assertThrows("expected exception: microservice has not registered and subscribed to Event",Exception.class,() -> bus.complete(event,"result") );
        bus.register(m);
        assertThrows("expected exception: microservice has not subscribed to Event",Exception.class,() -> bus.complete(event,"result"));
        bus.subscribeEvent(event.getClass(),m);
        assertThrows("expected exception: event has not been sent",Exception.class,() -> bus.complete(event,"result"));
        bus.sendEvent(event);
        bus.complete(event,"result");
        bus.unregister(m); // in order to not interfere with other tests
    }

    @Test
    public void sendBroadcast() {
        assertThrows("expected exception: there is no microservice registered to the broadcast",Exception.class,() -> bus.sendBroadcast(broadcast));
        bus.register(m);
        bus.subscribeBroadcast(broadcast.getClass(),m);
        bus.sendBroadcast(broadcast);
        bus.unregister(m); // in order to not interfere with other tests
    }

    @Test
    public void sendEvent() {
        assertThrows("expected exception: there is no microservice registered to the event",Exception.class,() -> bus.sendEvent(event));
        bus.register(m);
        bus.subscribeEvent(event.getClass(),m);
        bus.sendEvent(event);
        bus.unregister(m); // in order to not interfere with other tests
    }

    @Test
    public void register() {
        assertFalse(bus.isRegistered(m));
        bus.register(m);
        assertTrue(bus.isRegistered(m));
        bus.unregister(m); // in order to not interfere with other tests
    }

    @Test
    public void unregister() {
        bus.register(m);
        assertTrue(bus.isRegistered(m));
        bus.unregister(m);
        assertFalse(bus.isRegistered(m));
    }

    @Test
    public void awaitMessage() throws InterruptedException {
        bus.register(m);
        bus.subscribeEvent(event.getClass(),m);
        bus.sendEvent(event);
        assertEquals("expected message to be equal to message sent",event,bus.awaitMessage(m));
        bus.unregister(m); // in order to not interfere with other tests


    }

}