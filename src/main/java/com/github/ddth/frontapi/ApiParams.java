package com.github.ddth.frontapi;

/**
 * Encapsulates API's input parameters.
 * 
 * @author Thanh Ba Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class ApiParams {
    private Object params;

    public ApiParams(Object params) {
        this.params = params;
    }

    public Object getParams() {
        return params;
    }
}
