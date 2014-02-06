package com.github.ddth.frontapi.internal.thrift;

import org.apache.thrift.TProcessor;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TTransportException;

/**
 * Factory that creates threaded-selector {@link TServer}.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class ThreadedSelectorServerFactory extends AbstractServerFactory {
    private long maxReadBufferSize;

    public ThreadedSelectorServerFactory(int port, TProcessor processor,
            int clientTimeoutMillisecs, int maxFrameSize, long maxReadBufferSize) {
        super(port, processor, clientTimeoutMillisecs, maxFrameSize);
        this.maxReadBufferSize = maxReadBufferSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TServer createServer() throws TTransportException {
        TServer server = ThriftUtils.createThreadedSelectorServer(getProcessorFactory(), getPort(),
                getClientTimeoutMillisecs(), getMaxFrameSize(), maxReadBufferSize);
        return server;
    }
}
