package com.github.ddth.frontapi.internal;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ddth.frontapi.IApiRegistry;
import com.github.ddth.frontapi.impl.ApiRegistry;
import com.github.ddth.frontapi.impl.ApiServlet;
import com.github.ddth.frontapi.osgi.AbstractActivator;

public class Activator extends AbstractActivator {

    public final static String MODULE_NAME = "frontapi";

    public final static String CONFIG_FILE = "/com/github/ddth/frontapi/frontapi.properties";
    public final static String PROP_REST_MAPPING = "frontapi.rest.mapping";

    private final static String ATTR_MAPPING = "mapping";
    public final static String DEFAULT_REST_MAPPING = "/api";

    private final Logger LOGGER = LoggerFactory.getLogger(Activator.class);

    private Properties props;
    private ApiRegistry apiRegistry;
    private ServiceTracker serviceTracker;

    private void initProperties() throws IOException {
        props = new Properties();
        InputStream is = this.getClass().getResourceAsStream(CONFIG_FILE);
        try {
            props.load(is);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    private void initApiRegistry() {
        apiRegistry = new ApiRegistry();
        apiRegistry.init();
        Map<String, Object> props = new HashMap<String, Object>();
        props.put("Module", MODULE_NAME);
        registerService(IApiRegistry.class, apiRegistry, props);
    }

    private void destroyApiRegistry() {
        if (apiRegistry != null) {
            apiRegistry.destroy();
        }
        apiRegistry = null;
    }

    private void initRestServer() {
        serviceTracker = new ServiceTracker(bundleContext(), HttpService.class.getName(), null) {
            @SuppressWarnings({ "rawtypes", "unchecked" })
            @Override
            public void remove(ServiceReference reference) {
                try {
                    Object mapping = reference.getProperty(ATTR_MAPPING);
                    if (mapping != null) {
                        HttpService service = (HttpService) this.context.getService(reference);
                        if (service != null) {
                            service.unregister(mapping.toString());
                        }
                    }
                } catch (IllegalArgumentException exception) {
                    // Ignore; servlet registration probably failed earlier
                    // on...
                }
            }

            @SuppressWarnings({ "rawtypes", "unchecked" })
            @Override
            public Object addingService(ServiceReference reference) {
                HttpService httpService = (HttpService) this.context.getService(reference);
                try {
                    final String restApiServletMapping = props.getProperty(PROP_REST_MAPPING,
                            DEFAULT_REST_MAPPING);
                    Hashtable<String, String> props = new Hashtable<String, String>();
                    props.put("mapping", restApiServletMapping);
                    httpService.registerServlet(restApiServletMapping, new ApiServlet(apiRegistry,
                            restApiServletMapping), props, null);
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
                return httpService;
            }
        };
        // start tracking all HTTP services...
        serviceTracker.open();
    }

    private void destroyRestServer() {
        if (serviceTracker != null) {
            serviceTracker.close();
            serviceTracker = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void init() throws Exception {
        initProperties();
        initApiRegistry();
        initRestServer();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void destroy() throws Exception {
        try {
            destroyRestServer();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        try {
            destroyApiRegistry();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}
