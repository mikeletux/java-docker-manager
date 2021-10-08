package com.mikeletux.dockeradapter.httprestclient;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;


public class HttpQuickClient implements IHttpClientInterface{
    private HttpClient httpClient;

    public HttpQuickClient(){
        httpClient = HttpClient.newHttpClient();
    }

    public HttpCustomResponse Get(String uri) throws IOException, InterruptedException{
        HttpCustomResponse response;
        HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(uri))
                    .header("Accept", "application/json")
                    .GET()
                    .version(HttpClient.Version.HTTP_1_1)
                    .build();
        
  
        HttpResponse<String> httpResponse = httpClient.send(request, BodyHandlers.ofString());
        response = new HttpCustomResponse(httpResponse.statusCode(), httpResponse.body().toString());

        return response;
    }

    @Override
    public HttpCustomResponse Post(String uri, String body) throws IOException, InterruptedException{
        HttpCustomResponse response;
        HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(uri))
        .header("Content-Type", "application/json")
        .POST(BodyPublishers.ofString(body))
        .version(HttpClient.Version.HTTP_1_1)
        .build();

        HttpResponse<String> httpResponse = httpClient.send(request, BodyHandlers.ofString());
        response = new HttpCustomResponse(httpResponse.statusCode(), httpResponse.body().toString());

        return response;
    }

    @Override
    public HttpCustomResponse Delete(String uri) throws IOException, InterruptedException {
        HttpCustomResponse response;
        HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(uri))
        .header("Content-Type", "application/json")
        .DELETE()
        .version(HttpClient.Version.HTTP_1_1)
        .build();

        HttpResponse<String> httpResponse = httpClient.send(request, BodyHandlers.ofString());
        response = new HttpCustomResponse(httpResponse.statusCode(), httpResponse.body().toString());

        return response;
    }   
}
