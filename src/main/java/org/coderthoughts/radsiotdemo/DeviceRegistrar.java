package org.coderthoughts.radsiotdemo;

import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.dal.Device;
import org.osgi.service.dal.Function;
import org.osgi.service.dal.functions.BooleanSensor;
import org.osgi.service.dal.functions.Types;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class DeviceRegistrar implements ServiceTrackerCustomizer<Map<String, Date>, Map<String, Date>> {
    private final BundleContext bundleContext;
    private final Map<String, Device> devices = new ConcurrentHashMap<>();
    private final Map<String, MotionFunction> functions = new ConcurrentHashMap<>();

    public DeviceRegistrar(BundleContext context) {
        bundleContext = context;
    }

    void destroy() {
        for (MotionFunction mf : functions.values()) {
            mf.destroy();
        }
    }

    @Override
    public Map<String, Date> addingService(ServiceReference<Map<String, Date>> reference) {
        Map<String, Date> svc = bundleContext.getService(reference);
        modifiedService(reference, svc);
        return svc;
    }

    @Override
    public void modifiedService(ServiceReference<Map<String, Date>> reference, Map<String, Date> service) {
        Object pir1Val = reference.getProperty("pir1");
        if (pir1Val instanceof String) {
            String pir1State = (String) pir1Val;

            MotionFunction mf = getMotionFunction("pir1");
            mf.setData("on".equalsIgnoreCase(pir1State));

            getDevice("ESP8266", "pir1");
        }
    }

    private Device getDevice(String driverName, String deviceUID) {
        GenericDevice device = new GenericDevice();
        Device od = devices.putIfAbsent(deviceUID, device);
        if (od != null) {
            return od;
        } else {
            Dictionary<String, Object> props = new Hashtable<>();
            props.put(Device.SERVICE_UID, driverName + ":" + deviceUID);
            props.put(Device.SERVICE_DRIVER, driverName);
            props.put(Device.SERVICE_STATUS, Device.STATUS_ONLINE);
            props.put(Device.SERVICE_TYPES, new String [] { "PIR Sensor" } );
            device.setProperties(props);
            bundleContext.registerService(Device.class, device, props);
            return device;
        }
    }

    private MotionFunction getMotionFunction(String deviceUID) {
        MotionFunction func = new MotionFunction(bundleContext);
        MotionFunction of = functions.putIfAbsent(deviceUID, func);
        if (of != null) {
            return of;
        } else {
            Dictionary<String, Object> props = new Hashtable<>();
            props.put(Function.SERVICE_UID, deviceUID + "_motion");
            props.put(Function.SERVICE_TYPE, Types.MOTION);
            props.put(Function.SERVICE_DEVICE_UID, deviceUID);
            props.put(Function.SERVICE_PROPERTY_NAMES, new String [] {BooleanSensor.PROPERTY_DATA});
            func.setProperties(props);
            bundleContext.registerService(Function.class, func, props);
            return func;
        }
    }

    @Override
    public void removedService(ServiceReference<Map<String, Date>> reference, Map<String, Date> service) {
    }
}
