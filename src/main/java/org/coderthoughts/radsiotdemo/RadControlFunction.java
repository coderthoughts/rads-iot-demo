package org.coderthoughts.radsiotdemo;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Dictionary;

import org.osgi.service.dal.DeviceException;
import org.osgi.service.dal.OperationMetadata;
import org.osgi.service.dal.PropertyMetadata;
import org.osgi.service.dal.functions.MultiLevelControl;
import org.osgi.service.dal.functions.data.LevelData;

public class RadControlFunction implements MultiLevelControl {
    private final Dictionary<String, ?> serviceProps;
    private final String uid;
    private volatile LevelData data;

    public RadControlFunction(String deviceUID, Dictionary<String, Object> props) {
        uid = deviceUID;
        serviceProps = props;
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
    }
}
