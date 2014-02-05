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
     * @param module
     *            name of the module that provides API
     * @param apiName
     *            name of the API
     * @param authKey
     *            authentication key to call API
     * @param params
     *            API's input parameters
     * @return
     */
    public ApiResult callApi(String module, String apiName, String authKey, Object params);

    /**
     * Bundle calls this method to register its APIs
     * 
     * @param module
     * @param apiName
     * @param api
     */
    public void register(String module, String apiName, IApi api);

    /**
     * Bundle calls this method to unregister all its APIs.
     * 
     * @param module
     */
    public void unregister(String module);
}
