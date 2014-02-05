package com.github.ddth.frontapi.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.github.ddth.frontapi.ApiResult;
import com.github.ddth.frontapi.IApiRegistry;
import com.github.ddth.frontapi.internal.Activator;
import com.github.ddth.frontapi.internal.JsonUtils;

/**
 * A simple servlet which returns JSON data.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 */
public class ApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final static Charset UTF8 = Charset.forName("UTF-8");

    private String urlMapping = Activator.DEFAULT_REST_MAPPING;
    private IApiRegistry apiRegistry;

    private static class RequestTokens {

        public String authKey, moduleName, apiName;

        public static RequestTokens extractTokens(HttpServletRequest request, String urlMapping) {
            String requestUri = request.getRequestURI();
            requestUri = StringUtils.removeStartIgnoreCase(requestUri, urlMapping);
            requestUri = StringUtils.strip(requestUri, "/");
            String[] tokens = StringUtils.split(requestUri, '/');
            RequestTokens requestTokens = new RequestTokens();
            requestTokens.authKey = tokens.length > 0 ? tokens[0] : null;
            requestTokens.moduleName = tokens.length > 1 ? tokens[1] : null;
            requestTokens.apiName = tokens.length > 2 ? tokens[2] : null;
            return requestTokens;
        }
    }

    public ApiServlet(IApiRegistry apiRegistry, String urlMapping) {
        this.apiRegistry = apiRegistry;
        this.urlMapping = StringUtils.isBlank(urlMapping) ? Activator.DEFAULT_REST_MAPPING
                : urlMapping;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestTokens requestTokens = RequestTokens.extractTokens(request, urlMapping);
        Map<String, Object> requestParams = new HashMap<String, Object>();
        Map<?, ?> parameterMap = request.getParameterMap();
        for (Entry<?, ?> entry : parameterMap.entrySet()) {
            String key = entry.getKey().toString();
            Object value = entry.getValue();
            if (value != null) {
                if (value instanceof Object[]) {
                    Object[] arrValue = (Object[]) value;
                    if (arrValue.length < 2) {
                        requestParams.put(key, arrValue[0]);
                    } else {
                        requestParams.put(key, arrValue);
                    }
                } else {
                    requestParams.put(key, value);
                }
            }
        }
        ApiResult apiResult = apiRegistry.callApi(requestTokens.moduleName, requestTokens.apiName,
                requestTokens.authKey, requestParams);
        jsonResponse(response, apiResult);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestTokens requestTokens = RequestTokens.extractTokens(request, urlMapping);
        InputStream is = request.getInputStream();
        Object requestParams = null;
        try {
            // TODO buffer-over-flow?
            // TODO slow client?
            String strApiParams = IOUtils.toString(is, UTF8);
            requestParams = JsonUtils.fromJsonString(strApiParams);
        } finally {
            IOUtils.closeQuietly(is);
        }
        ApiResult apiResult = apiRegistry.callApi(requestTokens.moduleName, requestTokens.apiName,
                requestTokens.authKey, requestParams);
        jsonResponse(response, apiResult);
    }

    private void jsonResponse(HttpServletResponse response, ApiResult apiResult) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(JsonUtils.toJsonString(apiResult));
        // Map<String, Object> result = new HashMap<String, Object>();
        // result.put("status", apiResult.getStatus());
        // result.put("output", apiResult.getOutput());
        // response.getWriter().write(JsonUtils.toJsonString(result));
    }
}
