package com.leopaul29.bento.config;

import java.util.Map;

public class ErrorResponse {
    private String timestamp;
    private int status;
    private String error;
    private String message;
    private String path; // optionnel: rempli via un filter ou HandlerMethodArgumentResolver
    private Map<String, ?> details;

    public ErrorResponse() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final ErrorResponse r = new ErrorResponse();
        public Builder timestamp(String v) { r.timestamp = v; return this; }
        public Builder status(int v) { r.status = v; return this; }
        public Builder error(String v) { r.error = v; return this; }
        public Builder message(String v) { r.message = v; return this; }
        public Builder path(String v) { r.path = v; return this; }
        public Builder details(Map<String, ?> v) { r.details = v; return this; }
        public ErrorResponse build() { return r; }
    }

    public String getTimestamp() { return timestamp; }
    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getMessage() { return message; }
    public String getPath() { return path; }
    public Map<String, ?> getDetails() { return details; }
}