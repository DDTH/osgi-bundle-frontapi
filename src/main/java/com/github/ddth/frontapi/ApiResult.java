package com.github.ddth.frontapi;

/**
 * Encapsulates result of an API call.
 * 
 * @author Thanh Ba Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class ApiResult {
    private int status;
    private Object output;

    public ApiResult(int status, Object output) {
        this.status = status;
        this.output = output;
    }

    public int getStatus() {
        return status;
    }

    public Object getOutput() {
        return output;
    }

}
