package com.github.ddth.frontapi.internal.thrift;

import org.apache.thrift.TException;
import org.apache.thrift.server.TServer;

/**
 * Factory to create {@link TServer} instances.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public interface IServerFactory {
    public TServer createServer() throws TException;
}
