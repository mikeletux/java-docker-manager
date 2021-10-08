package com.mikeletux.dockeradapter.httprestclient;

import java.io.IOException;
import java.lang.InterruptedException;

public interface IHttpClientInterface {
    HttpCustomResponse Get(String uri) throws IOException, InterruptedException;
    HttpCustomResponse Post(String uri, String body) throws IOException, InterruptedException;
    HttpCustomResponse Delete(String uri) throws IOException, InterruptedException;
}
