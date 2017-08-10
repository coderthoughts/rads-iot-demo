package org.coderthoughts.radsiotdemo;

import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.dal.Function;
import org.osgi.service.dal.functions.BooleanSensor;
import org.osgi.service.dal.functions.Types;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class DeviceRegistrar implements ServiceTrackerCustomizer<Map<String, Date>, Map<String, Date>> {
    private final BundleContext bundleContext;
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
