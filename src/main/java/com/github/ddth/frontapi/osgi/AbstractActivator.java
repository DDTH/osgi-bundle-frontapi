package com.github.ddth.frontapi.osgi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ddth.frontapi.internal.VersionUtils;

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
     * Looks up an OSGi service.
     * 
     * <p>
     * This method unregisters the {@link ServiceReference} so caller does not
     * need to do it.
     * </p>
     * 
     * @param clazz
     * @return
     * @since 0.1.1
     */
    protected <T> T lookupService(Class<T> clazz) {
        ServiceReference<T> sref = lookupServiceReference(clazz);
        if (sref != null) {
            try {
                return getService(sref, clazz);
            } finally {
                ungetServiceReference(sref);
            }
        }
        return null;
    }

    /**
     * Looks up an OSGi service.
     * 
     * <p>
     * This method unregisters the {@link ServiceReference} so caller does not
     * need to do it.
     * </p>
     * 
     * @param clazz
     * @param filter
     * @return
     * @since 0.1.1
     */
    protected <T> T lookupService(Class<T> clazz, Map<String, String> filter) {
        ServiceReference<T> sref = lookupServiceReference(clazz, filter);
        if (sref != null) {
            try {
                return getService(sref, clazz);
            } finally {
                ungetServiceReference(sref);
            }
        }
        return null;
    }

    /**
     * Looks up an OSGi service.
     * 
     * <p>
     * This method unregisters the {@link ServiceReference} so caller does not
     * need to do it.
     * </p>
     * 
     * @param clazz
     * @param filter
     * @return
     * @since 0.1.1
     */
    protected <T> T lookupService(Class<T> clazz, Properties filter) {
        ServiceReference<T> sref = lookupServiceReference(clazz, filter);
        if (sref != null) {
            try {
                return getService(sref, clazz);
            } finally {
                ungetServiceReference(sref);
            }
        }
        return null;
    }

    /**
     * Gets the service instance from a service reference.
     * 
     * @param serviceRef
     * @return
     * @since 0.1.1
     */
    protected Object getService(ServiceReference<?> serviceRef) {
        if (serviceRef != null) {
            return bundleContext.getService(serviceRef);
        }
        return null;
    }

    /**
     * Gets the service instance from a service reference.
     * 
     * @param serviceRef
     * @param clazz
     * @return
     * @since 0.1.1
     */
    @SuppressWarnings("unchecked")
    public <T> T getService(ServiceReference<?> serviceRef, Class<T> clazz) {
        Object service = getService(serviceRef);
        if (service != null && clazz.isAssignableFrom(service.getClass())) {
            return (T) service;
        }
        return null;
    }

    /**
     * "Unget" a service reference.
     * 
     * @param serviceRef
     * @since 0.1.1
     */
    protected void ungetServiceReference(ServiceReference<?> serviceRef) {
        if (serviceRef != null) {
            bundleContext.ungetService(serviceRef);
        }
    }

    /**
     * Looks up an OSGi service reference.
     * 
     * @param clazz
     * @return
     * @since 0.1.1
     */
    protected <T> ServiceReference<T> lookupServiceReference(Class<T> clazz) {
        return lookupServiceReference(clazz, (String) null);
    }

    /**
     * Looks up an OSGi service reference.
     * 
     * @param clazz
     * @param filter
     * @return
     * @since 0.1.1
     */
    protected <T> ServiceReference<T> lookupServiceReference(Class<T> clazz, Properties filter) {
        if (filter == null || filter.size() == 0) {
            return lookupServiceReference(clazz, (String) null);
        }
        StringBuilder query = new StringBuilder("(&");
        for (Entry<Object, Object> entry : filter.entrySet()) {
            query.append("(");
            query.append(entry.getKey().toString());
            query.append("=");
            query.append(entry.getValue().toString());
            query.append(")");
        }
        query.append(")");
        return lookupServiceReference(clazz, query.toString());
    }

    /**
     * Looks up an OSGi service reference.
     * 
     * @param clazz
     * @param filter
     * @return
     * @since 0.1.1
     */
    protected <T> ServiceReference<T> lookupServiceReference(Class<T> clazz, Map<?, ?> filter) {
        if (filter == null || filter.size() == 0) {
            return lookupServiceReference(clazz, (String) null);
        }
        StringBuilder query = new StringBuilder("(&");
        for (Entry<?, ?> entry : filter.entrySet()) {
            query.append("(");
            query.append(entry.getKey().toString());
            query.append("=");
            query.append(entry.getValue().toString());
            query.append(")");
        }
        query.append(")");
        return lookupServiceReference(clazz, query.toString());
    }

    /**
     * Looks up an OSGi service reference.
     * 
     * @param clazz
     * @param filter
     * @return
     * @since 0.1.1
     */
    @SuppressWarnings("unchecked")
    protected <T> ServiceReference<T> lookupServiceReference(Class<T> clazz, String filter) {
        final ServiceReference<T>[] EMPTY_ARRAY = new ServiceReference[0];
        Collection<ServiceReference<T>> _refs = lookupServiceReferences(clazz, filter);
        ServiceReference<T>[] refs = _refs != null ? _refs.toArray(EMPTY_ARRAY) : EMPTY_ARRAY;
        ServiceReference<T> ref = (refs != null && refs.length > 0) ? refs[0] : null;
        for (int i = 1, n = refs != null ? refs.length : 0; i < n; i++) {
            ServiceReference<T> temp = refs[i];
            Object v1 = ref.getProperty(Constants.LOOKUP_PROP_VERSION);
            Object v2 = temp.getProperty(Constants.LOOKUP_PROP_VERSION);
            if (VersionUtils.compareVersions(v1 != null ? v1.toString() : null,
                    v2 != null ? v2.toString() : null) < 0) {
                // unget unmatched service reference
                ungetServiceReference(ref);
                ref = temp;
            } else {
                // unget unmatched service reference
                ungetServiceReference(temp);
            }
        }
        return ref;
    }

    /**
     * Looks up OSGi service references.
     * 
     * @param clazz
     * @param filter
     * @return
     * @since 0.1.1
     */
    protected <T> Collection<ServiceReference<T>> lookupServiceReferences(Class<T> clazz,
            Properties filter) {
        if (filter == null || filter.size() == 0) {
            return lookupServiceReferences(clazz, (String) null);
        }
        StringBuilder query = new StringBuilder("(&");
        for (Entry<Object, Object> entry : filter.entrySet()) {
            query.append("(");
            query.append(entry.getKey().toString());
            query.append("=");
            query.append(entry.getValue().toString());
            query.append(")");
        }
        query.append(")");
        return lookupServiceReferences(clazz, query.toString());
    }

    /**
     * Looks up OSGi service references.
     * 
     * @param clazz
     * @param filter
     * @return
     * @since 0.1.1
     */
    protected <T> Collection<ServiceReference<T>> lookupServiceReferences(Class<T> clazz,
            Map<?, ?> filter) {
        if (filter == null || filter.size() == 0) {
            return lookupServiceReferences(clazz, (String) null);
        }
        StringBuilder query = new StringBuilder("(&");
        for (Entry<?, ?> entry : filter.entrySet()) {
            query.append("(");
            query.append(entry.getKey().toString());
            query.append("=");
            query.append(entry.getValue().toString());
            query.append(")");
        }
        query.append(")");
        return lookupServiceReferences(clazz, query.toString());
    }

    /**
     * Looks up OSGi service references.
     * 
     * @param clazz
     * @param filter
     * @return
     * @since 0.1.1
     */
    protected <T> Collection<ServiceReference<T>> lookupServiceReferences(Class<T> clazz,
            String filter) {
        Collection<ServiceReference<T>> result = new ArrayList<ServiceReference<T>>();
        if (filter == null) {
            ServiceReference<T> serviceRef = bundleContext.getServiceReference(clazz);
            if (serviceRef != null) {
                result.add(serviceRef);
            }
        } else {
            try {
                result = bundleContext.getServiceReferences(clazz, filter);
            } catch (InvalidSyntaxException e) {
                LOGGER.error(
                        "Can not get service reference [" + clazz + "/" + filter + "]: "
                                + e.getMessage(), e);
            }
        }
        return result != null && result.size() > 0 ? result : null;
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
