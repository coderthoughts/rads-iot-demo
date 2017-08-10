package org.coderthoughts.radsiotdemo;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleContext;
import org.osgi.service.dal.Device;
import org.osgi.service.dal.Function;
import org.osgi.service.dal.functions.MultiLevelControl;
import org.osgi.service.dal.functions.Types;

public class RadiatorDevice extends GenericDevice {
    RadiatorDevice(BundleContext context, String deviceUID) {
        registerFunction(context, deviceUID);
        registerDevice(context, deviceUID);
    }

    private void registerDevice(BundleContext context, String deviceUID) {
        String driverName = "Z-Wave";
        Dictionary<String, Object> props = new Hashtable<>();
        props.put(Device.SERVICE_UID, driverName + ":" + deviceUID);
        props.put(Device.SERVICE_DRIVER, driverName);
        props.put(Device.SERVICE_STATUS, Device.STATUS_ONLINE);
        props.put(Device.SERVICE_TYPES, new String [] { "Radiator Thermostat" });
        context.registerService(Device.class, new GenericDevice(props), props);
    }

    private void registerFunction(BundleContext context, String deviceUID) {
        Dictionary<String, Object> props = new Hashtable<>();
        props.put(Function.SERVICE_UID, deviceUID + ":setpoint");
        props.put(Function.SERVICE_TYPE, Types.TEMPERATURE);
        props.put(Function.SERVICE_DEVICE_UID, deviceUID);
        props.put(Function.SERVICE_PROPERTY_NAMES, new String [] {MultiLevelControl.PROPERTY_DATA});
        RadiatorControlFunction func = new RadiatorControlFunction(deviceUID, props);
        context.registerService(Function.class, func, props);
    }

    public void destroy() {
    }
}
