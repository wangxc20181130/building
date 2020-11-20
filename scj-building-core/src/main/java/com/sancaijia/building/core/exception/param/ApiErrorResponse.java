package com.sancaijia.building.core.exception.param;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ApiErrorResponse<T> {
    @JsonProperty(
            value = "code",
            index = 1
    )
    private String code;
    @JsonProperty(
            index = 2
    )
    private String exception;
    @JsonProperty(
            index = 3
    )
    private String message;
    @JsonProperty(
            index = 4
    )
    private String path;
    @JsonProperty(
            index = 5
    )
    private String timestamp;
    @JsonProperty(
            index = 6
    )
    private T errorData;

    public ApiErrorResponse() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public T getErrorData() {
        return errorData;
    }

    public void setErrorData(T errorData) {
        this.errorData = errorData;
    }
}
