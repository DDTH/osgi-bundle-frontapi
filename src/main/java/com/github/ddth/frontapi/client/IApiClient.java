package com.github.ddth.frontapi.client;

import com.github.ddth.frontapi.ApiResult;

/**
 * Represents an API client.
 * 
 * @author Thanh Ba Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public interface IApiClient {
    /**
     * Makes the API call.
     * 
     * @param authKey
     * @param moduleName
     * @param apiName
     * @param apiInput
     * @return
     * @throws Exception
     */
    public ApiResult call(String authKey, String moduleName, String apiName, Object apiInput)
            throws Exception;
}
