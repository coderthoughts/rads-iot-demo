package org.coderthoughts.radsiotdemo;

import java.math.BigDecimal;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.dal.DeviceException;
import org.osgi.service.dal.Function;
import org.osgi.service.dal.FunctionEvent;
import org.osgi.service.dal.functions.MultiLevelControl;
import org.osgi.service.dal.functions.data.BooleanData;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.util.tracker.ServiceTracker;
import org.osoco.software.iot.demo.gateway.api.RoomService;

public class RadsController implements EventHandler {
    Map<String, MultiLevelControl> rads = new ConcurrentHashMap<>();
    private ServiceTracker<Function, Function> st;
    private ServiceTracker<RoomService, RoomService> rsst;

    RadsController(BundleContext context) {
        Dictionary<String, Object> props = new Hashtable<>();
        props.put(EventConstants.EVENT_TOPIC, new String[] {FunctionEvent.TOPIC_PROPERTY_CHANGED});
        context.registerService(EventHandler.class, this, props);

        st = new ServiceTracker<Function, Function>(context, Function.class, null) {
            @Override
            public Function addingService(ServiceReference<Function> reference) {
                Function svc = super.addingService(reference);
                Object uid = reference.getProperty(Function.SERVICE_DEVICE_UID);

                if (uid instanceof String && svc instanceof MultiLevelControl) {
                    rads.put((String) uid, (MultiLevelControl) svc);
                }
                return svc;
            }
        };
        st.open();

        rsst = new ServiceTracker<RoomService, RoomService>(context, RoomService.class, null);
        rsst.open();
    }

    public void destroy() {
        st.close();
        rsst.close();
    }

    @Override
    public void handleEvent(Event event) {
        System.out.println("*** Received update" + event);
        String fuid = getStringProperty(event, FunctionEvent.FUNCTION_UID);

        Integer roomID = null;
        switch (fuid) {
        case "pir1:motion":
            roomID = 1;
            break;
        case "pir2:motion":
            roomID = 2;
            break;
        }

        BooleanData data = getDataProperty(event);
        int temp = data.getValue() ? 21 : 7;

        if (roomID != null) {
            RoomService rs = rsst.getService();
            if (rs != null) {
                rs.reportMotion(roomID);
                Entry<Float, Integer> suggestion = rs.getHeatingSuggestion(roomID);
                temp = suggestion.getValue();
            }

            MultiLevelControl rad = rads.get(getRadFromRoomID(roomID));
            if (rad != null) {
                try {
                    rad.setData(new BigDecimal(temp), "degrees");
                } catch (DeviceException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String getRadFromRoomID(int roomID) {
        switch (roomID) {
        case 1: return "radA";
        case 2: return "radB";
        }
        return null;
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
}
