package com.caio.models.tests;

public class FailureDetail {

    private String type;
    private String message;
    private String stackTrace;

    public FailureDetail(String type, String message, String stackTrace) {
        this.type = type;
        this.message = message;
        this.stackTrace = stackTrace;
    }

    public boolean equals(FailureDetail failureDetail) {

        return failureDetail.getType().equals(type) && failureDetail.getMessage().equals(message)
                && failureDetail.getStackTrace().equals(stackTrace);

    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public String getStackTrace() {
        return stackTrace;
    }

}
