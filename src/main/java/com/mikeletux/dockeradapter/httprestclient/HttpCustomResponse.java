package com.mikeletux.dockeradapter.httprestclient;

public class HttpCustomResponse {
    private int statusCode;
    private String body;

    public HttpCustomResponse(int statusCode, String body){
        this.statusCode = statusCode;
        this.body = body;
    }

    public String getBody() {
        return this.body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
