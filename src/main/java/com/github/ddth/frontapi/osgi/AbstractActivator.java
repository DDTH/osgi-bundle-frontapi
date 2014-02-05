package com.github.ddth.frontapi.osgi;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An abstract implementation of {@link BundleActivator}. Bundle can extend this
 * class as a starting point to implement its bundle activator.
 * 
 * @author Thanh Ba Nguyen <bnguyen2k@gmail.com>
 * @since 0.1.0
 */
public abstract class AbstractActivator implements BundleActivator {

    private Logger LOGGER = LoggerFactory.getLogger(AbstractActivator.class);

    private BundleContext bundleContext;
    @SuppressWarnings("rawtypes")
    private List<ServiceRegistration> registeredServices = new ArrayList<ServiceRegistration>();

    /**
     * Gets the {@link BundleContext} instance.
     * 
     * @return
     */
    protected BundleContext bundleContext() {
        return this.bundleContext;
    }

    /**
     * Sub-class overrides this method to implement its initializing business.
     * 
     * <p>
     * This method is called by {@link #start(BundleContext)} when the bundle is
     * starting.
     * </p>
     * 
     * @throws Exception
     *             throws an exception to indicate that initializing process has
     *             failed
     */
    protected abstract void init() throws Exception;

    /**
     * Sub-class overrides this method to implement its destructing process.
     * 
     * <p>
     * This method is called by:
     * </p>
     * <ul>
     * <li>{@link #start(BundleContext)} if {@link #init()} throws exception.</li>
     * <li>{@link #stop(BundleContext)} when the bundle is stopping.</li>
     * </ul>
     * 
     * @throws Exception
     */
    protected abstract void destroy() throws Exception;

    @SuppressWarnings("rawtypes")
    private void _destroy() throws Exception {
        for (ServiceRegistration sr : registeredServices) {
            try {
                sr.unregister();
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        registeredServices.clear();
        destroy();
    }

    /**
     * Registers a service.
     * 
     * @param clazz
     * @param service
     * @param props
     * @return
     */
    @SuppressWarnings("rawtypes")
    protected <S> ServiceRegistration registerService(Class<S> clazz, S service,
            Map<String, ?> props) {
        Dictionary<String, Object> _p = new Hashtable<String, Object>();
        if (props != null) {
            for (Entry<String, ?> entry : props.entrySet()) {
                _p.put(entry.getKey(), entry.getValue());
            }
        }
        ServiceRegistration sr = bundleContext.registerService(clazz, service, _p);
        registeredServices.add(sr);
        return sr;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(BundleContext context) throws Exception {
        this.bundleContext = context;
        try {
            init();
        } catch (Exception e) {
            _destroy();
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        _destroy();
    }
}
