package com.github.ddth.frontapi.client.impl.httpclient;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

/**
 * Encapsulates response from a http request.
 * 
 * @author Thanh Ba Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class HttpResponse {
    private int statucCode = 200;
    private String content;
    private Map<String, List<String>> headers;
    private File file;

    public HttpResponse() {
    }

    public HttpResponse(int statusCode, String content, Map<String, List<String>> headers) {
        this.statucCode = statusCode;
        this.content = content;
        this.headers = headers;
    }

    public HttpResponse(int statusCode, File file, Map<String, List<String>> headers) {
        this.statucCode = statusCode;
        this.file = file;
        this.headers = headers;
    }

    public int statusCode() {
        return statucCode;
    }

    public HttpResponse statusCode(int statusCode) {
        this.statucCode = statusCode;
        return this;
    }

    public String content() {
        return content;
    }

    public HttpResponse content(String content) {
        this.content = content;
        return this;
    }

    public File file() {
        return file;
    }

    public HttpResponse file(File file) {
        this.file = file;
        return this;
    }

    public List<String> header(String name) {
        if (headers != null) {
            for (Entry<String, List<String>> entry : headers.entrySet()) {
                if (StringUtils.equalsIgnoreCase(entry.getKey(), name)) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    public Map<String, List<String>> headers() {
        return headers;
    }

    public HttpResponse headers(Map<String, List<String>> headers) {
        this.headers = headers;
        return this;
    }
}