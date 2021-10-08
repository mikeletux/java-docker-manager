package com.mikeletux.dockeradapter.dockerclient.exceptions;

public class ImageDoesNotExistException extends Exception{
    public ImageDoesNotExistException(String errorMessage) {
        super(errorMessage);
    }
}
