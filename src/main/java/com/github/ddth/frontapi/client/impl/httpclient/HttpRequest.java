package com.github.ddth.frontapi.client.impl.httpclient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to make http requests (POST and GET).
 * 
 * @author Thanh Ba Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class HttpRequest {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpRequest.class);

    private String url;
    private Map<String, Object> headers;
    private Map<String, Object> params;
    private int connectionTimeout = 3000, readTimeout = 10000;
    private String username, password;
    private String encoding = "utf-8";
    private String userAgent = "Mozilla/5.0 (Windows NT 5.1; rv:5.0) Gecko/20100101 Firefox/5.0";
    private int fetchSizeLimit = 0;

    static {
        HttpURLConnection.setFollowRedirects(true);
    }

    public HttpRequest() {
    }

    public HttpRequest(String url) {
        this.url = url;
    }

    public HttpRequest(String url, Map<String, Object> params) {
        this.url = url;
        this.params = params;
    }

    public HttpRequest(String url, Map<String, Object> params, Map<String, Object> headers) {
        this.url = url;
        this.params = params;
        this.headers = headers;
    }

    private static String encodeParams(Map<String, Object> data) {
        StringBuilder res = new StringBuilder();
        try {
            for (String key : data.keySet()) {
                res.append("&");
                res.append(URLEncoder.encode(key, "UTF-8"));
                res.append("=");
                res.append(URLEncoder.encode(data.get(key).toString(), "UTF-8"));
            }
        } catch (UnsupportedEncodingException ex) {
            LOGGER.error(ex.getMessage(), ex);
            return null;
        }
        if (!data.isEmpty()) {
            res.deleteCharAt(0);
        }
        return res.toString();
    }

    /*--------------------------------------------------------------------------------*/
    /**
     * Sets the url.
     * 
     * @param url
     * @return
     */
    public HttpRequest url(String url) {
        this.url = url;
        return this;
    }

    /**
     * Sets a request parameter.
     * 
     * @param name
     * @param value
     * @return
     */
    public HttpRequest param(String name, Object value) {
        if (params == null) {
            params = new HashMap<String, Object>();
        }
        params.put(name, value);
        return this;
    }

    /**
     * Sets a request header.
     * 
     * @param name
     * @param value
     * @return
     */
    public HttpRequest header(String name, Object value) {
        if (headers == null) {
            headers = new HashMap<String, Object>();
        }
        headers.put(name, value);
        return this;
    }

    /**
     * Sets connection timeout in milliseconds.
     * 
     * @param connectionTimeout
     * @return
     */
    public HttpRequest connectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        return this;
    }

    /**
     * Sets read timeout in milliseconds.
     * 
     * @param readTimeout
     * @return
     */
    public HttpRequest readTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    /**
     * Sets username and password for basic authentication.
     * 
     * @param username
     * @param password
     * @return
     */
    public HttpRequest basicAuthentication(String username, String password) {
        this.username = username;
        this.password = password;
        return this;
    }

    /**
     * Sets encoding.
     * 
     * @param encoding
     * @return
     */
    public HttpRequest encoding(String encoding) {
        this.encoding = encoding;
        return this;
    }

    /**
     * Sets user-agent.
     * 
     * @param userAgent
     * @return
     */
    public HttpRequest userAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    /**
     * Sets maximum number of bytes to read.
     * 
     * @param fetchSizeLimit
     * @return
     */
    public HttpRequest fetchSizeLimit(int fetchSizeLimit) {
        this.fetchSizeLimit = fetchSizeLimit;
        return this;
    }

    /*--------------------------------------------------------------------------------*/
    private URLConnection _doGet() throws MalformedURLException, IOException {
        String requestUrl = url;
        if (params != null && params.size() > 0) {
            if (requestUrl.contains("?")) {
                // url already contains param
                requestUrl += "&" + encodeParams(params);
            } else {
                // url doesn't contain param
                requestUrl += "?" + encodeParams(params);
            }
        }

        URLConnection conn = new URL(requestUrl).openConnection();
        conn.setConnectTimeout(connectionTimeout);
        conn.setReadTimeout(readTimeout);
        conn.setRequestProperty("User-Agent", userAgent);
        if (!StringUtils.isBlank(username)) {
            String auth = username + ":" + password;
            String encodedAuth = Base64.encodeBase64String(auth.getBytes(encoding));
            conn.setRequestProperty("Authorization", "Basic " + encodedAuth);
        }
        if (headers != null) {
            for (Entry<String, Object> entry : headers.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (key != null && value != null) {
                    conn.addRequestProperty(key, value.toString());
                }
            }
        }
        return conn;
    }

    public HttpResponse doGetToFile(File outFile) {
        InputStream is = null;
        int responseCode = 0;
        try {
            URLConnection conn = _doGet();
            responseCode = conn.getHeaderFieldInt("Status", 0);
            if (responseCode == 0) {
                String nullHeader = conn.getHeaderField(null);
                if (nullHeader != null) {
                    final Pattern PATTERN = Pattern.compile("(\\d\\d\\d)");
                    final Matcher MATCHER = PATTERN.matcher(nullHeader);
                    if (MATCHER.find()) {
                        responseCode = Integer.parseInt(MATCHER.group(1));
                    }
                }
            }
            Map<String, List<String>> responseHeaders = conn.getHeaderFields();

            // get the response
            is = conn.getInputStream();
            FileOutputStream fout = new FileOutputStream(outFile);
            try {
                IOUtils.copy(is, fout);
            } finally {
                IOUtils.closeQuietly(fout);
            }
            return new HttpResponse(responseCode, outFile, responseHeaders);
        } catch (Exception ex) {
            LOGGER.error("Cannot GET from [" + url + "]!", ex);
            return new HttpResponse(responseCode != 0 ? responseCode : 500,
                    ExceptionUtils.getStackTrace(ex), null);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    public HttpResponse doGet() {
        BufferedReader in = null;
        int responseCode = 0;
        try {
            URLConnection conn = _doGet();
            responseCode = conn.getHeaderFieldInt("Status", 0);
            if (responseCode == 0) {
                String nullHeader = conn.getHeaderField(null);
                if (nullHeader != null) {
                    final Pattern PATTERN = Pattern.compile("(\\d\\d\\d)");
                    final Matcher MATCHER = PATTERN.matcher(nullHeader);
                    if (MATCHER.find()) {
                        responseCode = Integer.parseInt(MATCHER.group(1));
                    }
                }
            }
            Map<String, List<String>> responseHeaders = conn.getHeaderFields();

            // get the response
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), encoding));
            char[] cbuf = new char[fetchSizeLimit > 0 ? Math.min(1024, fetchSizeLimit) : 1024];
            StringBuilder sb = new StringBuilder();
            int read = in.read(cbuf);
            while (read != -1) {
                sb.append(Arrays.copyOf(cbuf, read));
                if (fetchSizeLimit > 0 && sb.length() >= fetchSizeLimit) {
                    break;
                }
                read = in.read(cbuf);
            }
            return new HttpResponse(responseCode, sb.toString(), responseHeaders);
        } catch (Exception ex) {
            LOGGER.error("Cannot GET from [" + url + "]!", ex);
            return new HttpResponse(responseCode != 0 ? responseCode : 500,
                    ExceptionUtils.getStackTrace(ex), null);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    public HttpResponse doPost() {
        return doPost(params != null ? encodeParams(params) : null);
    }

    public HttpResponse doPost(String data) {
        OutputStreamWriter out = null;
        BufferedReader in = null;
        int responseCode = 0;
        try {
            URLConnection conn = new URL(url).openConnection();
            conn.setConnectTimeout(connectionTimeout);
            conn.setReadTimeout(readTimeout);
            conn.setRequestProperty("User-Agent", userAgent);
            // conn.setRequestProperty("Accept", "*/*");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("accept",
                    "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            if (!StringUtils.isBlank(username)) {
                String auth = username + ":" + password;
                String encodedAuth = Base64.encodeBase64String(auth.getBytes(encoding));
                conn.setRequestProperty("Authorization", "Basic " + encodedAuth);
            }
            // additional headers
            if (headers != null) {
                for (Entry<String, Object> entry : headers.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    if (key != null && value != null) {
                        conn.addRequestProperty(key, value.toString());
                    }
                }
            }

            // send data
            conn.setDoOutput(true);
            out = new OutputStreamWriter(conn.getOutputStream(), encoding);
            out.write(data);
            out.flush();

            // get the response headers
            responseCode = conn.getHeaderFieldInt("Status", 0);
            if (responseCode == 0) {
                String nullHeader = conn.getHeaderField(null);
                if (nullHeader != null) {
                    final Pattern PATTERN = Pattern.compile("(\\d\\d\\d)");
                    final Matcher MATCHER = PATTERN.matcher(nullHeader);
                    if (MATCHER.find()) {
                        responseCode = Integer.parseInt(MATCHER.group(1));
                    }
                }
            }
            Map<String, List<String>> responseHeaders = conn.getHeaderFields();

            // get the response
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), encoding));
            char[] cbuf = new char[fetchSizeLimit > 0 ? Math.min(1024, fetchSizeLimit) : 1024];
            StringBuilder sb = new StringBuilder();
            int read = in.read(cbuf);
            while (read != -1) {
                sb.append(Arrays.copyOf(cbuf, read));
                if (fetchSizeLimit > 0 && sb.length() >= fetchSizeLimit) {
                    break;
                }
                read = in.read(cbuf);
            }

            return new HttpResponse(responseCode, sb.toString(), responseHeaders);
        } catch (Exception ex) {
            LOGGER.error("Cannot POST to [" + url + "]!", ex);
            return new HttpResponse(responseCode != 0 ? responseCode : 500,
                    ExceptionUtils.getStackTrace(ex), null);
        } finally {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(in);
        }
    }
}