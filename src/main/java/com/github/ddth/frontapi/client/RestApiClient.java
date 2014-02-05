package com.github.ddth.frontapi.client;

import org.apache.commons.lang3.StringUtils;

import com.github.ddth.frontapi.ApiResult;
import com.github.ddth.frontapi.IApi;
import com.github.ddth.frontapi.client.impl.httpclient.HttpRequest;
import com.github.ddth.frontapi.client.impl.httpclient.HttpResponse;
import com.github.ddth.frontapi.internal.JsonUtils;

/**
 * {@link IApiClient}: REST implementation.
 * 
 * <p>
 * This API client calls REST APIs using http-POST method.
 * </p>
 * 
 * @author Thanh Ba Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class RestApiClient implements IApiClient {

    private String endpoint;

    public RestApiClient() {
    }

    public RestApiClient(String endpoint) {
        setEndpoint(endpoint);
    }

    public String getEndpoint() {
        return endpoint;
    }

    public RestApiClient setEndpoint(String endpoint) {
        this.endpoint = StringUtils.strip(endpoint, "/");
        return this;
    }

    private String buildUrl(String authKey, String moduleName, String apiName) {
        StringBuilder sb = new StringBuilder(endpoint);
        sb.append("/").append(!StringUtils.isBlank(authKey) ? authKey : IApi.OPEN_AUTHKEY);
        if (!StringUtils.isBlank(moduleName)) {
            sb.append("/").append(moduleName);
            if (!StringUtils.isBlank(apiName)) {
                sb.append("/").append(apiName);
            }
        }
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ApiResult call(String authKey, String moduleName, String apiName, Object apiInput)
            throws Exception {
        String url = buildUrl(authKey, moduleName, apiName);
        String apiParams = JsonUtils.toJsonString(apiInput);
        HttpRequest httpRequest = new HttpRequest(url);
        HttpResponse httpResponse = httpRequest.doPost(apiParams);
        int statusCode = httpResponse.statusCode();
        if (statusCode != 200) {
            return new ApiResult(statusCode, httpResponse.content());
        } else {
            try {
                ApiResult apiResult = JsonUtils.fromJsonString(httpResponse.content(),
                        ApiResult.class);
                return apiResult;
            } catch (Exception e) {
                return new ApiResult(500, httpResponse.content());
            }
        }
    }

    public static void main(String[] args) throws Exception {
        IApiClient apiClient = new RestApiClient("http://localhost:8080/api/");
        ApiResult apiResult = apiClient.call(null, "demo", "demo", "demo");
        System.out.println(apiResult);
    }
}
