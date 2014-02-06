package com.github.ddth.frontapi.internal.thrift;

import org.apache.thrift.TProcessor;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TTransportException;

/**
 * Factory that creates threaded {@link TServer}.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class ThreadedServerFactory extends AbstractServerFactory {

    public ThreadedServerFactory(int port, TProcessor processor, int clientTimeoutMillisecs,
            int maxFrameSize) {
        super(port, processor, clientTimeoutMillisecs, maxFrameSize);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TServer createServer() throws TTransportException {
        TServer server = ThriftUtils.createThreadedServer(getProcessorFactory(), getPort(),
                getClientTimeoutMillisecs(), getMaxFrameSize());
        return server;
    }
}
