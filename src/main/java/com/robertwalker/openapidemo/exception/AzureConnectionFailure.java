package com.robertwalker.openapidemo.exception;

import lombok.Getter;

@Getter
public class AzureConnectionFailure extends RuntimeException {
    public static final String AZURE_CONNECTION_FAILURE = "AZURE_CONNECTION_FAILURE";

    private final String code;

    public AzureConnectionFailure(String message, Throwable cause) {
        super(message, cause);
        this.code = AZURE_CONNECTION_FAILURE;
    }
}
