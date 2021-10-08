package com.mikeletux.dockeradapter.dockerclient.exceptions;

public class ContainerDoesNotExistException extends Exception{
    public ContainerDoesNotExistException(String errorMessage) {
        super(errorMessage);
    }
}
