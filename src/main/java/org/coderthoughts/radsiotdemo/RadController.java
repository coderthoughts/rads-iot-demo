package org.coderthoughts.radsiotdemo;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleContext;
import org.osgi.service.dal.FunctionEvent;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

public class RadController implements EventHandler {
    private final String radID;

    RadController(BundleContext context, String radID) {
        this.radID = radID;

        Dictionary<String, Object> props = new Hashtable<>();
        props.put(EventConstants.EVENT_TOPIC, new String[] {FunctionEvent.TOPIC_PROPERTY_CHANGED});
        context.registerService(EventHandler.class, this, props);
    }

    @Override
    public void handleEvent(Event event) {
        System.out.println(radID + "*** Received update" + event);
    }

    public void destroy() {
        // TODO Auto-generated method stub

    }
}
