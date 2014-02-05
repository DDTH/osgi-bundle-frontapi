package com.github.ddth.frontapi;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Encapsulates result of an API call.
 * 
 * @author Thanh Ba Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class ApiResult {

    public final static String FIELD_STATUS = "status", FIELD_OUTPUT = "output";

    @JsonProperty
    private int status;

    @JsonProperty
    private Object output;

    public ApiResult() {
    }

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
