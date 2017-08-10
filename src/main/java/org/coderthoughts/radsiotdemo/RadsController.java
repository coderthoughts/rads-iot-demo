package org.coderthoughts.radsiotdemo;

import java.math.BigDecimal;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;
import org.osgi.service.dal.DeviceException;
import org.osgi.service.dal.FunctionEvent;
import org.osgi.service.dal.functions.MultiLevelControl;
import org.osgi.service.dal.functions.data.BooleanData;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

public class RadsController implements EventHandler {
    Map<String, MultiLevelControl> rads = new ConcurrentHashMap<>();

    RadsController(BundleContext context) {
        Dictionary<String, Object> props = new Hashtable<>();
        props.put(EventConstants.EVENT_TOPIC, new String[] {FunctionEvent.TOPIC_PROPERTY_CHANGED});
        context.registerService(EventHandler.class, this, props);
    }

    @Override
    public void handleEvent(Event event) {
        System.out.println("*** Received update" + event);
        String fuid = getStringProperty(event, FunctionEvent.FUNCTION_UID);

        String radiator = null;
        switch (fuid) {
        case "pir1:motion":
            radiator = "rad1";
            break;
        case "pir2:motion":
            radiator = "rad2";
            break;
        }

        BooleanData data = getDataProperty(event);
        int temp = data.getValue() ? 21 : 7;

        MultiLevelControl rad = rads.get(radiator);
        if (rad != null) {
            try {
                rad.setData(new BigDecimal(temp), "degrees");
            } catch (DeviceException e) {
                e.printStackTrace();
            }
        }
    }

    private BooleanData getDataProperty(Event event) {
        Object data = event.getProperty(FunctionEvent.PROPERTY_VALUE);
        if (data instanceof BooleanData) {
            return (BooleanData) data;
        }
        return null;
    }

    private String getStringProperty(Event event, String propName) {
        Object uid = event.getProperty(propName);
        if (uid instanceof String) {
            return (String) uid;
        }
        return null;
    }

    public void destroy() {
    }
}
