package com.github.ddth.frontapi.internal.thrift;

import org.apache.thrift.TException;

import com.github.ddth.frontapi.ApiResult;
import com.github.ddth.frontapi.IApiRegistry;
import com.github.ddth.frontapi.internal.JsonUtils;

public class JsonApiHandler implements TApi.Iface {

    private IApiRegistry apiRegistry;

    public JsonApiHandler(IApiRegistry apiRegistry) {
        this.apiRegistry = apiRegistry;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TApiResult callApi(String authKey, String moduleName, String apiName, String jsonInput)
            throws TException {
        Object apiParams = JsonUtils.fromJsonString(jsonInput);
        ApiResult apiResult = apiRegistry.callApi(moduleName, apiName, authKey, apiParams);
        TApiResult result = new TApiResult(apiResult.getStatus(), JsonUtils.toJsonString(apiResult
                .getOutput()));
        return result;
    }
}
