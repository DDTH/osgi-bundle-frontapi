package com.github.ddth.frontapi.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.github.ddth.frontapi.ApiParams;
import com.github.ddth.frontapi.ApiResult;
import com.github.ddth.frontapi.IApi;
import com.github.ddth.frontapi.IApiRegistry;

public class ApiRegistry implements IApiRegistry {

    private Map<String, Map<String, IApi>> registeredApis = new ConcurrentHashMap<String, Map<String, IApi>>();

    /**
     * Init method.
     */
    public void init() {
        // EMPTY
    }

    /**
     * Destroy method.
     */
    public void destroy() {
        // EMPTY
    }

    /**
     * Looks up an API.
     * 
     * @param moduleName
     * @param apiName
     * @return
     */
    protected IApi lookup(String moduleName, String apiName) {
        Map<String, IApi> moduleApis = moduleName != null ? registeredApis.get(moduleName) : null;
        if (moduleApis == null) {
            return null;
        }
        IApi api = apiName != null ? moduleApis.get(apiName) : null;
        return api != null ? api : moduleApis.get(WILDCARD_API);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ApiResult callApi(String moduleName, String apiName, String authKey, Object params) {
        IApi api = lookup(moduleName, apiName);
        if (api == null) {
            return new ApiResult(IApi.STATUS_NOT_FOUND, "Api [" + moduleName + "/" + apiName
                    + "] not found!");
        }
        ApiParams apiParams = new ApiParams(params);
        try {
            Object output = api.call(apiParams);
            return new ApiResult(IApi.STATUS_OK, output);
        } catch (Exception e) {
            return new ApiResult(IApi.STATUS_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    synchronized public void register(String module, String apiName, IApi api) {
        Map<String, IApi> moduleApis = registeredApis.get(module);
        if (moduleApis == null) {
            moduleApis = new ConcurrentHashMap<String, IApi>();
            registeredApis.put(module, moduleApis);
        }
        moduleApis.put(apiName, api);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unregister(String module) {
        registeredApis.remove(module);
    }
}
