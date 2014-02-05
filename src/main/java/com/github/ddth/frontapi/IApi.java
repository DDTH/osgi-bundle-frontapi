package com.github.ddth.frontapi;

/**
 * Represents an API.
 * 
 * @author Thanh Ba Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public interface IApi {

    public final static String OPEN_AUTHKEY = "-";

    public final static int STATUS_OK = 200;
    public final static int STATUS_FORBIDDEN = 403;
    public final static int STATUS_NOT_FOUND = 404;
    public final static int STATUS_SERVER_ERROR = 500;

    /**
     * Calls the API.
     * 
     * @param params
     * @return
     * @throws Exception
     */
    public Object call(ApiParams params) throws Exception;
}
