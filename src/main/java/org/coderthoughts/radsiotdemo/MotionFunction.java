package org.coderthoughts.radsiotdemo;

import java.util.Collections;
import java.util.Dictionary;

import org.osgi.framework.BundleContext;
import org.osgi.service.dal.Function;
import org.osgi.service.dal.FunctionEvent;
import org.osgi.service.dal.OperationMetadata;
import org.osgi.service.dal.PropertyMetadata;
import org.osgi.service.dal.functions.BooleanSensor;
import org.osgi.service.dal.functions.data.BooleanData;
import org.osgi.service.event.EventAdmin;
import org.osgi.util.tracker.ServiceTracker;

public class MotionFunction implements BooleanSensor {
    private volatile BooleanData data;
    private volatile Dictionary<String, ?> serviceProps;
    private final ServiceTracker<EventAdmin,EventAdmin> st;

    MotionFunction(BundleContext bc) {
        st = new ServiceTracker<EventAdmin,EventAdmin>(bc, EventAdmin.class, null);
        st.open();
    }

    void destroy() {
        st.close();
    }

    @Override
    public PropertyMetadata getPropertyMetadata(String propertyName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public OperationMetadata getOperationMetadata(String operationName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getServiceProperty(String propKey) {
        return serviceProps.get(propKey);
    }

    @Override
    public String[] getServicePropertyKeys() {
        return Collections.list(serviceProps.keys()).toArray(new String [] {});
    }

    public BooleanData getData() {
        return data;
    }

    void setData(boolean d) {
        data = new BooleanData(System.currentTimeMillis(), null, d);

        EventAdmin ea = st.getService();
        if (ea != null) {
            FunctionEvent event = new FunctionEvent(FunctionEvent.TOPIC_PROPERTY_CHANGED,
                    getServiceProperty(Function.SERVICE_UID).toString(),
                    BooleanSensor.PROPERTY_DATA, data);
            ea.postEvent(event);
        }
    }

    void setProperties(Dictionary<String, ?> props) {
        serviceProps = props;
    }
}
