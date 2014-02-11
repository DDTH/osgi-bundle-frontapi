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
import com.github.ddth.frontapi.internal.thrift.ThriftApiServer;
import com.github.ddth.frontapi.osgi.AbstractActivator;
import com.github.ddth.frontapi.osgi.Constants;

public class Activator extends AbstractActivator {

    public final static String MODULE_NAME = "frontapi";

    public final static String CONFIG_FILE = "/com/github/ddth/frontapi/frontapi.properties";
    public final static String PROP_REST_MAPPING = "frontapi.rest.mapping";

    public final static String PROP_THRIFT_SERVER_ENABLED = "frontapi.thrift.enabled";
    public final static String PROP_THRIFT_SERVER_PORT = "frontapi.thrift.port";
    public final static String PROP_THRIFT_MAX_FRAME_SIZE = "frontapi.thrift.max_frame_size";
    public final static String PROP_THRIFT_MAX_READ_BUFFER_SIZE = "frontapi.thrift.max_read_buffer_size";
    public final static String PROP_THRIFT_CLIENT_TIMEOUT = "frontapi.thrift.client_timeout";

    private final static String ATTR_MAPPING = "mapping";
    public final static String DEFAULT_REST_MAPPING = "/api";

    public final static String DEFAULT_THRIFT_SERVER_ENABLED = "false";

    private final Logger LOGGER = LoggerFactory.getLogger(Activator.class);

    private Properties props;
    private ApiRegistry apiRegistry;
    private ServiceTracker serviceTracker;
    private ThriftApiServer thriftApiServer;

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
        props.put(Constants.LOOKUP_PROP_MODULE, MODULE_NAME);
        registerService(IApiRegistry.class, apiRegistry, props);
    }

    private void destroyApiRegistry() {
        if (apiRegistry != null) {
            apiRegistry.destroy();
        }
        apiRegistry = null;
    }

    private void initThriftServer() {
        boolean thriftServerEnabled = false;
        try {
            thriftServerEnabled = Boolean.parseBoolean(props.getProperty(
                    PROP_THRIFT_SERVER_ENABLED, DEFAULT_THRIFT_SERVER_ENABLED));
        } catch (Exception e) {
            thriftServerEnabled = false;
        }
        if (thriftServerEnabled) {
            int thriftPort = ThriftApiServer.DEFAULT_THRIFT_SERVER_PORT;
            try {
                thriftPort = Integer.parseInt(props.getProperty(PROP_THRIFT_SERVER_PORT,
                        String.valueOf(ThriftApiServer.DEFAULT_THRIFT_SERVER_PORT)));
            } catch (Exception e) {
                thriftPort = ThriftApiServer.DEFAULT_THRIFT_SERVER_PORT;
            }
            LOGGER.info("API Thrift Server enabled, port " + thriftPort + ".");

            thriftApiServer = new ThriftApiServer(apiRegistry);
            int thriftMaxFrameSize = Integer.parseInt(props.getProperty(PROP_THRIFT_MAX_FRAME_SIZE,
                    String.valueOf(ThriftApiServer.DEFAULT_THRIFT_MAX_FRAME_SIZE)));
            long thriftMaxReadBufferSize = Long.parseLong(props.getProperty(
                    PROP_THRIFT_MAX_READ_BUFFER_SIZE,
                    String.valueOf(ThriftApiServer.DEFAULT_THRIFT_MAX_READ_BUFFER_SIZE)));
            int thriftClientTimeout = Integer.parseInt(props.getProperty(
                    PROP_THRIFT_CLIENT_TIMEOUT,
                    String.valueOf(ThriftApiServer.DEFAULT_THRIFT_CLIENT_TIMEOUT)));
            thriftApiServer.setClientTimeoutMillisecs(thriftClientTimeout)
                    .setMaxFrameSize(thriftMaxFrameSize)
                    .setMaxReadBufferSize(thriftMaxReadBufferSize).setPort(thriftPort);
            thriftApiServer.start();
        } else {
            LOGGER.info("API Thrift Server disabled.");
        }
    }

    private void destroyThriftServer() {
        if (thriftApiServer != null) {
            thriftApiServer.destroy();
            thriftApiServer = null;
        }
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
        initThriftServer();
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
            destroyThriftServer();
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
