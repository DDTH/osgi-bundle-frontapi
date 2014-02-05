package com.github.ddth.frontapi;

/**
 * Bundle registers/unregisters its APIs via this interface.
 * 
 * @author Thanh Ba Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public interface IApiRegistry {

    public final static String WILDCARD_API = "*";

    /**
     * Calls an API.
     * 
     * @param moduleName
     *            name of the module that provides API
     * @param apiName
     *            name of the API
     * @param authKey
     *            authentication key to call API
     * @param params
     *            API's input parameters
     * @return
     */
    public ApiResult callApi(String moduleName, String apiName, String authKey, Object params);

    /**
     * Bundle calls this method to register its APIs
     * 
     * @param moduleName
     * @param apiName
     * @param api
     */
    public void register(String moduleName, String apiName, IApi api);

    /**
     * Bundle calls this method to unregister all its APIs.
     * 
     * @param moduleName
     */
    public void unregister(String moduleName);
}
