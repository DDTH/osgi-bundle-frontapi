package com.github.ddth.frontapi.internal.thrift;

import org.apache.thrift.TProcessor;
import org.apache.thrift.TProcessorFactory;

/**
 * Abstract implementation of {@link IServerFactory}.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public abstract class AbstractServerFactory implements IServerFactory {

    private int port;
    private int clientTimeoutMillisecs;
    private int maxFrameSize;
    private TProcessorFactory processorFactory;

    public AbstractServerFactory(int port, TProcessor processor, int clientTimeoutMillisecs,
            int maxFrameSize) {
        this.port = port;
        this.processorFactory = new TProcessorFactory(processor);
        this.clientTimeoutMillisecs = clientTimeoutMillisecs;
        this.maxFrameSize = maxFrameSize;
    }

    protected int getPort() {
        return port;
    }

    public AbstractServerFactory setPort(int port) {
        this.port = port;
        return this;
    }

    protected int getClientTimeoutMillisecs() {
        return clientTimeoutMillisecs;
    }

    public AbstractServerFactory setClientTimeoutMillisecs(int clientTimeoutMillisecs) {
        this.clientTimeoutMillisecs = clientTimeoutMillisecs;
        return this;
    }

    protected int getMaxFrameSize() {
        return maxFrameSize;
    }

    public AbstractServerFactory setMaxFrameSize(int maxFrameSize) {
        this.maxFrameSize = maxFrameSize;
        return this;
    }

    protected TProcessorFactory getProcessorFactory() {
        return processorFactory;
    }

    public AbstractServerFactory setProcessorFactory(TProcessorFactory processorFactory) {
        this.processorFactory = processorFactory;
        return this;
    }
}
