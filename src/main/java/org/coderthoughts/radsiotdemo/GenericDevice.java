package org.coderthoughts.radsiotdemo;

import java.util.Collections;
import java.util.Dictionary;

import org.osgi.service.dal.Device;
import org.osgi.service.dal.DeviceException;

public class GenericDevice implements Device {
    private Dictionary<String, ?> properties;

    GenericDevice() {
    }

    @Override
    public Object getServiceProperty(String propKey) {
        return properties.get(propKey);
    }

    @Override
    public String[] getServicePropertyKeys() {
        return Collections.list(properties.keys()).toArray(new String [] {});
    }

    @Override
    public void remove() throws DeviceException {
    }

    void setProperties(Dictionary<String, ?> props) {
        properties = props;
    }
}
