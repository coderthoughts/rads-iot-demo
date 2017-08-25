package org.coderthoughts.radsiotdemo;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Collections;
import java.util.Dictionary;

import org.osgi.service.dal.DeviceException;
import org.osgi.service.dal.OperationMetadata;
import org.osgi.service.dal.PropertyMetadata;
import org.osgi.service.dal.functions.MultiLevelControl;
import org.osgi.service.dal.functions.data.LevelData;

public class RadiatorControlFunction implements MultiLevelControl {
    private final Dictionary<String, ?> serviceProps;
    private final String uid;
    private final String deviceIP;
    private volatile LevelData data;

    public RadiatorControlFunction(String deviceUID, Dictionary<String, Object> props) {
        uid = deviceUID;
        serviceProps = props;

        deviceIP = deviceUID.equals("radA") ? "172.24.1.22" : "172.24.1.23";
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

    @Override
    public LevelData getData() throws DeviceException {
        return data;
    }

    @Override
    public void setData(BigDecimal level, String unit) throws DeviceException {
        System.out.println("*** Setting radiator " + uid + " to " + level + " " + unit);
        data = new LevelData(System.currentTimeMillis(), null, level, unit);

        String url = "http://" + deviceIP + "/" + (level.intValue() > 15 ? "on" : "off");
        try {
            InputStream is = new URL(url).openStream();
            Streams.suck(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
