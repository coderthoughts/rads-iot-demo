package org.coderthoughts.radsiotdemo;

import java.util.Date;
import java.util.Map;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {

    private ServiceTracker<Map<String, Date>, Map<String, Date>> st;
    private DeviceRegistrar registrar;
    private RadController[] rads;

    @Override
    public void start(BundleContext context) throws Exception {
        RadController rad1 = new RadController(context, "rad1", "(dal.function.UID=pir1_motion)");
        rads = new RadController[] { rad1 };

        Filter filter = context.createFilter("(&(objectClass=java.util.Map)(org.coderthoughts.recordingservlet=*))");

        registrar = new DeviceRegistrar(context);
        st = new ServiceTracker<Map<String,Date>,Map<String,Date>>(context, filter, registrar);
        st.open();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        st.close();
        registrar.destroy();
        for (RadController rad : rads) {
            rad.destroy();
        }

    }
}
