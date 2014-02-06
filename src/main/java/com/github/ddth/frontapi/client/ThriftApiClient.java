package com.github.ddth.frontapi.client;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import com.github.ddth.frontapi.ApiResult;
import com.github.ddth.frontapi.internal.JsonUtils;
import com.github.ddth.frontapi.internal.thrift.TApi;
import com.github.ddth.frontapi.internal.thrift.TApiResult;

/**
 * {@link IApiClient}: Thrift implementation.
 * 
 * <p>
 * This API client calls APIs via Thrift.
 * </p>
 * 
 * @author Thanh Ba Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class ThriftApiClient implements IApiClient {

    private String host;
    private int port;

    public ThriftApiClient() {
    }

    public ThriftApiClient(String host, int port) {
        setHost(host);
        setPort(port);
    }

    public String getHost() {
        return host;
    }

    public ThriftApiClient setHost(String host) {
        this.host = host;
        return this;
    }

    public int getPort() {
        return port;
    }

    public ThriftApiClient setPort(int port) {
        this.port = port;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ApiResult call(String authKey, String moduleName, String apiName, Object apiInput)
            throws Exception {
        TTransport transport = new TFramedTransport(new TSocket(host, port, 5000));
        try {
            transport.open();
            TProtocol protocol = new TBinaryProtocol(transport);
            TApi.Client client = new TApi.Client(protocol);
            TApiResult tapiResult = client.callApi(authKey, moduleName, apiName,
                    JsonUtils.toJsonString(apiInput));
            try {
                return new ApiResult(tapiResult.status,
                        JsonUtils.fromJsonString(tapiResult.jsonOutput));
            } catch (Exception e) {
                return new ApiResult(500, tapiResult.jsonOutput);
            }
        } finally {
            transport.close();
        }
    }

    public static void main(String[] args) throws Exception {
        IApiClient apiClient = new ThriftApiClient("localhost", 9090);
        ApiResult apiResult = apiClient.call(null, "demo", "demo", "demo");
        System.out.println(apiResult);
    }
}
