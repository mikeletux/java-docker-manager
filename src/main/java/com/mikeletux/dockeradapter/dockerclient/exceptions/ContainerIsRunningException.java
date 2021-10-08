package com.mikeletux.dockeradapter.dockerclient.exceptions;

public class ContainerIsRunningException extends Exception{
    public ContainerIsRunningException(String errorMessage) {
        super(errorMessage);
    }
}
